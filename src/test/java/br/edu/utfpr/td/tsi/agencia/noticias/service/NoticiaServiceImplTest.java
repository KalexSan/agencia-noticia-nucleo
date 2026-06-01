package br.edu.utfpr.td.tsi.agencia.noticias.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Noticia;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Perfil;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.StatusNoticia;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.AutorRepository;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.NoticiaRepository;

@ExtendWith(MockitoExtension.class)
class NoticiaServiceImplTest {

    @Mock
    private NoticiaRepository noticiaRepository;

    @Mock
    private AutorRepository autorRepository;

    @InjectMocks
    private NoticiaServiceImpl noticiaService;

    private Autor autor(String id, Perfil perfil) {
        Autor a = new Autor();
        a.setId(id);
        a.setNome("Nome " + id);
        a.setPerfil(perfil);
        return a;
    }

    // ---------- CRIAÇÃO ----------

    @Test
    void criarComoAutorForcaEmProducaoEAutoriaPropria() {
        Autor logado = autor("autor-1", Perfil.AUTOR);
        Noticia noticia = new Noticia();
        noticia.setStatus(StatusNoticia.CONCLUIDA); // tentativa de burlar

        noticiaService.criar(noticia, logado);

        ArgumentCaptor<Noticia> captor = ArgumentCaptor.forClass(Noticia.class);
        verify(noticiaRepository).insert(captor.capture());
        Noticia salva = captor.getValue();
        assertEquals(StatusNoticia.EM_PRODUCAO, salva.getStatus());
        assertEquals("autor-1", salva.getAutorId());
    }

    @Test
    void criarComoAdminRespeitaStatusEscolhido() {
        Autor logado = autor("admin-1", Perfil.ADMIN);
        Noticia noticia = new Noticia();
        noticia.setStatus(StatusNoticia.CONCLUIDA);

        noticiaService.criar(noticia, logado);

        ArgumentCaptor<Noticia> captor = ArgumentCaptor.forClass(Noticia.class);
        verify(noticiaRepository).insert(captor.capture());
        assertEquals(StatusNoticia.CONCLUIDA, captor.getValue().getStatus());
    }

    // ---------- EDIÇÃO / RASTREABILIDADE ----------

    @Test
    void autorNaoPodeEditarNoticiaDeOutro() {
        Autor logado = autor("autor-1", Perfil.AUTOR);
        Noticia existente = new Noticia();
        existente.setAutorId("autor-2");
        existente.setStatus(StatusNoticia.EM_PRODUCAO);
        when(noticiaRepository.findById("n1")).thenReturn(Optional.of(existente));

        assertThrows(AcaoNaoPermitidaException.class,
                () -> noticiaService.editar("n1", new Noticia(), logado));
        verify(noticiaRepository, never()).save(any());
    }

    @Test
    void autorNaoPodeEditarConcluida() {
        Autor logado = autor("autor-1", Perfil.AUTOR);
        Noticia existente = new Noticia();
        existente.setAutorId("autor-1");
        existente.setStatus(StatusNoticia.CONCLUIDA);
        when(noticiaRepository.findById("n1")).thenReturn(Optional.of(existente));

        assertThrows(AcaoNaoPermitidaException.class,
                () -> noticiaService.editar("n1", new Noticia(), logado));
    }

    @Test
    void adminEditandoNoticiaDeOutroMarcaRastreabilidade() {
        Autor admin = autor("admin-1", Perfil.ADMIN);
        Noticia existente = new Noticia();
        existente.setAutorId("autor-2");
        existente.setStatus(StatusNoticia.EM_PRODUCAO);
        when(noticiaRepository.findById("n1")).thenReturn(Optional.of(existente));

        Noticia form = new Noticia();
        form.setTitulo("Novo título");
        noticiaService.editar("n1", form, admin);

        ArgumentCaptor<Noticia> captor = ArgumentCaptor.forClass(Noticia.class);
        verify(noticiaRepository).save(captor.capture());
        Noticia salva = captor.getValue();
        assertTrue(salva.isEditadaPorAdmin());
        assertEquals("admin-1", salva.getEditadoPorId());
        assertFalse(salva.isVistoPeloAutor());
        assertEquals("autor-2", salva.getAutorId()); // autoria preservada
    }

    @Test
    void adminEditandoPropriaNoticiaNaoMarcaRastreabilidade() {
        Autor admin = autor("admin-1", Perfil.ADMIN);
        Noticia existente = new Noticia();
        existente.setAutorId("admin-1");
        existente.setStatus(StatusNoticia.EM_PRODUCAO);
        when(noticiaRepository.findById("n1")).thenReturn(Optional.of(existente));

        noticiaService.editar("n1", new Noticia(), admin);

        ArgumentCaptor<Noticia> captor = ArgumentCaptor.forClass(Noticia.class);
        verify(noticiaRepository).save(captor.capture());
        assertFalse(captor.getValue().isEditadaPorAdmin());
    }

    // ---------- TRANSIÇÕES ----------

    @Test
    void aprovarPorAutorEhProibido() {
        Autor autor = autor("autor-1", Perfil.AUTOR);
        assertThrows(AcaoNaoPermitidaException.class, () -> noticiaService.aprovar("n1", autor));
    }

    @Test
    void aprovarPromoveEmProducaoParaConcluida() {
        Autor admin = autor("admin-1", Perfil.ADMIN);
        Noticia n = new Noticia();
        n.setStatus(StatusNoticia.EM_PRODUCAO);
        when(noticiaRepository.findById("n1")).thenReturn(Optional.of(n));

        noticiaService.aprovar("n1", admin);

        assertEquals(StatusNoticia.CONCLUIDA, n.getStatus());
        verify(noticiaRepository).save(n);
    }

    @Test
    void reabrirVoltaConcluidaParaEmProducao() {
        Autor admin = autor("admin-1", Perfil.ADMIN);
        Noticia n = new Noticia();
        n.setStatus(StatusNoticia.CONCLUIDA);
        when(noticiaRepository.findById("n1")).thenReturn(Optional.of(n));

        noticiaService.reabrir("n1", admin);

        assertEquals(StatusNoticia.EM_PRODUCAO, n.getStatus());
    }

    @Test
    void removerPorAutorEhProibido() {
        Autor autor = autor("autor-1", Perfil.AUTOR);
        assertThrows(AcaoNaoPermitidaException.class, () -> noticiaService.remover("n1", autor));
        verify(noticiaRepository, never()).deleteById("n1");
    }

    // ---------- VISIBILIDADE ----------

    @Test
    void buscarPublicaPorIdRetornaNullSeNaoConcluida() {
        Noticia n = new Noticia();
        n.setStatus(StatusNoticia.EM_PRODUCAO);
        when(noticiaRepository.findById("n1")).thenReturn(Optional.of(n));

        assertNull(noticiaService.buscarPublicaPorId("n1"));
    }

    @Test
    void buscarPublicaPorIdRetornaNoticiaSeConcluida() {
        Noticia n = new Noticia();
        n.setStatus(StatusNoticia.CONCLUIDA);
        when(noticiaRepository.findById("n1")).thenReturn(Optional.of(n));

        assertEquals(n, noticiaService.buscarPublicaPorId("n1"));
    }
}
