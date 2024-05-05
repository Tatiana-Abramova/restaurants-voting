package voting.to;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;


@AllArgsConstructor
@ToString
@Schema(name = "User vote with restaurant details")
public class VoteRestaurantTo {

    Integer id;

    private LocalDate voteDate;

    private int restaurantId;

    private String restaurantName;
}
