package tif.SpringBoot.ReactiveWeb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@SpringBootApplication
public class ReactiveWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveWebApplication.class, args);
	}



}
