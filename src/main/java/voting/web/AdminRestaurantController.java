package voting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import voting.error.DataConflictException;
import voting.model.Dish;
import voting.model.Restaurant;
import voting.repository.DishRepository;
import voting.repository.RestaurantRepository;
import voting.to.RestaurantTo;
import voting.util.RestUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@Tag(name = "4. Restaurant Administration")
@RestController
@RequestMapping(value = AdminRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminRestaurantController {

    private final Logger log = getLogger(getClass());

    protected static final String REST_URL = "/api/admin/restaurants";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DishRepository dishRepository;

    @Operation(summary = "Get all restaurants")
    @GetMapping
    public List<RestaurantTo> getAll() {
        log.info("getAll");
        return restaurantRepository.getAllWithVoteCount();
    }

    @Operation(summary = "Get restaurant details by ID")
    @GetMapping("/{id}")
    public Restaurant get(@PathVariable int id) {
        log.info("get restaurant with id = {}", id);
        return restaurantRepository.getExisted(id);
    }

    @Operation(summary = "Create a new restaurant")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(value = "restaurants", allEntries = true)
    public ResponseEntity<Restaurant> create(@RequestBody @Valid Restaurant restaurant) {
        log.info("create {}", restaurant);
        restaurant.checkNew();
        Restaurant created = restaurantRepository.save(restaurant);
        return RestUtil.buildResponse(created, REST_URL);
    }

    @Operation(summary = "Update restaurant by ID")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = "restaurants", allEntries = true)
    public ResponseEntity<Restaurant> update(@Valid @RequestBody Restaurant restaurant, @PathVariable int id) {
        log.info("update {} with id={}", restaurant, id);
        Optional<RestaurantTo> existed = restaurantRepository.getWithVoteCount(id);
        if (existed.isPresent() && existed.get().votesCount() > 0) {
            throw new DataConflictException("Restaurant with votes for today cannot be changed");
        }
        restaurant.setId(id);
        Restaurant stored = restaurantRepository.saveAndFlush(restaurant);
        if (stored.getId() == id) {
            return RestUtil.emptyResponse();
        } else {
            return RestUtil.buildResponse(stored, REST_URL);
        }
    }

    @Operation(summary = "Delete restaurant")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "restaurants", allEntries = true)
    public void delete(@PathVariable int id) {
        log.info("delete restaurant with id = {}", id);
        RestaurantTo restaurant = restaurantRepository.getWithVoteCountExisted(id);
        if (restaurant.votesCount() > 0) {
            throw new DataConflictException("Restaurant with votes for today cannot be deleted");
        }
        restaurantRepository.delete(id);
    }

    @Operation(summary = "Get restaurant menu")
    @GetMapping("/{id}/menu")
    public List<Dish> getMenu(@PathVariable int id) {
        log.info("get menu for restaurantId = " + id);
        return dishRepository.getAll(id);
    }

    @Operation(summary = "Get restaurant menu on dates")
    @GetMapping("/{id}/menu/filter")
    public List<Dish> getMenuOnDates(@PathVariable int id,
                                     @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "Start date inclusive", example = "2022-07-15") LocalDate startDate,
                                     @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "End date exclusive", example = "2022-07-16") LocalDate endDate) {
        log.info("get menu for restaurantId = {}, startDate = {}, endDate = {}", id, startDate, endDate);
        return dishRepository.getBetweenHalfOpen(id, startDate, endDate);
    }

    @Operation(summary = "Get dish by ID")
    @GetMapping("/{id}/menu/{dish_id}")
    public Dish getDish(@PathVariable int id, @PathVariable("dish_id") int dishId) {
        log.info("get dish with id = {}", dishId);
        return dishRepository.getExisted(dishId, id);
    }

    @Operation(summary = "Create a new dish")
    @PostMapping(value = "/{id}/menu", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(value = "dishes", key = "#id")
    public ResponseEntity<Dish> createDish(@RequestBody @Valid Dish dish, @PathVariable int id) {
        log.info("create {}", dish);
        dish.checkNew();
        dish.setRestaurant(new Restaurant(id));
        Dish created = dishRepository.save(dish);
        return RestUtil.buildResponse(created, String.format("%s/%s/menu", REST_URL, id));
    }

    @Operation(summary = "Update dish by ID")
    @PutMapping(value = "/{id}/menu/{dish_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = "dishes", key = "#id")
    public ResponseEntity<Dish> updateDish(@Valid @RequestBody Dish dish, @PathVariable int id, @PathVariable("dish_id") int dishId) {
        log.info("update {} with id={}", dish, dish);
        RestaurantTo restaurant = restaurantRepository.getWithVoteCountExisted(id);
        if (restaurant.votesCount() > 0) {
            throw new DataConflictException("Dish with votes for today cannot be updated");
        }
        dish.setId(dishId);
        dish.setRestaurant(new Restaurant(id));
        Dish stored = dishRepository.saveAndFlush(dish);
        if (stored.getId() == id) {
            return RestUtil.emptyResponse();
        } else {
            return RestUtil.buildResponse(stored, String.format("%s/%s/menu", REST_URL, id));
        }
    }

    @Operation(summary = "Delete dish by ID")
    @DeleteMapping("/{id}/menu/{dish_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "dishes", key = "#id")
    public void deleteDish(@PathVariable int id, @PathVariable("dish_id") int dishId) {
        log.info("delete dish with id = {}", dishId);
        RestaurantTo restaurant = restaurantRepository.getWithVoteCountExisted(id);
        if (restaurant.votesCount() > 0) {
            throw new DataConflictException("Dish from restaurant with votes for today cannot be deleted");
        }
        dishRepository.deleteExisted(dishId, id);
    }
}
