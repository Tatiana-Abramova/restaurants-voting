package voting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import voting.error.DataConflictException;
import voting.model.Restaurant;
import voting.repository.RestaurantRepository;
import voting.to.RestaurantTo;
import voting.util.RestUtil;

import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@Tag(name = "Restaurant Administration")
@RestController
@RequestMapping(value = AdminRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminRestaurantController {

    private final Logger log = getLogger(getClass());

    protected static final String REST_URL = "/api/admin/restaurants";

    @Autowired
    private RestaurantRepository repository;

    @Operation(summary = "Get all restaurants")
    @GetMapping
    public List<RestaurantTo> getAll() {
        log.info("getAll");
        return repository.getAllWithVoteCount();
    }

    @Operation(summary = "Get restaurant details by ID")
    @GetMapping("/{id}")
    public Restaurant get(@PathVariable int id) {
        log.info("get restaurant with id = {}", id);
        return repository.getExisted(id);
    }

    @Operation(summary = "Create a new restaurant")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Restaurant> create(@RequestBody @Valid Restaurant restaurant) {
        log.info("create {}", restaurant);
        restaurant.checkNew();
        Restaurant created = repository.save(restaurant);
        return RestUtil.buildResponse(created, REST_URL);
    }

    @Operation(summary = "Update restaurant by ID")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> update(@Valid @RequestBody Restaurant restaurant, @PathVariable int id) {
        log.info("update {} with id={}", restaurant, id);
        Optional<RestaurantTo> existed = repository.getWithVoteCount(id);
        if (existed.isPresent() && existed.get().votesCount() > 0) {
            throw new DataConflictException("Restaurant with votes for today cannot be changed");
        }
        restaurant.setId(id);
        Restaurant stored = repository.saveAndFlush(restaurant);
        if (stored.getId() == id) {
            return RestUtil.emptyResponse();
        } else {
            return RestUtil.buildResponse(stored, REST_URL);
        }
    }

    @Operation(summary = "Delete restaurant")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        RestaurantTo restaurant = repository.getWithVoteCountExisted(id);
        if (restaurant.votesCount() > 0) {
            throw new DataConflictException("Restaurant with votes for today cannot be deleted");
        }
        repository.delete(id);
    }
}
