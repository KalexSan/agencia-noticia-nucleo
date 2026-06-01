package br.edu.utfpr.td.tsi.agencia.noticias.controle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.SolicitacaoAutor;
import br.edu.utfpr.td.tsi.agencia.noticias.seguranca.SessaoUtil;
import br.edu.utfpr.td.tsi.agencia.noticias.service.SolicitacaoAutorService;

import jakarta.servlet.http.HttpSession;

@Controller
public class SolicitacaoController {

	@Autowired
	private SolicitacaoAutorService solicitacaoService;

	@Autowired
	private SessaoUtil sessaoUtil;

	// ===================== PÚBLICO =====================

	@PostMapping(value = "/solicitarAutor")
	public String solicitar(SolicitacaoAutor solicitacao, RedirectAttributes redirect) {
		try {
			solicitacaoService.solicitar(solicitacao);
			redirect.addFlashAttribute("solicitacaoEnviada",
					"Pedido enviado! Nossa equipe vai avaliar e entrar em contato.");
		} catch (RuntimeException e) {
			// Em vez de ir para a página de erro, devolve a mensagem para exibir
			// no próprio formulário (alerta abaixo, no lugar do alerta de sucesso).
			redirect.addFlashAttribute("solicitacaoErro", e.getMessage());
		}
		return "redirect:/listarAutores";
	}

	// ===================== ADMIN =====================

	@GetMapping(value = "/aprovarSolicitacao")
	public String aprovar(@RequestParam String idSolicitacao, HttpSession session,
			RedirectAttributes redirect, Model model) {
		if (!ehAdmin(session, model)) {
			return "erro";
		}
		SolicitacaoAutor s = solicitacaoService.buscarPorId(idSolicitacao);
		if (s == null) {
			model.addAttribute("motivo", "Solicitação não encontrada.");
			return "erro";
		}
		// Marca aprovada e abre o cadastro pré-preenchido para o admin definir a senha.
		solicitacaoService.marcarAprovada(idSolicitacao);
		redirect.addFlashAttribute("preNome", s.getNome());
		redirect.addFlashAttribute("preEmail", s.getEmail());
		redirect.addFlashAttribute("preDataNascimento", s.getDataNascimento());
		return "redirect:/cadastrarAutor";
	}

	@GetMapping(value = "/rejeitarSolicitacao")
	public String rejeitar(@RequestParam String idSolicitacao, HttpSession session, Model model) {
		if (!ehAdmin(session, model)) {
			return "erro";
		}
		solicitacaoService.rejeitar(idSolicitacao);
		return "redirect:/admin";
	}

	private boolean ehAdmin(HttpSession session, Model model) {
		Autor logado = sessaoUtil.getUsuarioLogado(session);
		if (!sessaoUtil.ehAdmin(logado)) {
			model.addAttribute("motivo", "Ação restrita a administradores.");
			return false;
		}
		return true;
	}
}
