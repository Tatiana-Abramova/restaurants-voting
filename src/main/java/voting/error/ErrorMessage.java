package voting.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "Error details")
@Value
public class ErrorMessage {

    private LocalDateTime timestamp;

    private ErrorType type;

    private List<String> messages;

    private String path;
}
