package CommunitySIte.demo;

import CommunitySIte.p6spy.P6SpyFormatter;
import com.p6spy.engine.spy.P6SpyOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@SpringBootApplication
public class DemoApplication {
	@PostConstruct
	public void setLogMessageFormat() {
		P6SpyOptions.getActiveInstance().setLogMessageFormat(P6SpyFormatter.class.getName());
	}
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
