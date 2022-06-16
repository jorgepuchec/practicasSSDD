/**
 *
 */
package es.um.sisdist.videofaces.backend.dao.video;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.*;

import es.um.sisdist.videofaces.backend.dao.models.Video;

/**
 * @author dsevilla
 *
 */
@SuppressWarnings("deprecation")
public class SQLVideoDAO implements IVideoDAO
{
    Connection conn;
    private int auxV = 1;
    private String idV = String.valueOf(auxV);

    public SQLVideoDAO()
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

            // Si el nombre del host se pasa por environment, se usa aquí.
            // Si no, se usa localhost. Esto permite configurarlo de forma
            // sencilla para cuando se ejecute en el contenedor, y a la vez
            // se pueden hacer pruebas locales
            Optional<String> sqlServerName = Optional.ofNullable(System.getenv("SQL_SERVER"));
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + sqlServerName.orElse("localhost") + "/videofaces?user=root&password=root");

        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Video> getVideoById(String id)
    {
        PreparedStatement stm;
        try
        {
            stm = conn.prepareStatement("SELECT * from videos WHERE id = ?");
            stm.setString(1, id);
            ResultSet result = stm.executeQuery();
            System.out.println(result.toString());
            if (result.next())
                return createVideo(result);
        } catch (SQLException e)
        {
            // Fallthrough
        }
        return Optional.empty();
    }
    @Override
    public LinkedList<Video> getVideosById(String userId)
    {
        PreparedStatement stm;
        try
        {
            stm = conn.prepareStatement("SELECT * from videos WHERE userid = ?");
            stm.setString(1, userId);
            ResultSet result = stm.executeQuery();
            System.out.println(result.toString());

            LinkedList<Video> listaVideos = new LinkedList<Video>();
            while(result.next()){
                listaVideos.add(createVideo(result).get());
            }
            return listaVideos;
        } catch (SQLException e)
        {
            // Fallthrough
        }
        return new LinkedList<Video>();
    }

    


    private Optional<Video> createVideo(ResultSet result)
    {
        Video.PROCESS_STATUS estado;
        try
        {

            if(result.getInt(5)==0){
                estado = Video.PROCESS_STATUS.PROCESSING;
            }else {
                estado = Video.PROCESS_STATUS.PROCESSED;
            }



            return Optional.of(new Video(result.getString(1), 
                    result.getString(2), 
                    result.getString(3), 
                    result.getString(4), 
                    estado, 
                    result.getBinaryStream(6))); 
        } catch (SQLException e)
        {
            return Optional.empty();
        }
    }


    @Override
    public Optional<Video> saveVideo(Video v)
    {
        int estado;
        if(v.getPstatus()==Video.PROCESS_STATUS.PROCESSING){
            estado = 0;
        }else{
            estado = 1;
        }
        PreparedStatement stm;
        try
        {
            v.setVid(idV);
            stm = conn.prepareStatement("INSERT INTO videos VALUES(?, ?, ?, ?, ?, ?);");
            stm.setString(1, v.getVid());
            stm.setString(2, v.getUserid());
            stm.setString(3, v.getDate());
            stm.setString(4, v.getFilename());
            stm.setInt(5, estado);
            stm.setBlob(6, v.getInput());
            this.auxV ++;
            this.idV = String.valueOf(auxV);


            stm.executeUpdate();

            return getVideoById(v.getVid());
        } catch (SQLException e)
        {
            // Fallthrough
        }
        return Optional.empty();
    }

    @Override
    public InputStream getStreamForVideo(String id){
        PreparedStatement stm;
        try
        {
            stm = conn.prepareStatement("SELECT videodata from videos WHERE id = ?");
            stm.setString(1, id);
            ResultSet result = stm.executeQuery();
            return result.getBinaryStream(1);
        } catch (SQLException e)
        {
            // Fallthrough
        }
        return null;
    }

    @Override
    public Video.PROCESS_STATUS getVideoStatus(String id){
        PreparedStatement stm;
        try
        {
            stm = conn.prepareStatement("SELECT process_status from videos WHERE id = ?");
            stm.setString(1, id);
            ResultSet result = stm.executeQuery();
            int estado = result.getInt(1);
            if(estado==0){
                return Video.PROCESS_STATUS.PROCESSING;
            }else {
                return Video.PROCESS_STATUS.PROCESSED;
            }
        } catch (SQLException e)
        {
            // Fallthrough
        }
        return null;
    }

    @Override
    public Optional<Video> setStatusVideo(String id, int status){
        PreparedStatement stm;
        try
        {
            stm = conn.prepareStatement("UPDATE videos SET process_status=? WHERE id = ?");
            stm.setInt(1, status);
            stm.setString(2, id);
            stm.executeUpdate();
            

            return getVideoById(id);
        } catch (SQLException e)
        {
            // Fallthrough
        }
        return Optional.empty();
    }

}
