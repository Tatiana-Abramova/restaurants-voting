package voting.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import voting.model.User;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends BaseRepository<User> {

    @Query("SELECT u FROM User u WHERE u.deleted=false ORDER BY u.name, u.email")
    @Cacheable("users")
    List<User> getAll();

    @Query("SELECT u FROM User u WHERE u.email = LOWER(:email) AND u.deleted=false")
    @Cacheable("auth")
    Optional<User> findByEmailIgnoreCase(String email);
}

