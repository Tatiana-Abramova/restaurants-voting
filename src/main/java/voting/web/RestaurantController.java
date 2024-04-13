package voting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import voting.config.AppProperties;
import voting.error.InvalidTimeException;
import voting.model.Dish;
import voting.model.Restaurant;
import voting.model.Vote;
import voting.model.VoteId;
import voting.repository.DishRepository;
import voting.repository.RestaurantRepository;
import voting.repository.VoteRepository;
import voting.security.AuthUser;
import voting.to.RestaurantTo;

import java.time.LocalTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Tag(name = "3. Restaurant Controller")
@RestController
@RequestMapping(value = RestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantController {

    private final Logger log = getLogger(getClass());

    protected static final String REST_URL = "/api/profile/restaurants";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private AppProperties properties;

    @Operation(summary = "Get all restaurants")
    @GetMapping
    public List<RestaurantTo> getAll() {
        log.info("getAll");
        return restaurantRepository.getAllWithVoteCount();
    }

    @Operation(summary = "Vote for a restaurant")
    @PutMapping(value = "/{id}/vote")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void vote(@PathVariable int id, @AuthenticationPrincipal AuthUser authUser) {
        log.info("userId = {} votes for restaurantId = {}", authUser.id(), id);
        Vote vote = voteRepository.getByUserId(authUser.id());

        if (vote != null) {
            if (!vote.getId().getRestaurantId().equals(id)) {
                if (LocalTime.now().isAfter(LocalTime.of(properties.getDeadlineHours(), properties.getDeadlineMinutes()))) {
                    throw new InvalidTimeException();
                }
                voteRepository.delete(vote);
            } else {
                return;
            }
        }
        vote = new Vote(new VoteId(id, authUser.id()));
        vote.setRestaurant(new Restaurant(id));
        vote.setUser(authUser.getUser());
        voteRepository.save(vote);
    }

    @Operation(summary = "Get restaurant menu")
    @GetMapping("/{id}/menu")
    public List<Dish> getMenu(@PathVariable int id) {
        log.info("get menu for restaurantId = " + id);
        return dishRepository.getAll(id);
    }
}
