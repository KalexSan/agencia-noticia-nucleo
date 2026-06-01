package br.edu.utfpr.td.tsi.agencia.noticias.persistencia;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;

public interface AutorRepository extends MongoRepository<Autor, String> {

	Optional<Autor> findByEmail(String email);

	boolean existsByEmail(String email);

}
