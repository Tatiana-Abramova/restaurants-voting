package voting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import voting.model.Vote;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Integer> {

    @Query("SELECT v FROM Vote v WHERE v.id.userId = :userId AND v.id.voteDate = CURRENT_DATE")
    Vote getByUserId(@Param("userId") Integer userId);

    @Query("SELECT v FROM Vote v WHERE v.id.restaurantId = :restaurantId AND v.id.voteDate = CURRENT_DATE")
    List<Vote> getByRestaurantId(@Param("restaurantId") Integer restaurantId);
}
