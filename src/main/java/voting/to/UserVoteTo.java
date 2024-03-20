package voting.to;

import java.util.Date;

public record UserVoteTo(Integer id, String name, String email, Date registered, Integer votedRestaurant) {
}
