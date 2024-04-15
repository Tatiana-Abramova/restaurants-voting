package voting.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import voting.error.NotFoundException;
import voting.model.User;
import voting.to.UserVoteTo;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends BaseRepository<User> {

    @Override
    default User getExisted(int id) {
        return applyExisted(this::get, id, User.class);
    }

    @Query("SELECT u FROM User u WHERE u.deleted=false ORDER BY u.name, u.email")
    @Cacheable("users")
    List<User> getAll();

    @Query("SELECT u FROM User u WHERE u.email = LOWER(:email) AND u.deleted=false")
    @Cacheable("auth")
    Optional<User> findByEmailIgnoreCase(String email);
//TODO global WHERE condition for u.deleted=false

    @Query("SELECT new voting.to.UserVoteTo(u.id, u.name, u.email, CAST(u.registered AS DATE), v.id.restaurantId) " +
            "FROM User u " +
            "LEFT JOIN FETCH Vote v ON u.id = v.id.userId " +
            "WHERE u.id = :id AND (v.id.voteDate = CURRENT_DATE OR v.id.voteDate IS NULL)")
    Optional<UserVoteTo> getWithVote(int id);

    default UserVoteTo getWithVoteExisted(int id) {
        return applyExisted(this::getWithVote, id, User.class);
    }

    default void deleteExisted(int id) {
        if (delete(id) == 0) {
            throw new NotFoundException(id, User.class);
        }
    }
}

