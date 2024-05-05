package voting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import voting.config.AppProperties;
import voting.error.InvalidTimeException;
import voting.model.Vote;
import voting.repository.VoteRepository;
import voting.security.AuthUser;
import voting.to.VoteRestaurantTo;
import voting.to.VoteTo;
import voting.to.mapper.VoteMapper;
import voting.util.RestUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@Tag(name = "2. User Voting")
@RestController
@RequestMapping(value = VoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class VoteController {

    private final Logger log = getLogger(getClass());

    protected static final String REST_URL = "/api/profile/votes";

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private AppProperties properties;

    @Operation(summary = "Get user votes")
    @GetMapping
    public List<VoteRestaurantTo> getVotes(@AuthenticationPrincipal AuthUser authUser,
                                           @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "Start date inclusive", example = "2022-07-15") LocalDate startDate,
                                           @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "End date exclusive", example = "2024-07-16") LocalDate endDate) {
        log.info("get votes for userId = {}", authUser.id());
        return voteRepository.getAllByUserId(authUser.id(), startDate, endDate);
    }

    @Operation(summary = "Vote for a restaurant for today")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public ResponseEntity<Vote> vote(@RequestBody @Valid VoteTo voteTo, @AuthenticationPrincipal AuthUser authUser) {
        log.info("userId = {} votes for restaurantId = {}", authUser.id(), voteTo.restaurantId());
        voteTo.checkNew();
        Vote vote = voteRepository.getCurrentByUserId(authUser.id());
        if (vote != null && LocalTime.now().isAfter(LocalTime.of(properties.getDeadlineHours(), properties.getDeadlineMinutes()))) {
            throw new InvalidTimeException();
        }
        Vote created = voteRepository.save(VoteMapper.createFromVoteTo(voteTo, authUser));
        return RestUtil.buildResponse(created, REST_URL + "/votes");
    }

    @Operation(summary = "Re-vote for a restaurant for today")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void reVote(@RequestBody @Valid VoteTo voteTo, @PathVariable int id, @AuthenticationPrincipal AuthUser authUser) {
        log.info("userId = {} votes for restaurantId = {}", authUser.id(), voteTo.restaurantId());
        Optional<Vote> optionalVote = voteRepository.findById(id);
        Vote vote;
        if (optionalVote.isPresent()) {
            vote = optionalVote.get();
            if (!vote.getRestaurant().getId().equals(voteTo.restaurantId())) {
                if (LocalTime.now().isAfter(LocalTime.of(properties.getDeadlineHours(), properties.getDeadlineMinutes()))) {
                    throw new InvalidTimeException();
                }
            }
            VoteMapper.updateFromVoteTo(vote, voteTo);
        } else {
            vote = VoteMapper.createFromVoteTo(voteTo, authUser);
        }
        voteRepository.save(vote);
    }
}
