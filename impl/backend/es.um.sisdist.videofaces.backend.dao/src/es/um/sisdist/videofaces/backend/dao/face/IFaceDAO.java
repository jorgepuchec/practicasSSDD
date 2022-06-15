package es.um.sisdist.videofaces.backend.dao.face;

import java.util.*;
import java.util.Optional;
import es.um.sisdist.videofaces.backend.dao.models.Face;

public interface IFaceDAO
{
    public Optional<Face> getFaceById(String id);

    public Optional<Face> saveFace(Face f);

    public LinkedList<Face> getFacesByVideoId(String videoId);
}