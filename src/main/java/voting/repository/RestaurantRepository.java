package voting.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import voting.model.Restaurant;
import voting.to.RestaurantTo;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant> {

    default Restaurant getExisted(int id) {
        return applyExisted(this::get, id, Restaurant.class);
    }

/*    @Transactional
    @Modifying
    @Query("UPDATE Restaurant r SET r.deleted=true WHERE r.id=:id")
    int delete(int id);*/

    @Query("""
            SELECT new voting.to.RestaurantTo(r.id, r.name, r.registered, count(v.id.restaurantId))
            FROM Restaurant r
            LEFT JOIN FETCH Vote v ON r.id = v.id.restaurantId
            LEFT JOIN User u ON v.id.userId = u.id
            WHERE r.id = :id
            AND r.deleted = false
            AND (v.id.voteDate = CURRENT_DATE OR v.id.voteDate IS NULL)
            AND (u.deleted=false OR u.deleted IS NULL)
            GROUP BY v.id.restaurantId
            ORDER BY r.name
            """)
    Optional<RestaurantTo> getWithVoteCount(int id);

    default RestaurantTo getWithVoteCountExisted(int id) {
        return applyExisted(this::getWithVoteCount, id, Restaurant.class);
    }

    @Query("""
            SELECT new voting.to.RestaurantTo(r.id, r.name, r.registered, count(v.id.restaurantId))
            FROM Restaurant r
            LEFT JOIN FETCH Vote v ON r.id = v.id.restaurantId
            LEFT JOIN User u ON v.id.userId = u.id
            WHERE r.deleted = false
            AND (v.id.voteDate = CURRENT_DATE OR v.id.voteDate IS NULL)
            AND (u.deleted=false OR u.deleted IS NULL)
            GROUP BY r.id, r.name, v.id.restaurantId
            ORDER BY r.name
            """)
    @Cacheable("restaurants")
    List<RestaurantTo> getAllWithVoteCount();
}
