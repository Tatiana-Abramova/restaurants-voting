package voting.error;

import org.springframework.lang.NonNull;

public class ApiException extends RuntimeException {

    public ApiException(@NonNull String message) {
        super(message);
    }
}
