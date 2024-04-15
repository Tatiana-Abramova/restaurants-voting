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
import org.springframework.web.bind.annotation.*;
import voting.model.User;
import voting.repository.UserRepository;
import voting.util.RestUtil;
import voting.util.UserUtil;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Tag(name = "2. User Administration")
@RestController
@RequestMapping(value = AdminUserController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminUserController {
    private final Logger log = getLogger(getClass());

    protected static final String REST_URL = "/api/admin/users";

    @Autowired
    private UserRepository repository;

    @Operation(summary = "Get all users")
    @GetMapping
    public List<User> getAll() {
        log.info("getAll");
        return repository.getAll();
    }

    @Operation(summary = "Get user details by ID")
    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        log.info("get user with id = {}", id);
        return repository.getExisted(id);
    }

    @Operation(summary = "Create a new user")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(value = {"users", "auth"}, allEntries = true)
    public ResponseEntity<User> create(@RequestBody @Valid User user) {
        log.info("create {}", user);
        user.checkNew();
        User created = repository.save(UserUtil.prepareToSave(user));
        return RestUtil.buildResponse(created, REST_URL);
    }

    @Operation(summary = "Update user by ID")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = {"users", "auth"}, allEntries = true)
    public ResponseEntity<User> update(@Valid @RequestBody User user, @PathVariable int id) {
        log.info("update {} with id={}", user, id);
        user.setId(id);
        User stored = repository.saveAndFlush(UserUtil.prepareToSave(user));
        if (stored.getId() == id) {
            return RestUtil.emptyResponse();
        } else {
            return RestUtil.buildResponse(stored, REST_URL);
        }
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = {"users", "auth"}, allEntries = true)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        repository.deleteExisted(id);
    }
}
