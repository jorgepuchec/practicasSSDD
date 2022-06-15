package es.um.sisdist.videofaces.models;

import jakarta.xml.bind.annotation.XmlRootElement;
import es.um.sisdist.videofaces.backend.dao.models.Face;
import java.io.InputStream;
import org.apache.commons.codec.binary.Base64;

@XmlRootElement
public class FaceDTO
{
    private String fid;
    private String videoid;
    private String fileInputStream;

    /**
     * @return the id
     */
    public String getFid()
    {
        return fid;
    }

    /**
     * @param id the id to set
     */
    public void setFid(String id)
    {
        this.fid = id;
    }

    /**
     * @return the videoid
     */
    public String getVideoid()
    {
        return videoid;
    }

    /**
     * @param videoid the videoid to set
     */
    public void setVideoid(String videoid)
    {
        this.videoid = videoid;
    }

    /**
     * @return the fileInputStream
     */
    public String getData()
    {
        return fileInputStream;
    }

    /**
     * @param fileInputStream the fileInputStream to set
     */
    public void setData(String fileInputStream)
    {
        this.fileInputStream = fileInputStream;
    }




    public FaceDTO(String id, String videoid, String fileInputStream)
    {
        this.fid = id;
        this.videoid = videoid;
        this.fileInputStream = fileInputStream;
    }


    public FaceDTO(String videoid, String fileInputStream)
    {
        this.fid = "";
        this.videoid = videoid;
        this.fileInputStream = fileInputStream;
    }
    

    public FaceDTO()
    {
    }
}