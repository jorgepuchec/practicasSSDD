package es.um.sisdist.videofaces.models;

import es.um.sisdist.videofaces.backend.dao.models.Video;
public class VideoDTOUtils{

    public static Video fromDTO(VideoDTO vdto)
    {
        return new Video(vdto.getVid(), vdto.getUserid(), vdto.getDate(), vdto.getFilename(), vdto.getPstatus(), vdto.getFileInputStream());
    }

    public static VideoDTO toDTO(Video v)
    {
        if (v == null){
            return null;
        }
        return new VideoDTO(v.getVid(), v.getUserid(), 
            v.getDate(), v.getFilename(), v.getPstatus(), v.getInput());
    }
}
