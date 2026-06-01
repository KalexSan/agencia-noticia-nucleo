package br.edu.utfpr.td.tsi.agencia.noticias.controle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.seguranca.SessaoUtil;
import br.edu.utfpr.td.tsi.agencia.noticias.service.AutorService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AutorControle {

	@Autowired
	private AutorService autorService;

	@Autowired
	private SessaoUtil sessaoUtil;

	// Listagem pública dos autores.
	@GetMapping(value = "/listarAutores")
	public String listarAutores(Model model) {
		model.addAttribute("autores", autorService.listarTodos());
		return "listarAutores";
	}

	@GetMapping(value = "/cadastrarAutor")
	public String mostrarPaginaCadastroAutor() {
		return "cadastrarAutor";
	}

	@PostMapping(value = "/cadastrarAutor")
	public String receberDadosFormulario(Autor autor, HttpSession session, Model model) {
		// Cadastro de autor é restrito a ADMIN (defesa server-side).
		Autor logado = sessaoUtil.getUsuarioLogado(session);
		if (!sessaoUtil.ehAdmin(logado)) {
			model.addAttribute("motivo", "Apenas administradores podem cadastrar autores.");
			return "erro";
		}
		try {
			autorService.cadastrar(autor);
			return "redirect:/listarAutores";
		} catch (RuntimeException e) {
			model.addAttribute("motivo", e.getMessage());
			return "erro";
		}
	}

}
