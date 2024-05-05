package voting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import voting.model.Vote;
import voting.to.VoteRestaurantTo;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
public interface VoteRepository extends JpaRepository<Vote, Integer> {

    @Query("SELECT v FROM Vote v WHERE v.user.id = :userId AND v.voteDate = CURRENT_DATE")
    Vote getCurrentByUserId(int userId);

    @Query("""
            SELECT new voting.to.VoteRestaurantTo(v.id, v.voteDate, r.id, r.name) FROM Vote v
            JOIN FETCH Restaurant r ON v.restaurant.id = r.id
            WHERE v.user.id = :userId
            AND (v.voteDate >= :startDate OR :startDate IS NULL)
            AND (v.voteDate < :endDate OR :endDate IS NULL)
            ORDER BY v.voteDate DESC
            """)
    List<VoteRestaurantTo> getAllByUserId(int userId, LocalDate startDate, LocalDate endDate);
}
