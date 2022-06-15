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
import jakarta.ws.rs.core.Response.Status;
import java.util.*;

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
            @PathParam("userId") String userId) throws Exception
    {
        UserDTO user = UserDTOUtils.toDTO(impl.getUserById(userId).orElse(null));
        if (user == null){

            return Response.status(Status.FORBIDDEN).build();

        }else{

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
    public Response video(@PathParam("userId") String userId){
        
        LinkedList<Video> vs = impl.getVideosById(userId);
        System.out.println("HE PRINTEADO "+ vs.get(0).getFilename());
        LinkedList<VideoDTO> vddto = new LinkedList<VideoDTO>();
        for(Video v : vs){
            vddto.add(VideoDTOUtils.toDTO(v));
        }

        if(!vs.isEmpty()){

            //return new LinkedList<VideoDTO>();
            return Response.status(Response.Status.OK).entity(vddto).type(MediaType.APPLICATION_JSON).build();
            //return Response.ok(vddto).build();
        } else {
            return Response.status(Status.FORBIDDEN).build();
        }

    }
}
