package voting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import voting.error.NotFoundException;

import java.util.Optional;
import java.util.function.Function;

@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, Integer> {

    @Query("SELECT e FROM #{#entityName} e WHERE e.id=:id AND e.deleted=false")
    Optional<T> get(int id);

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted=true WHERE e.id=:id AND e.deleted = false")
    int delete(int id);

    default <R> R applyExisted(Function<Integer, Optional<R>> func, int id, Class<T> clazz) {
        return func.apply(id).orElseThrow(() -> new NotFoundException(id, clazz));
    }

    T getExisted(int id);
}
