package br.edu.utfpr.td.tsi.agencia.noticias.controle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.seguranca.SessaoUtil;

import jakarta.servlet.http.HttpSession;

/**
 * Disponibiliza o usuário logado em todas as views (chip do cabeçalho,
 * decisões por perfil nos templates).
 */
@ControllerAdvice
public class GlobalControllerAdvice {

	@Autowired
	private SessaoUtil sessaoUtil;

	@ModelAttribute("usuarioLogado")
	public Autor usuarioLogado(HttpSession session) {
		return sessaoUtil.getUsuarioLogado(session);
	}
}
