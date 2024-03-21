package voting.to.mapper;

import voting.model.Role;
import voting.model.User;
import voting.to.UserTo;

public class Mapper {

    public static User createFromUserTo(UserTo userTo) {
        return new User(null, userTo.name(), userTo.email(), userTo.password(), Role.USER);
    }

    public static void updateFromUserTo(User user, UserTo userTo) {
        user.setName(userTo.name());
        user.setEmail(userTo.email());
        user.setPassword(userTo.password());
    }
}
