package app.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

@JsonView(Response.ResponseView.class)
public class Response {
    public interface ResponseView {}

    private String status;
    private String message;
    private Object info;

    public Response() {
    }

    public Response(Object info) {
        this.info = info;
    }

    public Response error(String message) {
        this.status = "error";
        this.message = message;

        return this;
    }

    public Response serverError() {
        this.status = "error";
        this.message = "Server error";

        return this;
    }

    public Response success(String message) {
        this.status = "success";
        this.message = message;

        return this;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Object getInfo() {
        return info;
    }
}
