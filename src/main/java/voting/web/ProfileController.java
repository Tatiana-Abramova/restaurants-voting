package voting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import voting.model.User;
import voting.repository.UserRepository;
import voting.security.AuthUser;
import voting.to.UserTo;
import voting.to.UserVoteTo;
import voting.to.mapper.UserMapper;
import voting.util.RestUtil;
import voting.util.UserUtil;

import static org.slf4j.LoggerFactory.getLogger;

@Tag(name = "User Profile")
@RestController
@RequestMapping(value = ProfileController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileController {
    private final Logger log = getLogger(getClass());

    protected static final String REST_URL = "/api/profile";

    @Autowired
    private UserRepository repository;

    @Operation(summary = "Get authorized user details")
    @GetMapping()
    public UserVoteTo get(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get user with id = {}", authUser.id());
        return repository.getWithVoteExisted(authUser.id());
    }

    @Operation(summary = "Register a new user. Authorization is not required")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> register(@RequestBody @Valid UserTo userTo) {
        log.info("register {}", userTo);
        userTo.checkNew();
        User created =
                repository.save(UserUtil.prepareToSave(UserMapper.createFromUserTo(userTo)));
        return RestUtil.buildResponse(created, REST_URL);
    }

    @Operation(summary = "Update authorized user details")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody @Valid UserTo userTo, @AuthenticationPrincipal AuthUser authUser) {
        log.info("update {} with id={}", userTo, authUser.id());
        User user = authUser.getUser();
        UserUtil.prepareToSave(UserMapper.updateFromUserTo(user, userTo));
        repository.save(user);
    }
}
