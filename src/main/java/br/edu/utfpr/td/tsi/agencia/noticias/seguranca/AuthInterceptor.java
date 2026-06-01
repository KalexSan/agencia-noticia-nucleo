package br.edu.utfpr.td.tsi.agencia.noticias.seguranca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Protege rotas administrativas: exige usuário na sessão.
 * Se não houver, redireciona para /login.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

	@Autowired
	private SessaoUtil sessaoUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (sessaoUtil.estaLogado(request.getSession())) {
			return true;
		}
		response.sendRedirect(request.getContextPath() + "/login");
		return false;
	}
}
