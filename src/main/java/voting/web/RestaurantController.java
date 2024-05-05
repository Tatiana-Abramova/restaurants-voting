package voting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import voting.model.Dish;
import voting.model.Restaurant;
import voting.repository.DishRepository;
import voting.repository.RestaurantRepository;

import java.time.LocalDate;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Tag(name = "6. Restaurants Info")
@RestController
@RequestMapping(value = RestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantController {

    private final Logger log = getLogger(getClass());

    protected static final String REST_URL = "/api/restaurants";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DishRepository dishRepository;

    @Operation(summary = "Get all restaurants with dishes on dates")
    @GetMapping
    public List<Restaurant> getAll(
            @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "Start dish date inclusive", example = "2022-07-15") LocalDate startDate,
            @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "End dish date exclusive", example = "2024-07-16") LocalDate endDate
    ) {
        log.info("get all restaurants");
        return restaurantRepository.getAllForDishDates(startDate, endDate);
    }

    @Operation(summary = "Get restaurant menu(s) on dates")
    @GetMapping("/{id}/dishes")
    public List<Dish> getMenuOnDates(@PathVariable int id,
                                     @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "Start date inclusive", example = "2022-07-15") LocalDate startDate,
                                     @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "End date exclusive", example = "2024-07-16") LocalDate endDate) {
        log.info("get menu(s) for restaurantId = {}, startDate = {}, endDate = {}", id, startDate, endDate);
        return dishRepository.getAllForDates(id, startDate, endDate);
    }
}

