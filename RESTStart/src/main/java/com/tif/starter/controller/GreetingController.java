package com.tif.starter.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tif.starter.bean.Greeting;


@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	
	//Atomic could make sure under the multiple threads access, 
	//the variable could 
	private final AtomicLong counter =new AtomicLong();
	
	@RequestMapping("/greeting")
	public Greeting greeting(@RequestParam(value="name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(),
							String.format(template, name));
	}
	

	
	
	@RequestMapping(method = RequestMethod.POST, value = "/greeting")
	@ResponseBody
	public Greeting greeting(@RequestBody Greeting greeting) {
		System.out.println("POST received!");
		return new Greeting(counter.incrementAndGet(),"POST");
	}

}
