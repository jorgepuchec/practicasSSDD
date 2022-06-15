
package es.um.sisdist.videofaces.backend.dao.models;
import java.io.InputStream;

public class Face
{
    private String fid;
    private String videoid;

    private InputStream fileInputStream;

    // Note: blob data is not included

    public Face()
    {
    }

    public Face(String id, String videoid, InputStream inputStream)
    {
        this.fid = id;
        this.videoid = videoid;
        this.fileInputStream =inputStream;
    }
    public Face(String videoid, InputStream inputStream)
    {
        this.fid = "";
        this.videoid = videoid;
        this.fileInputStream =inputStream;
    }

    public Face(String id, String videoid)
    {
        this.fid = id;
        this.videoid = videoid;
    }


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
    public void setUserid(String videoid)
    {
        this.videoid = videoid;
    }


    public InputStream getData()
    {
        return fileInputStream;
    }
}