package app.security;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class Argon2 {
    private static Argon2PasswordEncoder argon;

    private Argon2() {
        argon = new Argon2PasswordEncoder(12, 32, 1, 65536, 10);
    }

    private static Argon2PasswordEncoder getInstance() {
        if (argon == null) new Argon2();
        return argon;
    }

    public static String hash(String password) {
        return getInstance().encode(password);
    }

    public static boolean verify(String password, String hash) {
        return getInstance().matches(password, hash);
    }
}
