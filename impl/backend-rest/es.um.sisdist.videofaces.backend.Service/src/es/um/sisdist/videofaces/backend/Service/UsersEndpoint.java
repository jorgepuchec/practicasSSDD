package es.um.sisdist.videofaces.backend.Service;

import es.um.sisdist.videofaces.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.videofaces.models.UserDTO;
import es.um.sisdist.videofaces.models.UserDTOUtils;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
public class UsersEndpoint
{
    private AppLogicImpl impl = AppLogicImpl.getInstance();


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signIn(UserDTO user)
    {
        if(user.getEmail()==""|| user.getName()==""){

            return Response.status(Status.FORBIDDEN).build();

        } else{

            UserDTO u = UserDTOUtils.toDTO(impl.registerUser(user).orElse(null));

            if(u != null){

                return Response.ok(u).build();

            } else {

                return Response.status(Status.FORBIDDEN).build();
                
            }
        }
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUserInfo(@PathParam("username") String username)
    {
        return UserDTOUtils.toDTO(impl.getUserByEmail(username).orElse(null));
    }
}
