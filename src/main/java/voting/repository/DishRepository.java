package voting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import voting.error.NotFoundException;
import voting.model.Dish;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface DishRepository extends JpaRepository<Dish, Integer> {

    @Query("SELECT d FROM Dish d WHERE d.id=:id AND d.restaurant.id=:restId")
    Optional<Dish> get(@Param("id") Integer id, @Param("restId") Integer restId);

    default Dish getExisted(@Param("id") Integer id, @Param("restId") Integer restId) {
        return get(id, restId).orElseThrow(() ->
                new NotFoundException("Dish with id = " + id + " not fount for restaurant with id = " + restId));
    }

    @Transactional
    @Modifying
    @Query("DELETE FROM Dish d WHERE d.id=:id AND d.restaurant.id=:restId")
    int delete(int id, int restId);

    default void deleteExisted(int id, int restId) {
        if (delete(id, restId) == 0) {
            throw new NotFoundException("Dish ith id = " + id + " not fount for restaurant with id = " + restId);
        }
    }

    @Query("SELECT d FROM Dish d WHERE d.restaurant.id = :restId AND d.dishDate = CURRENT_DATE ORDER BY d.name")
    List<Dish> getAll(int restId);

    @Query("""
            SELECT d FROM Dish d
            WHERE d.restaurant.id = :restId
            AND (d.dishDate >= :startDate OR :startDate IS NULL)
            AND (d.dishDate < :endDate OR :endDate IS NULL)
            ORDER BY d.dishDate DESC, d.name ASC
            """)
    List<Dish> getBetweenHalfOpen(int restId, LocalDate startDate, LocalDate endDate);
}
