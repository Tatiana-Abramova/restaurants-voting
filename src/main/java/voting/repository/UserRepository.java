package voting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import voting.error.NotFoundException;
import voting.model.User;
import voting.to.UserVoteTo;

import java.util.Optional;

import static voting.config.SecurityConfig.PASSWORD_ENCODER;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.email = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(String email);

    @Query("SELECT new voting.to.UserVoteTo(u.id, u.name, u.email, CAST(u.registered AS DATE), v.id.restaurantId) FROM User u LEFT JOIN FETCH Vote v ON u.id = v.id.userId WHERE u.id = :id AND (v.voteDate = CURRENT_DATE OR v.voteDate IS NULL) ORDER BY u.name, u.email")
    UserVoteTo getWithVote(@Param("id") Integer id);

    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.id=:id")
    int delete(int id);

    default void deleteExisted(int id) {
        if (delete(id) == 0) {
            throw new NotFoundException("User with id=" + id + " not found");
        }
    }
}
