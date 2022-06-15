
package es.um.sisdist.videofaces.backend.dao.face;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.*;

import es.um.sisdist.videofaces.backend.dao.models.Face;


@SuppressWarnings("deprecation")
public class SQLFaceDAO implements IFaceDAO
{
    Connection conn;
    private int auxF = 1;
    private String idF = String.valueOf(auxF);

    public SQLFaceDAO()
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
    public Optional<Face> getFaceById(String id)
    {
        PreparedStatement stm;
        try
        {
            stm = conn.prepareStatement("SELECT * from faces WHERE id = ?");
            stm.setString(1, id);
            ResultSet result = stm.executeQuery();
            System.out.println(result.toString());
            if (result.next())
                return createFace(result);
        } catch (SQLException e)
        {
            // Fallthrough
        }
        return Optional.empty();
    }
    @Override
    public LinkedList<Face> getFacesByVideoId(String videoId)
    {
        PreparedStatement stm;
        try
        {
            stm = conn.prepareStatement("SELECT * from faces WHERE videoid = ?");
            stm.setString(1, videoId);
            ResultSet result = stm.executeQuery();
            System.out.println(result.toString());

            LinkedList<Face> listaFaces = new LinkedList<Face>();
            while(result.next()){
                listaFaces.add(createFace(result).get());
            }
            return listaFaces;
        } catch (SQLException e)
        {
            // Fallthrough
        }
        return new LinkedList<Face>();
    }

    


    private Optional<Face> createFace(ResultSet result)
    {
        try
        {
            return Optional.of(new Face(result.getString(1), 
                    result.getString(2),
                    result.getBinaryStream(3))); 
        } catch (SQLException e)
        {
            return Optional.empty();
        }
    }


    @Override
    public Optional<Face> saveFace(Face f)
    {
        PreparedStatement stm;
        try
        {
            f.setFid(idF);
            stm = conn.prepareStatement("INSERT INTO videos VALUES(?, ?, ?, ?, ?, ?);");
            stm.setString(1, f.getFid());
            stm.setString(2, f.getVideoid());
            stm.setBlob(3, f.getData());
            this.auxF ++;
            this.idF = String.valueOf(auxF);


            stm.executeUpdate();

            return getFaceById(f.getFid());
        } catch (SQLException e)
        {
            // Fallthrough
        }
        return Optional.empty();
    }


}
