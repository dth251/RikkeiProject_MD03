package ra.edu;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ra.edu.util.TimezoneUtil;

@SpringBootApplication
public class RikkeiProjectMd03Application {

    @PostConstruct
    public void init() {
        TimezoneUtil.setTimezone();
    }

    public static void main(String[] args) {
        SpringApplication.run(RikkeiProjectMd03Application.class, args);
    }

}
