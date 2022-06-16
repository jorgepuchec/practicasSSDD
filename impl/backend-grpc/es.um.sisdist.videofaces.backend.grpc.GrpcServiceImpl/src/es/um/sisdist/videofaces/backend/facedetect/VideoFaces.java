package es.um.sisdist.videofaces.backend.facedetect;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.openimaj.image.FImage;
import org.openimaj.image.Image;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplay.EndAction;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.VideoPositionListener;
import org.openimaj.video.xuggle.XuggleVideo;
import es.um.sisdist.videofaces.backend.dao.video.IVideoDAO;
import es.um.sisdist.videofaces.backend.dao.face.IFaceDAO;
import es.um.sisdist.videofaces.backend.dao.DAOFactoryImpl;
import es.um.sisdist.videofaces.backend.dao.IDAOFactory;
import es.um.sisdist.videofaces.backend.dao.models.Face;
import java.lang.Thread;
import java.io.*;

/**
 * OpenIMAJ Hello world!
 *
 */
public class VideoFaces extends Thread
{

    IDAOFactory daoFactory;
    IVideoDAO daoVideo;
    IFaceDAO daoFace;

    public String idVideo;

    public VideoFaces(){
        daoFactory = new DAOFactoryImpl();
        daoVideo = daoFactory.createSQLVideoDAO();
        daoFace = daoFactory.createSQLFaceDAO();
    }

    public void setId(String idVideo){
        this.idVideo = idVideo;
    }

    public void run()
    {

        // VideoCapture vc = new VideoCapture( 320, 240 );
        // VideoDisplay<MBFImage> video = VideoDisplay.createVideoDisplay( vc );
        Video<MBFImage> video = new XuggleVideo(daoVideo.getVideoById(idVideo).get().getInput());
        VideoDisplay<MBFImage> vd = VideoDisplay.createOffscreenVideoDisplay(video);

        // El Thread de procesamiento de vídeo se termina al terminar el vídeo.
        vd.setEndAction(EndAction.CLOSE_AT_END);

        vd.addVideoListener(new VideoDisplayListener<MBFImage>() {
            // Número de imagen
            int imgn = 0;

            @Override
            public void beforeUpdate(MBFImage frame)
            {
                FaceDetector<DetectedFace, FImage> fd = new HaarCascadeDetector(40);
                List<DetectedFace> faces = fd.detectFaces(Transforms.calculateIntensity(frame));

                for (DetectedFace face : faces)
                {
                    frame.drawShape(face.getBounds(), RGBColour.RED);
                    try
                    {
                        File file = new File(String.format("/tmp/img%05d.jpg", imgn++));
                        // También permite enviar la imagen a un OutputStream
                        ImageUtilities.write(frame.extractROI(face.getBounds()), file);

                        InputStream inputStream = new FileInputStream(file);
                        daoFace.saveFace(new Face(idVideo, inputStream));
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    System.out.println("!");
                }
            }

            @Override
            public void afterUpdate(VideoDisplay<MBFImage> display)
            {
            }
        });

        vd.addVideoPositionListener(new VideoPositionListener() {
            @Override
            public void videoAtStart(VideoDisplay<? extends Image<?, ?>> vd)
            {
            }

            @Override
            public void videoAtEnd(VideoDisplay<? extends Image<?, ?>> vd)
            {

                daoVideo.setStatusVideo(idVideo, 1);
                System.out.println("End of video");
            }
        });

        System.out.println("Fin.");
    }
}