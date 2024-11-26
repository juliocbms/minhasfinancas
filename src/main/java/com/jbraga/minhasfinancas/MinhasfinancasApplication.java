package com.jbraga.minhasfinancas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableWebMvc
@CrossOrigin("*")
@RestController
public class MinhasfinancasApplication implements WebMvcConfigurer {

@Override
public void addCorsMappings (CorsRegistry registry){
	registry.addMapping("/**").allowedMethods("GET","POST","PUT","DELETE","OPTIONS");
}
@Autowired
private Environment env;

@GetMapping("/")
public String getAmbient(){
	String ambienteAtual = "PADRÃO (nenhum)";
	if(env.getActiveProfiles().length > 0){
		 ambienteAtual = env.getActiveProfiles()[0];
	}
String appName =	env.getProperty("spring.application.name");

	return  String.format("Ambiente: %s | App Name: %s", ambienteAtual, appName);
}


	public static void main(String[] args) {
		SpringApplication.run(MinhasfinancasApplication.class, args);
	}

}
