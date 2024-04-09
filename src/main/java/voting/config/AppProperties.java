package voting.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.yaml")
@Getter
public class AppProperties {

    @Value("${voting.deadline.hours}")
    int deadlineHours;

    @Value("${voting.deadline.minutes}")
    int deadlineMinutes;
}
