package voting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import voting.model.Dish;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static voting.util.ValidationUtil.checkDeleted;
import static voting.util.ValidationUtil.checkNotFound;

@Transactional(readOnly = true)
public interface DishRepository extends JpaRepository<Dish, Integer> {

    @Query("SELECT d FROM Dish d WHERE d.id=:id AND d.restaurant.id=:restId")
    Optional<Dish> get(@Param("id") Integer id, @Param("restId") Integer restId);

    default Dish getExisted(int id, int restId) {
        return checkNotFound(get(id, restId), id, " for restaurant with id = " + restId);
    }

    @Transactional
    @Modifying
    @Query("DELETE FROM Dish d WHERE d.id=:id AND d.restaurant.id=:restId")
    Integer delete(Integer id, Integer restId);

    default void deleteExisted(int id, int restId) {
        checkDeleted(delete(id, restId), id, " for restaurant with id = " + restId);
    }

    @Query("""
            SELECT d FROM Dish d
            WHERE d.restaurant.id = :restId
            AND (d.dishDate >= :startDate OR :startDate IS NULL)
            AND (d.dishDate < :endDate OR :endDate IS NULL)
            ORDER BY d.dishDate DESC, d.name ASC
            """)
    List<Dish> getAllForDates(int restId, LocalDate startDate, LocalDate endDate);
}
