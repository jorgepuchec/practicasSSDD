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
    private int pstatus;

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
    public int getPstatus()
    {
        return pstatus;
    }




    public VideoDTO(String id, String userid, String date, String filename, Video.PROCESS_STATUS pstatus)
    {
        super();
        this.vid = id;
        this.userid = userid;
        this.date = date;
        this.filename = filename;


        if(pstatus==Video.PROCESS_STATUS.PROCESSING){
            this.pstatus = 0;
        }else{
            this.pstatus = 1;
        }
    }


    public VideoDTO(String userid, String date, String filename, Video.PROCESS_STATUS pstatus)
    {
        super();
        this.vid = "";
        this.userid = userid;
        this.date = date;
        this.filename = filename;
        if(pstatus==Video.PROCESS_STATUS.PROCESSING){
            this.pstatus = 0;
        }else{
            this.pstatus = 1;
        }
    }
    

    public VideoDTO()
    {
    }
}
