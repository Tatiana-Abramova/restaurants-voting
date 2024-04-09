package voting.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class VoteId {

    @Column(name = "rest_id", nullable = false)
    @NotNull
    private Integer restaurantId;

    @Column(name = "user_id", nullable = false, updatable = false)
    @NotNull
    private Integer userId;

    @Column(name = "vote_date", nullable = false, updatable = false, columnDefinition = "date default CURRENT_DATE")
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date voteDate = new Date();

    public VoteId(Integer restaurantId, Integer userId) {
        this.restaurantId = restaurantId;
        this.userId = userId;
    }
}
