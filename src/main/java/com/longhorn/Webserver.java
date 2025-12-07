package main.java.com.longhorn;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Webserver {

	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

}
