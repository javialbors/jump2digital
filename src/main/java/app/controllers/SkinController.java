package app.controllers;

import app.services.SkinService;
import app.model.Skin;
import app.utils.DataErrorException;
import app.utils.DBErrorException;
import app.utils.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Collection;

@RestController
@RequestMapping(path = "j2d/api")
public class SkinController {

    @GetMapping("/skins/available")
    public ResponseEntity<? extends Object> availableSkins() {
        try {
            Collection<Skin> skins = SkinService.getAvailableSkins();

            return ResponseEntity.ok().body(skins);
        } catch (SQLException | DBErrorException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response().serverError());
        } catch (DataErrorException e) {
            return ResponseEntity.status(e.getStatus()).body(new Response().error(e.getMessage()));
        }
    }

    @GetMapping("/skin/getskin/{id}")
    public ResponseEntity<? extends Object> getSkin(@PathVariable Integer id) {
        if (id == null) {
            return ResponseEntity.badRequest().body(new Response().error("Bad request"));
        } else {
            try {
                Skin skin = SkinService.getSkin(id);

                return ResponseEntity.ok(skin);
            } catch (SQLException | DBErrorException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body(new Response().serverError());
            } catch (DataErrorException e) {
                return ResponseEntity.status(e.getStatus()).body(new Response().error(e.getMessage()));
            }
        }
    }
}
