package app.utils;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONReader {
    public static JSONObject readFromFile(String path) {
        try {
            return new JSONObject(new String(Files.readAllBytes(Paths.get(path))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
