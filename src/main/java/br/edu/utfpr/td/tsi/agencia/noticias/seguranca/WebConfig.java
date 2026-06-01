package br.edu.utfpr.td.tsi.agencia.noticias.seguranca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	private AuthInterceptor authInterceptor;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// Rotas administrativas / de escrita exigem login.
		// Rotas públicas (/, /listarNoticias, /detalheNoticia, /listarAutores, /login)
		// e estáticos (/css, /js) ficam de fora.
		registry.addInterceptor(authInterceptor)
				.addPathPatterns(
						"/admin",
						"/cadastrarNoticia",
						"/editarNoticia",
						"/removerNoticia",
						"/aprovarNoticia",
						"/reabrirNoticia",
						"/cadastrarAutor",
						"/aprovarSolicitacao",
						"/rejeitarSolicitacao");
		// Obs.: /solicitarAutor é público (visitante envia o pedido).
	}
}
