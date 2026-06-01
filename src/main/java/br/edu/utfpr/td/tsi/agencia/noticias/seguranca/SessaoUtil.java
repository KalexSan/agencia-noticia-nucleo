package br.edu.utfpr.td.tsi.agencia.noticias.seguranca;

import org.springframework.stereotype.Component;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Perfil;

import jakarta.servlet.http.HttpSession;

/**
 * Acesso ao usuário logado guardado na HttpSession.
 * O atributo de sessão é "usuario".
 */
@Component
public class SessaoUtil {

	public static final String ATRIBUTO_USUARIO = "usuario";

	public Autor getUsuarioLogado(HttpSession session) {
		if (session == null) {
			return null;
		}
		return (Autor) session.getAttribute(ATRIBUTO_USUARIO);
	}

	public void definirUsuarioLogado(HttpSession session, Autor usuario) {
		session.setAttribute(ATRIBUTO_USUARIO, usuario);
	}

	public void encerrarSessao(HttpSession session) {
		session.invalidate();
	}

	public boolean estaLogado(HttpSession session) {
		return getUsuarioLogado(session) != null;
	}

	public boolean ehAdmin(Autor usuario) {
		return usuario != null && usuario.getPerfil() == Perfil.ADMIN;
	}
}
