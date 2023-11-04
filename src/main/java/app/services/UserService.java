package app.services;

import app.model.Color;
import app.model.Skin;
import app.model.User;
import app.persistence.ColorRepository;
import app.persistence.SkinRepository;
import app.persistence.UserRepository;
import app.security.Argon2;
import app.utils.DataErrorException;
import app.utils.DBErrorException;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Collection;

public class UserService {
    public static User registerUser(String username, String password) throws SQLException, DBErrorException, DataErrorException {
        try {
            UserRepository.getUser(username);
            return null;
        } catch (DataErrorException e) {
            SecureRandom random = new SecureRandom();

            byte[] secretKey = new byte[16];
            random.nextBytes(secretKey);

            StringBuilder key = new StringBuilder();
            for (byte b : secretKey) {
                key.append(String.format("%02x", b));
            }

            return UserRepository.addUser(username, Argon2.hash(password), key.toString());
        }
    }

    public static User login(String username, String password) throws SQLException, DataErrorException, DBErrorException {
        User user = UserRepository.getUser(username);

        if (Argon2.verify(password, user.getPassword())) return user;
        else throw new DataErrorException(403, "Invalid password");
    }

    public static User buySkin(int id, String apiKey) throws SQLException, DataErrorException, DBErrorException {
        User user = UserRepository.getUserByKey(apiKey);
        Skin skin = SkinRepository.getSkinByID(id);

        try {
            UserRepository.getUserSkinByID(user.getId(), skin.getId());
        } catch (DataErrorException e) {
            if (user.getFunds() < skin.getPrice()) throw new DataErrorException(403, "Not enough funds", user);

            return UserRepository.buySkin(user.getId(), skin.getId(), user.getFunds() - skin.getPrice());
        }

        throw new DataErrorException(403, "You already own this skin");
    }

    public static Collection<Skin> getUserSkins(String apiKey) throws SQLException, DataErrorException, DBErrorException {
        User user = UserRepository.getUserByKey(apiKey);

        return UserRepository.getSkinsFromUser(user.getId());
    }

    public static Skin changeSkinColor(int skinID, String hexColor, String apiKey) throws SQLException, DataErrorException, DBErrorException {
        User user = UserRepository.getUserByKey(apiKey);
        Skin skin = UserRepository.getUserSkinByID(user.getId(), skinID);

        Color color;
        try {
            color = ColorRepository.getColor(hexColor);
        } catch (DataErrorException e) {
            color = ColorRepository.addColor(hexColor);
        }

        return UserRepository.updateSkinColor(user.getId(), skin.getId(), color.getId());
    }


    public static Collection<Skin> deleteSkin(int skinID, String apiKey) throws SQLException, DataErrorException, DBErrorException {
        User user = UserRepository.getUserByKey(apiKey);

        Skin skin = UserRepository.getUserSkinByID(user.getId(), skinID);

        return UserRepository.deleteUserSkin(user.getId(), skin.getId());
    }
}
