package br.edu.utfpr.td.tsi.agencia.noticias.modelo;

public enum StatusNoticia {
	INATIVA,      // rascunho não publicado
	EM_PRODUCAO,  // em elaboração — visível só para autor/admin
	CONCLUIDA     // pronta — única exibida no site público
}