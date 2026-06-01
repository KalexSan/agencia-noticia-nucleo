package br.edu.utfpr.td.tsi.agencia.noticias.controle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.edu.utfpr.td.tsi.agencia.noticias.service.NoticiaService;

@Controller
public class IndexController {

	@Autowired
	private NoticiaService noticiaService;

	@GetMapping(value = "/")
	public String index(Model model) {
		// Home pública: apenas notícias CONCLUIDA (autor já resolvido no serviço).
		model.addAttribute("noticias", noticiaService.listarPublicas());
		return "index";
	}
}
