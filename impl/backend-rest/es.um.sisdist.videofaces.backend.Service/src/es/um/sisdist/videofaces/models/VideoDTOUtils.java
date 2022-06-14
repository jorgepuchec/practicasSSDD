package es.um.sisdist.videofaces.models;

import es.um.sisdist.videofaces.backend.dao.models.Video;
public class VideoDTOUtils{

    public static Video fromDTO(VideoDTO vdto)
    {
        Video.PROCESS_STATUS estado;
        if(vdto.getPstatus()==0){
            estado = Video.PROCESS_STATUS.PROCESSING;
        }else{
            estado = Video.PROCESS_STATUS.PROCESSED;
        }

        return new Video(vdto.getVid(), vdto.getUserid(), vdto.getDate(), estado, vdto.getFilename());
    }

    public static VideoDTO toDTO(Video v)
    {
        if (v == null){
            return null;
        }
        return new VideoDTO(v.getVid(), v.getUserid(), 
            v.getDate(), v.getFilename(), v.getPstatus());
    }
}
