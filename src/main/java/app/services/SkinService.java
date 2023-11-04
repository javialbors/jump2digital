package app.services;

import app.model.Skin;
import app.persistence.SkinRepository;
import app.utils.DataErrorException;
import app.utils.DBErrorException;

import java.sql.SQLException;
import java.util.Collection;

public class SkinService {

    public static Collection<Skin> getAvailableSkins() throws SQLException, DataErrorException, DBErrorException {
        return SkinRepository.getSkins();
    }

    public static Skin getSkin(int id) throws SQLException, DataErrorException, DBErrorException {
        return SkinRepository.getSkinByID(id);
    }
}
