package voting.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "vote")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {

    @EmbeddedId
    private VoteId id;

    @Column(name = "vote_date", nullable = false, columnDefinition = "timestamp default now()")
    @NotNull
    private Date vote_date = new Date();

    @Override
    public String toString() {
        return "Vote: userId:" + id.getUser().getId() + ", restaurantId:" + id.getRestaurant().getId() + ", date:" + vote_date;
    }
}
