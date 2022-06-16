package es.um.sisdist.videofaces.backend.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import es.um.sisdist.videofaces.models.UserDTO;
import es.um.sisdist.videofaces.models.UserDTOUtils;
import es.um.sisdist.videofaces.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.videofaces.backend.dao.models.Video;
import es.um.sisdist.videofaces.models.VideoDTO;
import es.um.sisdist.videofaces.models.VideoDTOUtils;
import es.um.sisdist.videofaces.models.FaceDTO;
import es.um.sisdist.videofaces.models.FaceDTOUtils;
import es.um.sisdist.videofaces.backend.dao.models.Face;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.net.URI;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.PathParam;
//import org.apache.http.HttpHeaders;
//import org.springframework.http.HttpHeaders;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response.Status;
import java.util.*;
import es.um.sisdist.videofaces.backend.dao.models.Face;
import java.time.*;
import es.um.sisdist.videofaces.backend.dao.models.utils.UserUtils;

@Path("/users/{userId}/video")
public class UploadVideoEndpoint
{
    private AppLogicImpl impl = AppLogicImpl.getInstance();
    // GrpcServiceImpl implGrpc = GrpcServiceGrpc.getInstance();

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadVideo(@FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileMetaData,
            @PathParam("userId") String userId, @Context HttpHeaders headers) throws Exception
    {

        LocalDate lt = LocalDate.now();

        String url = "http://localhost:8080/rest/users/"+userId+"/video";
        //String authTokenrcv = headers.getHeaderString("Auth-Token");
        String userTokenrcv = headers.getHeaderString("User-Token");
        String authTokenFront = UserUtils.md5pass(url+lt.toString()+userTokenrcv);



        UserDTO user = UserDTOUtils.toDTO(impl.getUserById(userId).orElse(null));

        String userToken = user.getToken();

        if (user == null){

            return Response.status(Status.FORBIDDEN).build();

        }else if(!impl.checkToken(authTokenFront, url, userToken)){

            return Response.status(Status.UNAUTHORIZED).build();

        } else{

                LocalDate date = LocalDate.now();
                String fecha = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                Video v = new Video(userId, fecha, Video.PROCESS_STATUS.PROCESSING, fileMetaData.getFileName(), fileInputStream);

                VideoDTO vDTO = VideoDTOUtils.toDTO(impl.saveVideo(v).orElse(null));

                if(vDTO != null){
                    impl.processVideo(vDTO.getVid());
                    return Response.created(new URI(vDTO.getVid())).build();

                } else {

                    return Response.status(Status.FORBIDDEN).build();
                    
                }


        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response video(@PathParam("userId") String userId, @Context HttpHeaders headers){
        
        String url = "http://localhost:8080/rest/users/"+userId+"/video";
        LocalDate lt = LocalDate.now();
        String userTokenrcv = headers.getHeaderString("User-Token");
        String authTokenFront = UserUtils.md5pass(url+lt.toString()+userTokenrcv);



        LinkedList<Video> vs = impl.getVideosById(userId);
        System.out.println("HE PRINTEADO "+ vs.get(0).getFilename());
        LinkedList<VideoDTO> vddto = new LinkedList<VideoDTO>();
        for(Video v : vs){
            vddto.add(VideoDTOUtils.toDTO(v));
        }

        String userToken = UserDTOUtils.toDTO(impl.getUserById(userId).orElse(null)).getToken();

        if(!vs.isEmpty()){


            return Response.status(Response.Status.OK).entity(vddto).type(MediaType.APPLICATION_JSON).build();

        } else if(!impl.checkToken(authTokenFront, url, userToken)){
            return Response.status(Status.UNAUTHORIZED).build();
        }
            else {
                return Response.status(Status.FORBIDDEN).build();
            }

    }

    @GET
    @Path("/{videoid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response face(@PathParam("userId") String userId ,@PathParam("videoid") String videoId, @Context HttpHeaders headers) throws java.io.IOException{

        String url = "http://localhost:8080/rest/users/"+userId+"/video/"+videoId;
        LocalDate lt = LocalDate.now();
        String userTokenrcv = headers.getHeaderString("User-Token");
        String authTokenFront = UserUtils.md5pass(url+lt.toString()+userTokenrcv);

        String userToken = UserDTOUtils.toDTO(impl.getUserById(userId).orElse(null)).getToken();

        LinkedList<Face> faces = impl.getFacesByVideoId(videoId);
        System.out.println("HE PRINTEADO "+ faces.size());
        LinkedList<FaceDTO> facesdto = new LinkedList<FaceDTO>();
        for(Face f : faces){
            facesdto.add(FaceDTOUtils.toDTO(f));
        }

        if(!faces.isEmpty()){
            //return Response.ok(new LinkedList<FaceDTO>()).build();
            return Response.status(Response.Status.OK).entity(facesdto).type(MediaType.APPLICATION_JSON).build();
        } else if(!impl.checkToken(authTokenFront, url, userToken)){
            return Response.status(Status.UNAUTHORIZED).build();
        }
            else {
                return Response.status(Status.FORBIDDEN).build();
            }

    }
}
