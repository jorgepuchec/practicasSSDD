package es.um.sisdist.videofaces.models;

import es.um.sisdist.videofaces.backend.dao.models.Face;
public class FaceDTOUtils{

    public static Face fromDTO(FaceDTO fdto)
    {

        return new Face(fdto.getFid(), fdto.getVideoid(), fdto.getData());
    }

    public static FaceDTO toDTO(Face f)
    {
        if (f == null){
            return null;
        }
        return new FaceDTO(f.getFid(), f.getVideoid(), f.getData());
    }
}