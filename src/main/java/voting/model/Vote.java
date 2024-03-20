package voting.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Date;

@Entity
@Table(name = "vote")
@Getter
public class Vote {

    @EmbeddedId
    private VoteId id;

    @Column(name = "vote_date", nullable = false, columnDefinition = "date default CURRENT_DATE")
    @NotNull
    private Date voteDate = new Date();

    @Override
    public String toString() {
        return "Vote: userId:" + id.getUserId() + ", restaurantId:" + id.getRestaurantId() + ", date:" + voteDate;
    }
}
