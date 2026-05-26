package br.edu.utfpr.td.tsi.agencia.noticias.controle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.service.AutorService;

@Controller
public class AutorControle {

	@Autowired
	private AutorService autorService;

	@GetMapping(value = "/cadastrarAutor")
	public String mostrarPaginaCadastroAutor() {
		return "cadastrarAutor";
	}

	@PostMapping(value = "/cadastrarAutor")
	public String receberDadosFormulario(Autor autor, Model model) {
		try {
			autorService.cadastrar(autor);
			return "redirect:/";
		} catch (RuntimeException e) {
			String motivo = e.getMessage();
			model.addAttribute("motivo", motivo);
			return "erro";
		}
	}

}
