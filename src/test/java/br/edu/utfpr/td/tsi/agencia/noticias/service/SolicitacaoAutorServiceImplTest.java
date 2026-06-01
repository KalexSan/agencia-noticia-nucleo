package br.edu.utfpr.td.tsi.agencia.noticias.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.SolicitacaoAutor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.StatusSolicitacao;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.SolicitacaoAutorRepository;

@ExtendWith(MockitoExtension.class)
class SolicitacaoAutorServiceImplTest {

    @Mock
    private SolicitacaoAutorRepository solicitacaoRepository;

    @InjectMocks
    private SolicitacaoAutorServiceImpl service;

    @Test
    void solicitarDeveDefinirIdStatusPendenteEData() {
        SolicitacaoAutor s = new SolicitacaoAutor();
        s.setNome("Helena");
        s.setEmail("helena@exemplo.com");

        service.solicitar(s);

        ArgumentCaptor<SolicitacaoAutor> captor = ArgumentCaptor.forClass(SolicitacaoAutor.class);
        verify(solicitacaoRepository).insert(captor.capture());
        SolicitacaoAutor salva = captor.getValue();
        assertNotNull(salva.getId());
        assertEquals(StatusSolicitacao.PENDENTE, salva.getStatus());
        assertNotNull(salva.getDataSolicitacao());
    }

    @Test
    void solicitarDeveFalharSemNomeOuEmail() {
        SolicitacaoAutor s = new SolicitacaoAutor();
        s.setNome("  ");
        s.setEmail("");

        assertThrows(RuntimeException.class, () -> service.solicitar(s));
        verify(solicitacaoRepository, never()).insert(s);
    }

    @Test
    void marcarAprovadaDeveAtualizarStatus() {
        SolicitacaoAutor s = new SolicitacaoAutor();
        s.setStatus(StatusSolicitacao.PENDENTE);
        when(solicitacaoRepository.findById("s1")).thenReturn(Optional.of(s));

        service.marcarAprovada("s1");

        assertEquals(StatusSolicitacao.APROVADA, s.getStatus());
        verify(solicitacaoRepository).save(s);
    }

    @Test
    void rejeitarDeveAtualizarStatus() {
        SolicitacaoAutor s = new SolicitacaoAutor();
        s.setStatus(StatusSolicitacao.PENDENTE);
        when(solicitacaoRepository.findById("s1")).thenReturn(Optional.of(s));

        service.rejeitar("s1");

        assertEquals(StatusSolicitacao.REJEITADA, s.getStatus());
    }

    @Test
    void aprovarInexistenteDeveLancarExcecao() {
        when(solicitacaoRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.marcarAprovada("x"));
    }
}
