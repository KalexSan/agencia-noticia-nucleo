package br.edu.utfpr.td.tsi.agencia.noticias.service;

/**
 * Lançada quando um usuário tenta uma ação que seu perfil/estado não permite
 * (ex.: autor editando notícia de outro, autor aprovando, transição inválida).
 */
public class AcaoNaoPermitidaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AcaoNaoPermitidaException(String mensagem) {
		super(mensagem);
	}
}
