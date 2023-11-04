package app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    public interface UserCredentialsView {}
    public interface UserInfoView {}

    private Integer id;
    private String username;
    private String password; //hashed
    private String apiKey;
    private Float funds;

    public User(int id, String username, String password, String apiKey, float funds) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.apiKey = apiKey;
        this.funds = funds;
    }

    public User(int id, String username, float funds) {
        this.id = id;
        this.username = username;
        this.funds = funds;
    }

    @JsonIgnore
    public Integer getId() {
        return id;
    }

    @JsonView({UserCredentialsView.class, UserInfoView.class})
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonView(UserCredentialsView.class)
    public String getApiKey() {
        return apiKey;
    }

    @JsonView({UserCredentialsView.class, UserInfoView.class})
    public Float getFunds() {
        return funds;
    }
}
