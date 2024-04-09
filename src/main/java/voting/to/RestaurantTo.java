package voting.to;

import io.swagger.v3.oas.annotations.media.Schema;
import voting.HasId;

@Schema(name = "Restaurant with votes count")
public record RestaurantTo(Integer id, String name, Long votesCount) implements HasId {
}
