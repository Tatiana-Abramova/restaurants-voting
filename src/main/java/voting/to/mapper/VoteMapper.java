package voting.to.mapper;

import voting.model.Restaurant;
import voting.model.Vote;
import voting.security.AuthUser;
import voting.to.VoteTo;

public class VoteMapper {

    public static Vote createFromVoteTo(VoteTo voteTo, AuthUser authUser) {
        return new Vote(null, authUser.getUser(), new Restaurant(voteTo.restaurantId()));
    }

    public static Vote updateFromVoteTo(Vote vote, VoteTo voteTo) {
        vote.setRestaurant(new Restaurant(voteTo.restaurantId()));
        return vote;
    }
}
