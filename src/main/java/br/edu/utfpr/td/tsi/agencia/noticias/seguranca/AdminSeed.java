package br.edu.utfpr.td.tsi.agencia.noticias.seguranca;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Perfil;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.AutorRepository;

/**
 * Cria um ADMIN inicial se a coleção de usuários estiver vazia,
 * para permitir o primeiro login no painel.
 * e-mail: redacaoadmin@nucleo.com.br
 * senha: admin123 (provisória — troque depois)
 */
@Configuration
public class AdminSeed {

	public static final String EMAIL_ADMIN = "redacaoadmin@nucleo.com.br";
	public static final String SENHA_ADMIN = "admin123";

	@Bean
	CommandLineRunner seedAdmin(AutorRepository autorRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (autorRepository.count() > 0) {
				return;
			}
			Autor admin = new Autor();
			admin.setId(UUID.randomUUID().toString());
			admin.setNome("Alex Sander Admin");
			admin.setEmail(EMAIL_ADMIN);
			admin.setSenha(passwordEncoder.encode(SENHA_ADMIN));
			admin.setPerfil(Perfil.ADMIN);
			admin.setDataNascimento(LocalDate.of(1990, 1, 1));
			autorRepository.save(admin);
			System.out.println("[seed] ADMIN inicial criado: " + EMAIL_ADMIN + " / " + SENHA_ADMIN);
		};
	}
}
