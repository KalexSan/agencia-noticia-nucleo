package br.edu.utfpr.td.tsi.agencia.noticias.controle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.seguranca.SessaoUtil;
import br.edu.utfpr.td.tsi.agencia.noticias.service.AutorService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

	@Autowired
	private AutorService autorService;

	@Autowired
	private SessaoUtil sessaoUtil;

	@GetMapping("/login")
	public String exibirLogin(HttpSession session) {
		// Já logado? vai direto ao painel.
		if (sessaoUtil.estaLogado(session)) {
			return "redirect:/admin";
		}
		return "login";
	}

	@PostMapping("/login")
	public String autenticar(@RequestParam String email, @RequestParam String senha,
			HttpSession session, Model model) {
		Autor usuario = autorService.autenticar(email, senha);
		if (usuario == null) {
			model.addAttribute("erroLogin", "E-mail ou senha inválidos.");
			return "login";
		}
		sessaoUtil.definirUsuarioLogado(session, usuario);
		return "redirect:/admin";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		sessaoUtil.encerrarSessao(session);
		return "redirect:/login";
	}
}
