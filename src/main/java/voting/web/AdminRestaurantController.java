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
import voting.model.Restaurant;
import voting.repository.RestaurantRepository;
import voting.to.RestaurantTo;
import voting.util.RestUtil;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Tag(name = "4. Restaurant Administration")
@RestController
@RequestMapping(value = AdminRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminRestaurantController {

    private final Logger log = getLogger(getClass());

    protected static final String REST_URL = "/api/admin/restaurants";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Operation(summary = "Get all restaurants with votes count")
    @GetMapping
    public List<RestaurantTo> getAll() {
        log.info("get all restaurants");
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
        return RestUtil.buildResponse(created, String.format("%s/%s", REST_URL, created.getId()));
    }

    @Operation(summary = "Update restaurant by ID")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = "restaurants", allEntries = true)
    @Transactional
    public void update(@Valid @RequestBody Restaurant restaurant, @PathVariable int id) {
        log.info("update {} with id = {}", restaurant, id);
        restaurantRepository.checkExisted(id);
        restaurant.setId(id);
        restaurantRepository.save(restaurant);
    }

    @Operation(summary = "Delete restaurant")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "restaurants", allEntries = true)
    public void delete(@PathVariable int id) {
        log.info("delete restaurant with id = {}", id);
        restaurantRepository.deleteExisted(id);
    }
}
