package br.edu.utfpr.td.tsi.agencia.noticias.service;

import java.util.List;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;

public interface AutorService {

	void cadastrar(Autor autor);

	List<Autor> listarTodos();

	Autor buscarPorId(String id);

	/**
	 * Valida e-mail + senha. Retorna o usuário se as credenciais conferem,
	 * ou null caso contrário.
	 */
	Autor autenticar(String email, String senha);

}
