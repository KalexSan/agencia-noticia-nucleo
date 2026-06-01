package br.edu.utfpr.td.tsi.agencia.noticias.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.SolicitacaoAutor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.StatusSolicitacao;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.AutorRepository;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.SolicitacaoAutorRepository;

@Service
public class SolicitacaoAutorServiceImpl implements SolicitacaoAutorService {

	@Autowired
	private SolicitacaoAutorRepository solicitacaoRepository;

	@Autowired
	private AutorRepository autorRepository;

	@Override
	public void solicitar(SolicitacaoAutor solicitacao) {
		if (solicitacao.getNome() == null || solicitacao.getNome().isBlank()
				|| solicitacao.getEmail() == null || solicitacao.getEmail().isBlank()) {
			throw new RuntimeException("Nome e e-mail são obrigatórios");
		}

		String email = solicitacao.getEmail().trim();

		// E-mail único: não pode já ser de um autor cadastrado...
		if (autorRepository.existsByEmail(email)) {
			throw new RuntimeException("Já existe um autor cadastrado com este e-mail.");
		}
		// ...nem ter uma solicitação pendente com o mesmo e-mail.
		if (solicitacaoRepository.existsByEmailAndStatus(email, StatusSolicitacao.PENDENTE)) {
			throw new RuntimeException("Já existe uma solicitação pendente com este e-mail.");
		}

		solicitacao.setEmail(email);
		solicitacao.setId(UUID.randomUUID().toString());
		solicitacao.setStatus(StatusSolicitacao.PENDENTE);
		solicitacao.setDataSolicitacao(LocalDateTime.now());
		solicitacaoRepository.insert(solicitacao);
	}

	@Override
	public List<SolicitacaoAutor> listarPendentes() {
		return solicitacaoRepository.findByStatus(StatusSolicitacao.PENDENTE);
	}

	@Override
	public SolicitacaoAutor buscarPorId(String id) {
		if (id == null) {
			return null;
		}
		return solicitacaoRepository.findById(id).orElse(null);
	}

	@Override
	public void marcarAprovada(String id) {
		atualizarStatus(id, StatusSolicitacao.APROVADA);
	}

	@Override
	public void rejeitar(String id) {
		atualizarStatus(id, StatusSolicitacao.REJEITADA);
	}

	private void atualizarStatus(String id, StatusSolicitacao novo) {
		SolicitacaoAutor s = solicitacaoRepository.findById(id).orElse(null);
		if (s == null) {
			throw new RuntimeException("Solicitação não encontrada");
		}
		s.setStatus(novo);
		solicitacaoRepository.save(s);
	}
}
