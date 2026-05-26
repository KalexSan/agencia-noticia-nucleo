package br.edu.utfpr.td.tsi.agencia.noticias.persistencia;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Noticia;

public interface NoticiaRepository extends MongoRepository<Noticia, String>{

}
