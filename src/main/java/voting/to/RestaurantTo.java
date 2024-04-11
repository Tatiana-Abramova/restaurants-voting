package voting.to;

import io.swagger.v3.oas.annotations.media.Schema;
import voting.HasId;

import java.time.LocalDate;

@Schema(name = "Restaurant with votes count")
public record RestaurantTo(Integer id, String name, LocalDate registered, Long votesCount) implements HasId {
}
