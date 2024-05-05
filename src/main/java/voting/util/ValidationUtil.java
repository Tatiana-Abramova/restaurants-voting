package voting.util;

import voting.error.NotFoundException;

import java.util.Optional;

public class ValidationUtil {

    public static <T> T checkNotFound(Optional<T> opt, int id) {
        return checkNotFound(opt, id, null);
    }

    public static <T> T checkNotFound(Optional<T> opt, int id, String msg) {
        return opt.orElseThrow(() -> new NotFoundException(id, msg));
    }

    public static <T> void checkDeleted(int count, int id) {
        checkDeleted(count, id, null);
    }

    public static <T> void checkDeleted(int count, int id, String msg) {
        if (count == 0) {
            throw new NotFoundException(id, msg);
        }
    }
}
