package br.edu.utfpr.td.tsi.agencia.noticias.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.AutorRepository;

@Service
public class AutorServiceImpl implements AutorService {

	@Autowired
	private AutorRepository autorRepository;

	@Override
	public void cadastrar(Autor autor) {
		LocalDate dataNascimento = autor.getDataNascimento();
		LocalDate hoje = LocalDate.now();
		int idade = Period.between(dataNascimento, hoje).getYears();

		if (idade < 18) {
			throw new RuntimeException("Autor menor de idade");
		}
		autor.setId(UUID.randomUUID().toString());
		autorRepository.save(autor);

	}

}
