package br.edu.utfpr.td.tsi.agencia.noticias.persistencia;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.SolicitacaoAutor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.StatusSolicitacao;

public interface SolicitacaoAutorRepository extends MongoRepository<SolicitacaoAutor, String> {

	List<SolicitacaoAutor> findByStatus(StatusSolicitacao status);

}
