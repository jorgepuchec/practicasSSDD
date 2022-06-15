package es.um.sisdist.videofaces.models;

import es.um.sisdist.videofaces.backend.dao.models.Face;
//import org.apache.commons.codec.binary.Base64;
import java.util.Base64;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
public class FaceDTOUtils{

    public static Face fromDTO(FaceDTO fdto)
    {

        String enconded = fdto.getData();
        byte[] bytes = Base64.getDecoder().decode(enconded);
        InputStream decoded = new ByteArrayInputStream(bytes);

        return new Face(fdto.getFid(), fdto.getVideoid(), decoded);
    }

    public static FaceDTO toDTO(Face f) throws java.io.IOException
    {
        if (f == null){
            return null;
        }

        InputStream input = f.getData();
        byte[] bytes = IOUtils.toByteArray(input);
        String enconded = Base64.getEncoder().encodeToString(bytes);

        return new FaceDTO(f.getFid(), f.getVideoid(), enconded);
    }
}