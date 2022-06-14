package es.um.sisdist.videofaces.models;

import jakarta.xml.bind.annotation.XmlRootElement;
import es.um.sisdist.videofaces.backend.dao.models.Video;
import java.io.InputStream;

@XmlRootElement
public class VideoDTO
{
    private String vid;
    private String userid;
    private String date;
    private String filename; // En caso de que se utilice un esquema híbrido de usar un sistema de ficheros
    private InputStream fileInputStream;
    private Video.PROCESS_STATUS pstatus;

    /**
     * @return the id
     */
    public String getVid()
    {
        return vid;
    }

    /**
     * @param id the id to set
     */
    public void setVid(String id)
    {
        this.vid = id;
    }

    /**
     * @return the userid
     */
    public String getUserid()
    {
        return userid;
    }

    /**
     * @param userid the userid to set
     */
    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    /**
     * @return the date
     */
    public String getDate()
    {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date)
    {
        this.date = date;
    }

    /**
     * @return the filename
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    /**
     * @return the pstatus
     */
    public Video.PROCESS_STATUS getPstatus()
    {
        return pstatus;
    }

    /**
     * @param pstatus the pstatus to set
     */
    public void setPstatus(Video.PROCESS_STATUS pstatus)
    {
        this.pstatus = pstatus;
    }

    /**
     * @return the fileInputStream
     */
    public InputStream getFileInputStream()
    {
        return fileInputStream;
    }

    /**
     * @param fileInputStream the fileInputStream to set
     */
    public void setFileInputStream(InputStream fileInputStream)
    {
        this.fileInputStream = fileInputStream;
    }

    public VideoDTO(String id, String userid, String date, String filename, Video.PROCESS_STATUS pstatus, InputStream fileInputStream)
    {
        super();
        this.vid = id;
        this.userid = userid;
        this.date = date;
        this.filename = filename;
        this.pstatus = pstatus;
        this.fileInputStream = fileInputStream;
    }


    public VideoDTO(String userid, String date, String filename, Video.PROCESS_STATUS pstatus, InputStream fileInputStream)
    {
        super();
        this.vid = "";
        this.userid = userid;
        this.date = date;
        this.filename = filename;
        this.pstatus = pstatus;
        this.fileInputStream = fileInputStream;
    }
    

    public VideoDTO()
    {
    }
}
