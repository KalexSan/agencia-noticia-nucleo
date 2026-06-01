package br.edu.utfpr.td.tsi.agencia.noticias.modelo;

public enum StatusSolicitacao {
	PENDENTE,   // aguardando avaliação do admin
	APROVADA,   // admin cadastrou o autor a partir do pedido
	REJEITADA   // admin recusou o pedido
}
