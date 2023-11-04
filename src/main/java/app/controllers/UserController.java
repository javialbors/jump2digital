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

import java.sql.SQLException;
import java.util.Collection;

@RestController
@RequestMapping("j2d/api")
public class UserController {

    private interface UserCredentialsResponseView extends User.UserCredentialsView, Response.ResponseView {};
    private interface UserInfoResponseView extends User.UserInfoView, Response.ResponseView {};

    @JsonView(UserCredentialsResponseView.class)
    @PostMapping("/register")
    public ResponseEntity<? extends Object> register(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
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
    public ResponseEntity<? extends Object> buySkin(@RequestParam(value = "id") Integer id, @RequestParam(value = "apiKey") String apiKey) {
        if (id == null || apiKey == null) {
            return ResponseEntity.badRequest().body(new Response().error("Bad request"));
        } else {
            try {
                User user = UserService.buySkin(id, apiKey);

                return ResponseEntity.ok(user);
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
    public ResponseEntity<? extends Object> changeSkinColor(@RequestParam(value = "id") Integer skinID, @RequestParam(value = "color") String hexColor, @RequestParam(value = "apiKey") String apiKey) {
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
    public ResponseEntity<? extends Object> deleteSkin(@PathVariable Integer id, @RequestParam(value = "apiKey") String apiKey) {
        if (id == null || apiKey == null) {
            return ResponseEntity.badRequest().body(new Response().error("Bad request"));
        } else {
            try {
                Collection<Skin> userSkins = UserService.deleteSkin(id, apiKey);

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
