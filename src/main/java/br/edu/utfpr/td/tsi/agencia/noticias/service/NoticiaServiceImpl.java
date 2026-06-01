package br.edu.utfpr.td.tsi.agencia.noticias.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Noticia;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Perfil;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.StatusNoticia;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.AutorRepository;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.NoticiaRepository;

@Service
public class NoticiaServiceImpl implements NoticiaService {

	@Autowired
	private NoticiaRepository noticiaRepository;

	@Autowired
	private AutorRepository autorRepository;

	// ===================== CONSULTAS =====================

	@Override
	public Noticia buscarPorId(String id) {
		Noticia noticia = (id == null) ? null : noticiaRepository.findById(id).orElse(null);
		return resolverAutor(noticia);
	}

	@Override
	public Noticia buscarPublicaPorId(String id) {
		Noticia noticia = buscarPorId(id);
		if (noticia == null || noticia.getStatus() != StatusNoticia.CONCLUIDA) {
			return null;
		}
		return noticia;
	}

	@Override
	public List<Noticia> listarPublicas() {
		return resolverAutores(noticiaRepository.findByStatus(StatusNoticia.CONCLUIDA));
	}

	@Override
	public List<Noticia> listarPublicasPorAutor(String autorId) {
		List<Noticia> publicas = listarPublicas();
		publicas.removeIf(n -> n.getAutorId() == null || !n.getAutorId().equals(autorId));
		return publicas;
	}

	@Override
	public List<Noticia> listarParaPainel(Autor logado) {
		exigirLogado(logado);
		List<Noticia> noticias = ehAdmin(logado)
				? noticiaRepository.findAll()
				: noticiaRepository.findByAutorId(logado.getId());
		return resolverAutores(noticias);
	}

	@Override
	public List<Noticia> listarConcluidas() {
		return listarPublicas(); // CONCLUIDA já resolve autor
	}

	@Override
	public List<Noticia> listarPendentes() {
		List<Noticia> pendentes = noticiaRepository.findByStatus(StatusNoticia.EM_PRODUCAO);
		pendentes.addAll(noticiaRepository.findByStatus(StatusNoticia.INATIVA));
		return resolverAutores(pendentes);
	}

	// ===================== CRIAÇÃO =====================

	@Override
	public void criar(Noticia noticia, Autor logado) {
		exigirLogado(logado);

		// Autoria original e imutável.
		noticia.setId(UUID.randomUUID().toString());
		noticia.setAutorId(logado.getId());
		noticia.setDataCriacao(LocalDate.now());
		noticia.setEditadaPorAdmin(false);
		noticia.setVistoPeloAutor(true);

		// Resolve o autor escolhido no form (ou o próprio logado) só para exibição.
		String autorEscolhidoId = (noticia.getAutor() != null) ? noticia.getAutor().getId() : null;

		// Status: AUTOR é forçado a EM_PRODUCAO (defesa server-side);
		// ADMIN pode escolher qualquer um.
		if (ehAdmin(logado)) {
			if (noticia.getStatus() == null) {
				noticia.setStatus(StatusNoticia.EM_PRODUCAO);
			}
			// ADMIN pode atribuir a autoria a outro colaborador (via select do form).
			if (autorEscolhidoId != null) {
				noticia.setAutorId(autorEscolhidoId);
			}
		} else {
			noticia.setStatus(StatusNoticia.EM_PRODUCAO);
			// AUTOR só cria para si mesmo.
			noticia.setAutorId(logado.getId());
		}

		noticia.setAutor(autorRepository.findById(noticia.getAutorId()).orElse(null));
		noticiaRepository.insert(noticia);
	}

	// ===================== EDIÇÃO =====================

	@Override
	public void editar(String idNoticia, Noticia dadosForm, Autor logado) {
		exigirLogado(logado);
		Noticia n = noticiaRepository.findById(idNoticia).orElse(null);
		if (n == null) {
			throw new AcaoNaoPermitidaException("Notícia não encontrada.");
		}
		autorizarEdicao(n, logado);

		// Aplica campos de conteúdo. NUNCA toca em autorId (preserva a autoria).
		n.setTitulo(dadosForm.getTitulo());
		n.setAssunto(dadosForm.getAssunto());
		n.setConteudo(dadosForm.getConteudo());
		n.setUrlImagem(dadosForm.getUrlImagem());

		boolean ehAdmin = ehAdmin(logado);

		// Status: só ADMIN altera; AUTOR mantém o status atual (ignora o que veio do form).
		if (ehAdmin) {
			if (dadosForm.getStatus() != null) {
				n.setStatus(dadosForm.getStatus());
			}
			// ADMIN pode reatribuir autoria explicitamente via select.
			if (dadosForm.getAutor() != null && dadosForm.getAutor().getId() != null) {
				n.setAutorId(dadosForm.getAutor().getId());
			}
		}

		// Rastreabilidade: ADMIN editando notícia de OUTRO autor.
		boolean deOutro = !logado.getId().equals(n.getAutorId());
		if (ehAdmin && deOutro) {
			n.setEditadaPorAdmin(true);
			n.setEditadoPorId(logado.getId());
			n.setEditadoPorNome(logado.getNome());
			n.setDataEdicaoAdmin(LocalDateTime.now());
			n.setVistoPeloAutor(false);
		}

		noticiaRepository.save(n);
	}

	@Override
	public void marcarVistoPeloAutor(String idNoticia, Autor logado) {
		if (logado == null) {
			return;
		}
		Noticia n = noticiaRepository.findById(idNoticia).orElse(null);
		if (n == null || n.isVistoPeloAutor()) {
			return;
		}
		// Só o dono "vê" a notificação; histórico editadoPor* permanece para auditoria.
		if (logado.getId().equals(n.getAutorId())) {
			n.setVistoPeloAutor(true);
			noticiaRepository.save(n);
		}
	}

	// ===================== TRANSIÇÕES DE STATUS =====================

	@Override
	public void aprovar(String idNoticia, Autor logado) {
		exigirLogado(logado);
		exigirAdmin(logado);
		Noticia n = exigirNoticia(idNoticia);
		if (n.getStatus() != StatusNoticia.EM_PRODUCAO) {
			throw new AcaoNaoPermitidaException("Só é possível aprovar notícias em produção.");
		}
		n.setStatus(StatusNoticia.CONCLUIDA);
		noticiaRepository.save(n);
	}

	@Override
	public void reabrir(String idNoticia, Autor logado) {
		exigirLogado(logado);
		exigirAdmin(logado);
		Noticia n = exigirNoticia(idNoticia);
		if (n.getStatus() != StatusNoticia.CONCLUIDA) {
			throw new AcaoNaoPermitidaException("Só é possível reabrir notícias concluídas.");
		}
		n.setStatus(StatusNoticia.EM_PRODUCAO);
		noticiaRepository.save(n);
	}

	@Override
	public void remover(String idNoticia, Autor logado) {
		exigirLogado(logado);
		exigirAdmin(logado);
		exigirNoticia(idNoticia);
		noticiaRepository.deleteById(idNoticia);
	}

	// ===================== AUTORIZAÇÃO =====================

	private void autorizarEdicao(Noticia n, Autor logado) {
		if (ehAdmin(logado)) {
			return; // ADMIN edita qualquer notícia, em qualquer status.
		}
		boolean dono = logado.getId().equals(n.getAutorId());
		if (!dono) {
			throw new AcaoNaoPermitidaException("Você só pode editar as suas próprias notícias.");
		}
		if (n.getStatus() == StatusNoticia.CONCLUIDA) {
			throw new AcaoNaoPermitidaException("Notícia concluída não pode ser editada pelo autor.");
		}
	}

	private void exigirLogado(Autor logado) {
		if (logado == null) {
			throw new AcaoNaoPermitidaException("É necessário estar autenticado.");
		}
	}

	private void exigirAdmin(Autor logado) {
		if (!ehAdmin(logado)) {
			throw new AcaoNaoPermitidaException("Ação restrita a administradores.");
		}
	}

	private Noticia exigirNoticia(String idNoticia) {
		Noticia n = noticiaRepository.findById(idNoticia).orElse(null);
		if (n == null) {
			throw new AcaoNaoPermitidaException("Notícia não encontrada.");
		}
		return n;
	}

	private boolean ehAdmin(Autor autor) {
		return autor != null && autor.getPerfil() == Perfil.ADMIN;
	}

	// ===================== HELPERS =====================

	/** Substitui o autor embarcado pelo registro atual a partir de autorId. */
	private Noticia resolverAutor(Noticia noticia) {
		if (noticia != null && noticia.getAutorId() != null) {
			autorRepository.findById(noticia.getAutorId())
					.ifPresent(noticia::setAutor);
		}
		return noticia;
	}

	private List<Noticia> resolverAutores(List<Noticia> noticias) {
		for (Noticia n : noticias) {
			resolverAutor(n);
		}
		return noticias;
	}
}
