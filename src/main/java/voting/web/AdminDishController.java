package voting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import voting.model.Dish;
import voting.model.Restaurant;
import voting.repository.DishRepository;
import voting.repository.RestaurantRepository;
import voting.util.RestUtil;

import static org.slf4j.LoggerFactory.getLogger;

@Tag(name = "5. Restaurant Dishes Administration")
@RestController
@RequestMapping(value = AdminDishController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminDishController {

    private final Logger log = getLogger(getClass());

    protected static final String BASE_REST_URL = "/api/admin/restaurants";

    protected static final String REST_URL = BASE_REST_URL + "/{id}/dishes";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DishRepository dishRepository;

    @Operation(summary = "Get dish by ID")
    @GetMapping("/{dish_id}")
    public Dish get(@PathVariable int id, @PathVariable("dish_id") int dishId) {
        log.info("get dish with id = {}", dishId);
        return dishRepository.getExisted(dishId, id);
    }

    @Operation(summary = "Create a new dish")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(value = "dishes", key = "#id")
    public ResponseEntity<Dish> create(@RequestBody @Valid Dish dish, @PathVariable int id) {
        log.info("create {}", dish);
        dish.checkNew();
        dish.setRestaurant(new Restaurant(id));
        Dish created = dishRepository.save(dish);
        return RestUtil.buildResponse(created, String.format("%s/%s/dishes/%s", BASE_REST_URL, id, dish.getId()));
    }

    @Operation(summary = "Update dish by ID")
    @PutMapping(value = "/{dish_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = "dishes", key = "#id")
    @Transactional
    public void update(@Valid @RequestBody Dish dish, @PathVariable int id, @PathVariable("dish_id") int dishId) {
        log.info("update {} with id = {}", dish, dish);
        dishRepository.getExisted(dishId, id);
        dish.setId(dishId);
        dish.setRestaurant(new Restaurant(id));
        dishRepository.save(dish);
    }

    @Operation(summary = "Delete dish by ID")
    @DeleteMapping("/{dish_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "dishes", key = "#id")
    public void delete(@PathVariable int id, @PathVariable("dish_id") int dishId) {
        log.info("delete dish with id = {}", dishId);
        dishRepository.deleteExisted(dishId, id);
    }
}
