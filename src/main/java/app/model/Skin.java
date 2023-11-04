package app.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Skin implements Serializable {
    private Integer id;
    private String name;
    private Float price;
    private String color;
    private ArrayList<Type> types;


    public Skin(int id, String name, float price, String color) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.color = color;
    }

    public Skin(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public void addType(Type type) {
        if (types == null) types = new ArrayList<>();
        types.add(type);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Float getPrice() {
        return price;
    }

    public String getColor() {
        return color;
    }

    public ArrayList<Type> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<Type> types) {
        this.types = types;
    }
}
