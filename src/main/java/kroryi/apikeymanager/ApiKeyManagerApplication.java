package kroryi.apikeymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// 서비스/컨트롤러가 있는 외부 패키지 등록
@EnableJpaRepositories(basePackages = {
        "kroryi.apikeymanager.repository"
})
@EntityScan(basePackages = {
        "kroryi.apikeymanager.entity"
})
public class ApiKeyManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiKeyManagerApplication.class, args);
    }

}
