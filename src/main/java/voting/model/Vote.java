package voting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vote")
@Getter
@Setter
@NoArgsConstructor
public class Vote {

    @EmbeddedId
    private VoteId id;

    @MapsId("restaurantId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rest_id", nullable = false, updatable = false)
    @JsonIgnore
    private Restaurant restaurant;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @JsonIgnore
    private User user;

    public Vote(VoteId id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Vote: userId:" + id.getUserId() + ", restaurantId:" + id.getRestaurantId() + ", date:" + id.getVoteDate();
    }
}
