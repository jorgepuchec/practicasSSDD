/**
 *
 */
package es.um.sisdist.videofaces.backend.Service.impl;

import java.util.Optional;

import java.util.logging.Logger;
import es.um.sisdist.videofaces.models.UserDTO;
import es.um.sisdist.videofaces.models.VideoDTO;
import es.um.sisdist.videofaces.models.FaceDTO;
import es.um.sisdist.videofaces.models.UserDTOUtils;
import es.um.sisdist.videofaces.models.VideoDTOUtils;
import es.um.sisdist.videofaces.models.FaceDTOUtils;
import es.um.sisdist.videofaces.backend.dao.DAOFactoryImpl;
import es.um.sisdist.videofaces.backend.dao.IDAOFactory;
import es.um.sisdist.videofaces.backend.dao.models.User;
import es.um.sisdist.videofaces.backend.dao.models.Video;
import es.um.sisdist.videofaces.backend.dao.models.Face;
import es.um.sisdist.videofaces.backend.dao.models.utils.UserUtils;
import es.um.sisdist.videofaces.backend.dao.user.IUserDAO;
import es.um.sisdist.videofaces.backend.dao.video.IVideoDAO;
import es.um.sisdist.videofaces.backend.dao.face.IFaceDAO;
import es.um.sisdist.videofaces.backend.grpc.GrpcServiceGrpc;
import es.um.sisdist.videofaces.backend.grpc.VideoAvailability;
import es.um.sisdist.videofaces.backend.grpc.VideoSpec;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import java.time.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.*;

/**
 * @author dsevilla
 *
 */
public class AppLogicImpl
{
    IDAOFactory daoFactory;
    IUserDAO dao;
    IVideoDAO daoVideo;
    IFaceDAO daoFace;

    private static final Logger logger = Logger.getLogger(AppLogicImpl.class.getName());

    private final ManagedChannel channel;
    private final GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub;
    private final GrpcServiceGrpc.GrpcServiceStub asyncStub;

    static AppLogicImpl instance = new AppLogicImpl();

    private AppLogicImpl()
    {
        daoFactory = new DAOFactoryImpl();

        dao = daoFactory.createSQLUserDAO();
        daoVideo = daoFactory.createSQLVideoDAO();
        daoFace = daoFactory.createSQLFaceDAO();


        Optional<String> grpcServerName = Optional.ofNullable(System.getenv("GRPC_SERVER"));
        Optional<String> grpcServerPort = Optional.ofNullable(System.getenv("GRPC_SERVER_PORT"));

        channel = ManagedChannelBuilder
                .forAddress(grpcServerName.orElse("localhost"), Integer.parseInt(grpcServerPort.orElse("50051")))
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS
                // to avoid
                // needing certificates.
                .usePlaintext().build();
        blockingStub = GrpcServiceGrpc.newBlockingStub(channel);
        asyncStub = GrpcServiceGrpc.newStub(channel);
    }

    public static AppLogicImpl getInstance()
    {
        return instance;
    }

    public Optional<User> getUserByEmail(String userId)
    {
        Optional<User> u = dao.getUserByEmail(userId);
        return u;
    }

    public Optional<User> getUserById(String userId)
    {
        return dao.getUserById(userId);
    }

    public boolean isVideoReady(String videoId)
    {
        // Test de grpc, puede hacerse con la BD
        VideoAvailability available = blockingStub.isVideoReady(VideoSpec.newBuilder().setId(videoId).build());
        return available.getAvailable();
    }

    public void processVideo(String videoId)
    {

        try{
            final CountDownLatch finishLatch = new CountDownLatch(1);
            StreamObserver<Empty> soEmpty = new StreamObserver<Empty>(){
                @Override
                public void onNext(Empty value){

                }
                @Override
                public void onError(Throwable t) {
                    finishLatch.countDown();
                }
                @Override
                public void onCompleted(){
                    finishLatch.countDown();
                }
            };
            StreamObserver<VideoSpec> so = asyncStub.processVideo(soEmpty);
            so.onNext(VideoSpec.newBuilder().setId(videoId).build());  
            so.onCompleted();

            //esperar respuesta
            if(finishLatch.await(1, TimeUnit.SECONDS)){
                logger.info("Received response.");
            } else {
                logger.info("Not received response.");
            }

        } catch (StatusRuntimeException e){

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
    }

    // El frontend, a través del formulario de login,
    // envía el usuario y pass, que se convierte a un DTO. De ahí
    // obtenemos la consulta a la base de datos, que nos retornará,
    // si procede,
    public Optional<User> checkLogin(String email, String pass)
    {
        Optional<User> u = dao.getUserByEmail(email);

        if (u.isPresent())
        {
            String hashed_pass = UserUtils.md5pass(pass);
            if (0 == hashed_pass.compareTo(u.get().getPassword_hash()))
                return u;
        }

        return Optional.empty();
    }

    public Optional<User> registerUser(UserDTO udto){

        Optional<User> u = dao.getUserByEmail(udto.getEmail());
        if (u.isPresent()){
            return Optional.empty();

        } else {
        	String token = udto.getName()+udto.getEmail();
            User newUser = new User(udto.getEmail(), UserUtils.md5pass(udto.getPassword()), udto.getName(), UserUtils.md5pass(token), 0);
            return dao.saveUser(newUser);

        }
    }

    public void userVisited(String userEmail){

        dao.addUserVisits(userEmail);

    }

    public void userVisited(String userId, String aux){

        dao.addUserVisitsId(userId);

    }

    public Optional<Video> saveVideo(Video video){
        
        return daoVideo.saveVideo(video);

    }

    public LinkedList<Video> getVideosById(String userId){

        return daoVideo.getVideosById(userId);
        //return new LinkedList<Video>();

    }

    public LinkedList<Face> getFacesByVideoId(String videoId){
        return daoFace.getFacesByVideoId(videoId);
    }

    public boolean checkToken(String authtoken, String url, String userToken){
        LocalDate lt = LocalDate.now();
        String sinCode = url+lt.toString()+userToken;
        String cod = UserUtils.md5pass(sinCode);
        if(authtoken.equals(cod)){
            return true;
        } else{
            return false;
        }
    }


}
