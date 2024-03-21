package voting.to;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserTo(Integer id, String name, String email, String password) {
}
