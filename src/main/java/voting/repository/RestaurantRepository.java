package voting.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import voting.model.Restaurant;
import voting.to.RestaurantTo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant> {

    @Override
    @EntityGraph(attributePaths = {"dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Restaurant> get(int id);

    @Query("""
            SELECT new voting.to.RestaurantTo(r.id, r.name, r.registered, count(v.restaurant.id))
            FROM Restaurant r
            LEFT JOIN FETCH Vote v ON r.id = v.restaurant.id
            LEFT JOIN User u ON v.user.id = u.id
            WHERE r.deleted = false
            AND (v.voteDate = CURRENT_DATE OR v.voteDate IS NULL)
            AND (u.deleted=false OR u.deleted IS NULL)
            GROUP BY r.id, r.name, v.restaurant.id
            ORDER BY r.name
            """)
    List<RestaurantTo> getAllWithVoteCount();

    @EntityGraph(attributePaths = {"dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("""
            SELECT r FROM Restaurant r
            LEFT JOIN r.dishes d
            WHERE r.deleted = false
            AND (d.dishDate >= :startDate OR :startDate IS NULL)
            AND (d.dishDate < :endDate OR :endDate IS NULL)
            ORDER BY r.name
            """)
    List<Restaurant> getAllForDishDates(LocalDate startDate, LocalDate endDate);
}
