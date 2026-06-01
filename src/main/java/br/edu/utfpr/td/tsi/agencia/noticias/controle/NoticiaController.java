package br.edu.utfpr.td.tsi.agencia.noticias.controle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Noticia;
import br.edu.utfpr.td.tsi.agencia.noticias.seguranca.SessaoUtil;
import br.edu.utfpr.td.tsi.agencia.noticias.service.AcaoNaoPermitidaException;
import br.edu.utfpr.td.tsi.agencia.noticias.service.AutorService;
import br.edu.utfpr.td.tsi.agencia.noticias.service.NoticiaService;
import br.edu.utfpr.td.tsi.agencia.noticias.service.SolicitacaoAutorService;

import jakarta.servlet.http.HttpSession;

@Controller
public class NoticiaController {

	@Autowired
	private AutorService autorService;

	@Autowired
	private NoticiaService noticiaService;

	@Autowired
	private SessaoUtil sessaoUtil;

	@Autowired
	private SolicitacaoAutorService solicitacaoService;

	// ===================== ROTAS PÚBLICAS =====================

	@GetMapping(value = "/listarNoticias")
	public String exibirPaginaListarNoticias(
			@RequestParam(required = false) String idAutor, Model model) {
		List<Noticia> noticias = (idAutor != null && !idAutor.isEmpty())
				? noticiaService.listarPublicasPorAutor(idAutor)
				: noticiaService.listarPublicas();
		model.addAttribute("noticias", noticias);
		model.addAttribute("autores", autorService.listarTodos());
		model.addAttribute("idAutorSelecionado", idAutor);
		return "listarNoticias";
	}

	@GetMapping(value = "/detalheNoticia")
	public String detalhe(@RequestParam String idNoticia, Model model) {
		// Detalhe é público: só exibe notícia CONCLUIDA.
		model.addAttribute("noticia", noticiaService.buscarPublicaPorId(idNoticia));
		return "detalheNoticia";
	}

	// ===================== PAINEL =====================

	@GetMapping(value = "/admin")
	public String admin(HttpSession session, Model model) {
		Autor logado = sessaoUtil.getUsuarioLogado(session);
		model.addAttribute("autores", autorService.listarTodos());

		if (sessaoUtil.ehAdmin(logado)) {
			// ADMIN: duas listagens separadas — concluídas e pendentes (aprovar).
			model.addAttribute("noticiasConcluidas", noticiaService.listarConcluidas());
			model.addAttribute("noticiasPendentes", noticiaService.listarPendentes());
			model.addAttribute("solicitacoes", solicitacaoService.listarPendentes());
		} else {
			// AUTOR: vê apenas as próprias matérias.
			model.addAttribute("noticias", noticiaService.listarParaPainel(logado));
		}
		return "admin";
	}

	// ===================== CRIAÇÃO =====================

	@GetMapping(value = "/cadastrarNoticia")
	public String exibirPaginaCadastrarNoticia(Model model) {
		model.addAttribute("autores", autorService.listarTodos());
		return "cadastrarNoticia";
	}

	@PostMapping(value = "/cadastrarNoticia")
	public String cadastrarDocumento(Noticia noticia, HttpSession session, Model model) {
		Autor logado = sessaoUtil.getUsuarioLogado(session);
		try {
			noticiaService.criar(noticia, logado);
			return "redirect:/admin";
		} catch (AcaoNaoPermitidaException e) {
			model.addAttribute("motivo", e.getMessage());
			return "erro";
		}
	}

	// ===================== EDIÇÃO =====================

	@GetMapping(value = "/editarNoticia")
	public String mostrarPaginaEditaNoticia(@RequestParam String idNoticia,
			HttpSession session, Model model) {
		Autor logado = sessaoUtil.getUsuarioLogado(session);

		// Ao abrir, o autor "vê" a edição feita por admin (limpa a notificação).
		noticiaService.marcarVistoPeloAutor(idNoticia, logado);

		model.addAttribute("noticia", noticiaService.buscarPorId(idNoticia));
		model.addAttribute("autores", autorService.listarTodos());
		return "editarNoticia";
	}

	@PostMapping(value = "/editarNoticia")
	public String editaNoticia(@RequestParam String idNoticia, Noticia noticia,
			HttpSession session, Model model) {
		Autor logado = sessaoUtil.getUsuarioLogado(session);
		try {
			noticiaService.editar(idNoticia, noticia, logado);
			return "redirect:/admin";
		} catch (AcaoNaoPermitidaException e) {
			model.addAttribute("motivo", e.getMessage());
			return "erro";
		}
	}

	// ===================== TRANSIÇÕES / EXCLUSÃO (ADMIN) =====================

	@GetMapping(value = "/aprovarNoticia")
	public String aprovar(@RequestParam String idNoticia, HttpSession session, Model model) {
		return executarAcaoAdmin(idNoticia, session, model, Acao.APROVAR);
	}

	@GetMapping(value = "/reabrirNoticia")
	public String reabrir(@RequestParam String idNoticia, HttpSession session, Model model) {
		return executarAcaoAdmin(idNoticia, session, model, Acao.REABRIR);
	}

	@GetMapping(value = "/removerNoticia")
	public String removerDocumentos(@RequestParam String idNoticia, HttpSession session, Model model) {
		return executarAcaoAdmin(idNoticia, session, model, Acao.REMOVER);
	}

	// ===================== HELPERS =====================

	private enum Acao { APROVAR, REABRIR, REMOVER }

	private String executarAcaoAdmin(String idNoticia, HttpSession session, Model model, Acao acao) {
		Autor logado = sessaoUtil.getUsuarioLogado(session);
		try {
			switch (acao) {
				case APROVAR -> noticiaService.aprovar(idNoticia, logado);
				case REABRIR -> noticiaService.reabrir(idNoticia, logado);
				case REMOVER -> noticiaService.remover(idNoticia, logado);
			}
			return "redirect:/admin";
		} catch (AcaoNaoPermitidaException e) {
			model.addAttribute("motivo", e.getMessage());
			return "erro";
		}
	}
}
