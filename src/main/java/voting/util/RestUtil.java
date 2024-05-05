package voting.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class RestUtil {

    public static <T> ResponseEntity<T> buildResponse(T entity, String url) {
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(url).build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(entity);
    }
}
