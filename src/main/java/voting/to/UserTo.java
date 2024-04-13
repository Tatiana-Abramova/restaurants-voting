package voting.to;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import voting.HasId;

@Schema(name = "New user")
public record UserTo(
        // @JsonIgnore does not work with record
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Integer id,
        @NotBlank @Size(min = 2, max = 128) String name,
        @Email @NotBlank @Size(max = 128) String email,
        @NotBlank @Size(max = 128) String password) implements HasId {
}
