/**
 *
 */
package es.um.sisdist.videofaces.backend.dao;

import es.um.sisdist.videofaces.backend.dao.user.IUserDAO;
import es.um.sisdist.videofaces.backend.dao.video.IVideoDAO;
import es.um.sisdist.videofaces.backend.dao.face.IFaceDAO;
import es.um.sisdist.videofaces.backend.dao.user.MongoUserDAO;
import es.um.sisdist.videofaces.backend.dao.user.SQLUserDAO;
import es.um.sisdist.videofaces.backend.dao.video.SQLVideoDAO;
import es.um.sisdist.videofaces.backend.dao.face.SQLFaceDAO;

/**
 * @author dsevilla
 *
 */
public class DAOFactoryImpl implements IDAOFactory
{
    @Override
    public IUserDAO createSQLUserDAO()
    {
        return new SQLUserDAO();
    }


    @Override
    public IFaceDAO createSQLFaceDAO()
    {
        return new SQLFaceDAO();
    }

    @Override
    public IVideoDAO createSQLVideoDAO()
    {
        return new SQLVideoDAO();
    }

    @Override
    public IUserDAO createMongoUserDAO()
    {
        return new MongoUserDAO();
    }
}
