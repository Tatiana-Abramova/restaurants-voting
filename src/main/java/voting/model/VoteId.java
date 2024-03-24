package voting.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Embeddable
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class VoteId {

    @Column(name = "rest_id", nullable = false)
    private Integer restaurantId;

    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private Integer userId;
}
