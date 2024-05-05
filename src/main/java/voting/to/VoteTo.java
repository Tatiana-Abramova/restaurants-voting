package voting.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import voting.HasId;

@Schema(name = "New user vote")
public record VoteTo(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Integer id,
        @NotNull Integer restaurantId) implements HasId {
}
