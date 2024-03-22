package voting.web;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import voting.error.NotFoundException;
import voting.model.User;
import voting.repository.UserRepository;
import voting.to.UserTo;
import voting.to.UserVoteTo;
import voting.to.mapper.Mapper;

import java.net.URI;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping(value = UserController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    protected final Logger log = getLogger(getClass());

    static final String REST_URL = "/api/users";

    @Autowired
    protected UserRepository repository;

    @GetMapping
    public List<User> getAll() {
        log.info("getAll");
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @GetMapping("/{id}")
    public UserVoteTo get(@PathVariable int id) {
        log.info("Get user with userId: " + id);
        return repository.getWithVote(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> register(@RequestBody @Valid UserTo userTo) {
        log.info("register {}", userTo);
        if (userTo.id() != null) {
            throw new IllegalArgumentException("User must be new");
        }
        User created = repository.save(Mapper.createFromUserTo(userTo));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL).build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void update(@RequestBody @Valid UserTo userTo) {
        log.info("update {}", userTo);
        try {
            User user = repository.getReferenceById(userTo.id());
            Mapper.updateFromUserTo(user, userTo);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(String.format("User with id=%s not found", userTo.id()));
        }
    }
}
