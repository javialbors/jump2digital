package app.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Color {
    private Integer id;
    private String hex;

    public Color(int id, String hex) {
        this.id = id;
        this.hex = hex;
    }

    public Integer getId() {
        return id;
    }

    public String getHex() {
        return hex;
    }
}
