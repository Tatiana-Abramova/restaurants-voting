package voting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "vote",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"vote_date", "user_id", "rest_id"}, name = "vote_unique_vote_date_user_restaurant_idx")},
        indexes = {
                @Index(name = "vote_user_id_vote_date_idx", columnList = "user_id, vote_date"),
                @Index(name = "vote_vote_date_idx", columnList = "vote_date")})
@Getter
@Setter
@NoArgsConstructor
public class Vote extends BaseEntity {

    @Column(name = "vote_date", nullable = false, updatable = false, columnDefinition = "date default CURRENT_DATE")
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate voteDate = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rest_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Restaurant restaurant;

    public Vote(Integer id, User user, Restaurant restaurant) {
        super(id);
        this.setUser(user);
        this.setRestaurant(restaurant);
    }

    @Override
    public String toString() {
        return "Vote: userId:" + user.getId() + ", restaurantId:" + restaurant.getId() + ", date:" + voteDate;
    }
}
