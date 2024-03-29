package voting.util;

import voting.model.User;

import static voting.config.SecurityConfig.PASSWORD_ENCODER;

public class UserUtil {

    public static User prepareToSave(User user) {
        user.setPassword(PASSWORD_ENCODER.encode(user.getPassword()));
        user.setEmail(user.getEmail().toLowerCase());
        return user;
    }
}
