package br.edu.utfpr.td.tsi.agencia.noticias.service;

import java.util.List;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.SolicitacaoAutor;

public interface SolicitacaoAutorService {

	/** Registra um novo pedido público (status PENDENTE). */
	void solicitar(SolicitacaoAutor solicitacao);

	List<SolicitacaoAutor> listarPendentes();

	SolicitacaoAutor buscarPorId(String id);

	/** Marca a solicitação como APROVADA (o cadastro do autor é feito em seguida). */
	void marcarAprovada(String id);

	/** Marca a solicitação como REJEITADA. */
	void rejeitar(String id);

}
