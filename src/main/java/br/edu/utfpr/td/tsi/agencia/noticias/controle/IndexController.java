package br.edu.utfpr.td.tsi.agencia.noticias.controle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

	@GetMapping(value = "/")
	public String index() {
		return "index";
	}
}
