package voting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import voting.error.NotFoundException;

import java.util.Optional;
import java.util.function.Function;

@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, Integer> {

    @Query("SELECT u FROM #{#entityName} u WHERE u.id=:id AND u.deleted=false")
    Optional<T> get(@Param("id") Integer id);

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} u SET u.deleted=true WHERE u.id=:id AND u.deleted = false")
    int delete(int id);

    default <R> R applyExisted(Function<Integer, Optional<R>> func, int id, Class<T> clazz) {
        return func.apply(id).orElseThrow(() -> new NotFoundException(id, clazz));
    }

    T getExisted(int id);
}
