package voting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static voting.util.ValidationUtil.checkDeleted;
import static voting.util.ValidationUtil.checkNotFound;

@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, Integer> {

    @Query("SELECT e FROM #{#entityName} e WHERE e.id=:id AND e.deleted=false")
    Optional<T> get(int id);

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted=true WHERE e.id=:id AND e.deleted = false")
    Integer delete(Integer id);

    default T getExisted(int id) {
        return checkNotFound(get(id), id);
    }

    default void checkExisted(int id) {
        getExisted(id);
    }

    default void deleteExisted(int id) {
        checkDeleted(delete(id), id);
    }
}
