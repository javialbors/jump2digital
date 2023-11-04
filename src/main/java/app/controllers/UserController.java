package app.controllers;

import app.model.Skin;
import app.model.User;
import app.services.UserService;
import app.utils.DBErrorException;
import app.utils.DataErrorException;
import app.utils.Response;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.Collection;

@CrossOrigin
@RestController
@RequestMapping("j2d/api")
public class UserController {

    private interface UserCredentialsResponseView extends User.UserCredentialsView, Response.ResponseView {};
    private interface UserInfoResponseView extends User.UserInfoView, Response.ResponseView {};

    @JsonView(UserCredentialsResponseView.class)
    @PostMapping("/register")
    public ResponseEntity<? extends Object> register(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        try { username = URLDecoder.decode(username, "UTF-8"); }
        catch (UnsupportedEncodingException e) { username = null; }

        try { password = URLDecoder.decode(password, "UTF-8"); }
        catch (UnsupportedEncodingException e) { password = null; }


        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(new Response().error("Bad request"));
        } else {
            try {
                User user = UserService.registerUser(username, password);

                if (user == null) throw new DataErrorException(401, "User already exists");

                return ResponseEntity.ok(user);
            } catch (SQLException | DBErrorException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body(new Response().serverError());
            } catch (DataErrorException e) {
                return ResponseEntity.status(e.getStatus()).body(new Response().error(e.getMessage()));
            }
        }
    }

    @JsonView(UserCredentialsResponseView.class)
    @PostMapping("/login")
    public ResponseEntity<? extends Object> login(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        try { username = URLDecoder.decode(username, "UTF-8"); }
        catch (UnsupportedEncodingException e) { username = null; }

        try { password = URLDecoder.decode(password, "UTF-8"); }
        catch (UnsupportedEncodingException e) { password = null; }

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(new Response().error("Bad request"));
        } else {
            try {
                User user = UserService.login(username, password);

                return ResponseEntity.ok(user);
            } catch (SQLException | DBErrorException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body(new Response().serverError());
            } catch (DataErrorException e) {
                return ResponseEntity.status(e.getStatus()).body(new Response().error(e.getMessage()));
            }
        }
    }

    @JsonView(UserInfoResponseView.class)
    @PostMapping("/skins/buy")
    public ResponseEntity<? extends Object> buySkin(@RequestParam(value = "skinID") Integer skinID, @RequestParam(value = "apiKey") String apiKey) {
        try { apiKey = URLDecoder.decode(apiKey, "UTF-8"); }
        catch (UnsupportedEncodingException e) { apiKey = null; }

        if (skinID == null || apiKey == null) {
            return ResponseEntity.badRequest().body(new Response().error("Bad request"));
        } else {
            try {
                User user = UserService.buySkin(skinID, apiKey);

                return ResponseEntity.ok(new Response(user).success("Item bought"));
            } catch (SQLException | DBErrorException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body(new Response().serverError());
            } catch (DataErrorException e) {
                return ResponseEntity.status(e.getStatus()).body(new Response(e.getObject()).error(e.getMessage()));
            }
        }
    }

    @GetMapping("/skins/myskins")
    public ResponseEntity<? extends Object> userSkins(@RequestParam(value = "apiKey") String apiKey) {
        try { apiKey = URLDecoder.decode(apiKey, "UTF-8"); }
        catch (UnsupportedEncodingException e) { apiKey = null; }

        if (apiKey == null) {
            return ResponseEntity.badRequest().body(new Response().error("Bad request"));
        } else {
            try {
                Collection<Skin> userSkins = UserService.getUserSkins(apiKey);

                return ResponseEntity.ok().body(userSkins);
            } catch (SQLException | DBErrorException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body(new Response().serverError());
            } catch (DataErrorException e) {
                return ResponseEntity.status(e.getStatus()).body(new Response().error(e.getMessage()));
            }
        }
    }

    @PutMapping("/skins/color")
    public ResponseEntity<? extends Object> changeSkinColor(@RequestParam(value = "skinID") Integer skinID, @RequestParam(value = "color") String hexColor, @RequestParam(value = "apiKey") String apiKey) {
        try { hexColor = URLDecoder.decode(hexColor, "UTF-8"); }
        catch (UnsupportedEncodingException e) { hexColor = null; }

        try { apiKey = URLDecoder.decode(apiKey, "UTF-8"); }
        catch (UnsupportedEncodingException e) { apiKey = null; }

        if (skinID == null || hexColor == null || apiKey == null) {
            return ResponseEntity.badRequest().body(new Response().error("Bad request"));
        } else {
            try {
                Skin skin = UserService.changeSkinColor(skinID, hexColor, apiKey);

                return ResponseEntity.ok(skin);
            } catch (SQLException | DBErrorException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body(new Response().serverError());
            } catch (DataErrorException e) {
                return ResponseEntity.status(e.getStatus()).body(new Response().error(e.getMessage()));
            }
        }
    }

    @DeleteMapping("/skins/delete/{id}")
    public ResponseEntity<? extends Object> deleteSkin(@PathVariable String id, @RequestParam(value = "apiKey") String apiKey) {
        try { apiKey = URLDecoder.decode(apiKey, "UTF-8"); }
        catch (UnsupportedEncodingException e) { apiKey = null; }

        Integer skinID;
        try { skinID = Integer.parseInt(id); }
        catch (Exception e) { skinID = null; }

        if (skinID == null || apiKey == null) {
            return ResponseEntity.badRequest().body(new Response().error("Bad request"));
        } else {
            try {
                Collection<Skin> userSkins = UserService.deleteSkin(skinID, apiKey);

                return ResponseEntity.ok(userSkins);
            } catch (SQLException | DBErrorException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body(new Response().serverError());
            } catch (DataErrorException e) {
                return ResponseEntity.status(e.getStatus()).body(new Response().error(e.getMessage()));
            }
        }
    }
}
