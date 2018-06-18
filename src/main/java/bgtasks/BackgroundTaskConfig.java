package bgtasks;

        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class BackgroundTaskConfig {

    @Bean
    public BackgroundTasks getSchedulingTasks() {
        return new BackgroundTasks();
    }
}
