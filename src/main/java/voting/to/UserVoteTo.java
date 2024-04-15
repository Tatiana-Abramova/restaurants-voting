package voting.to;

import io.swagger.v3.oas.annotations.media.Schema;
import voting.HasId;

import java.util.Date;


@Schema(name = "User with vote")
public record UserVoteTo(Integer id, String name, String email, Date registered,
                         Integer votedRestaurantId) implements HasId {
}
