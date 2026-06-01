package br.edu.utfpr.td.tsi.agencia.noticias.service;

import java.util.List;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Noticia;

/**
 * Concentra TODA a regra de permissão, propriedade e máquina de status.
 * Os controllers passam o usuário logado em cada operação; o serviço decide
 * o que é permitido (lança {@link AcaoNaoPermitidaException} caso não seja).
 */
public interface NoticiaService {

	Noticia buscarPorId(String id);

	/** Notícia pública (com autor resolvido) — apenas se CONCLUIDA, senão null. */
	Noticia buscarPublicaPorId(String id);

	/** Site público: apenas CONCLUIDA, com autor resolvido. */
	List<Noticia> listarPublicas();

	/** Site público filtrado por autor: apenas CONCLUIDA daquele autor. */
	List<Noticia> listarPublicasPorAutor(String autorId);

	/** Painel: ADMIN vê todas; AUTOR vê só as suas. Autor resolvido em cada uma. */
	List<Noticia> listarParaPainel(Autor logado);

	/** Cria a notícia aplicando as regras de status por perfil. */
	void criar(Noticia noticia, Autor logado);

	/** Edita conteúdo aplicando autorização e rastreabilidade de admin. */
	void editar(String idNoticia, Noticia dadosForm, Autor logado);

	/**
	 * Marca a notícia como vista pelo autor (limpa a notificação de edição por admin).
	 * Só tem efeito se o logado for o dono da notícia.
	 */
	void marcarVistoPeloAutor(String idNoticia, Autor logado);

	/** EM_PRODUCAO -> CONCLUIDA (somente ADMIN). */
	void aprovar(String idNoticia, Autor logado);

	/** CONCLUIDA -> EM_PRODUCAO (somente ADMIN). */
	void reabrir(String idNoticia, Autor logado);

	/** Remove a notícia (somente ADMIN). */
	void remover(String idNoticia, Autor logado);

}
