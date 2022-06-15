/**
 *
 */
package es.um.sisdist.videofaces.backend.dao;

import es.um.sisdist.videofaces.backend.dao.user.IUserDAO;
import es.um.sisdist.videofaces.backend.dao.video.IVideoDAO;
import es.um.sisdist.videofaces.backend.dao.face.IFaceDAO;

/**
 * @author dsevilla
 *
 */
public interface IDAOFactory
{
    public IUserDAO createSQLUserDAO();

    public IFaceDAO createSQLFaceDAO();

    public IVideoDAO createSQLVideoDAO();

    public IUserDAO createMongoUserDAO();
}
