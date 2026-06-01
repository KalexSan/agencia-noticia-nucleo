package br.edu.utfpr.td.tsi.agencia.noticias.persistencia;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Noticia;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.StatusNoticia;

public interface NoticiaRepository extends MongoRepository<Noticia, String> {

	List<Noticia> findByStatus(StatusNoticia status);   // site público (CONCLUIDA)

	List<Noticia> findByAutorId(String autorId);        // painel do autor

}
