package br.edu.utfpr.td.tsi.agencia.noticias.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Perfil;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.AutorRepository;

@Service
public class AutorServiceImpl implements AutorService {

	@Autowired
	private AutorRepository autorRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void cadastrar(Autor autor) {
		LocalDate dataNascimento = autor.getDataNascimento();
		LocalDate hoje = LocalDate.now();
		int idade = Period.between(dataNascimento, hoje).getYears();

		if (idade < 18) {
			throw new RuntimeException("Autor menor de idade");
		}

		if (autorRepository.existsByEmail(autor.getEmail())) {
			throw new RuntimeException("Já existe um usuário com este e-mail");
		}

		// Senha é obrigatória no cadastro.
		if (autor.getSenha() == null || autor.getSenha().isBlank()) {
			throw new RuntimeException("A senha é obrigatória");
		}

		// Perfil padrão: AUTOR (cadastro pelo painel cria colaboradores da redação).
		if (autor.getPerfil() == null) {
			autor.setPerfil(Perfil.AUTOR);
		}

		// Senha sempre gravada como hash, nunca em texto puro.
		autor.setSenha(passwordEncoder.encode(autor.getSenha()));

		autor.setId(UUID.randomUUID().toString());
		autorRepository.save(autor);
	}

	@Override
	public List<Autor> listarTodos() {
		return autorRepository.findAll();
	}

	@Override
	public Autor buscarPorId(String id) {
		if (id == null) {
			return null;
		}
		return autorRepository.findById(id).orElse(null);
	}

	@Override
	public Autor autenticar(String email, String senha) {
		if (email == null || senha == null) {
			return null;
		}
		Autor autor = autorRepository.findByEmail(email).orElse(null);
		if (autor == null || autor.getSenha() == null) {
			return null;
		}
		return passwordEncoder.matches(senha, autor.getSenha()) ? autor : null;
	}

}
