import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ConditionalMailHealthIndicator implements HealthIndicator {

    @Value("${spring.mail.username:}")
    private String username;

    @Value("${spring.mail.password:}")
    private String password;

    @Override
    public Health health() {
        if (username.isEmpty() || username.contains("<<your-email>>") || password.isEmpty() || password.contains("<<your-secret>>"))
            return Health.up().withDetail("Mail Health Check", "Skipped due to missing credentials").build();
        
        return Health.up().build();
    }
}
