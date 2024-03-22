package voting.error;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class ErrorMessage {

    private LocalDateTime timestamp;

    private ErrorType type;

    private List<String> messages;

    private String path;
}
