package br.edu.utfpr.td.tsi.agencia.noticias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({ "file:./applicationContext.xml" })
public class Main {

	public static void main(String[] args) {
		System.setProperty("server.servlet.context-path", "/agencia.noticias");
		SpringApplication.run(Main.class, args);
	}
}
