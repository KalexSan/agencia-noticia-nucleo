package br.edu.utfpr.td.tsi.agencia.noticias.controle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Noticia;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.AutorRepository;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.NoticiaRepository;

@ExtendWith(MockitoExtension.class)
class NoticiaControllerTest {

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private NoticiaRepository noticiaRepository;

    @Mock
    private Model model;

    @InjectMocks
    private NoticiaController noticiaController;

    @Test
    void exibirPaginaCadastrarNoticiaDeveAdicionarAutoresNoModelo() {
        List<Autor> autores = List.of(new Autor());
        when(autorRepository.findAll()).thenReturn(autores);

        String view = noticiaController.exibirPaginaCadastrarNoticia(model);

        assertEquals("cadastrarNoticia", view);
        verify(model).addAttribute("autores", autores);
    }

    @Test
    void cadastrarDocumentoDeveGerarIdDataEInserirNoticia() {
        Noticia noticia = new Noticia();

        String view = noticiaController.cadastrarDocumento(noticia);

        assertEquals("index", view);
        assertNotNull(noticia.getId());
        assertEquals(LocalDate.now(), noticia.getDataCriacao());
        verify(noticiaRepository).insert(noticia);
    }

    @Test
    void exibirPaginaListarNoticiasDeveResolverAutorEmCadaNoticia() {
        Autor autor = new Autor();
        autor.setId("autor-1");

        Noticia noticia = new Noticia();
        Autor autorSomenteComId = new Autor();
        autorSomenteComId.setId("autor-1");
        noticia.setAutor(autorSomenteComId);

        List<Noticia> noticias = List.of(noticia);
        when(noticiaRepository.findAll()).thenReturn(noticias);
        when(autorRepository.findById("autor-1")).thenReturn(Optional.of(autor));

        String view = noticiaController.exibirPaginaListarNoticias(model);

        assertEquals("listarNoticias", view);
        assertEquals(autor, noticia.getAutor());
        verify(model).addAttribute("noticias", noticias);
    }

    @Test
    void removerDocumentosDeveExcluirNoticiaPorId() {
        String view = noticiaController.removerDocumentos("noticia-1");

        assertEquals("index", view);
        verify(noticiaRepository).deleteById("noticia-1");
    }

    @Test
    void mostrarpaginaEditaNoticiaDeveAdicionarNoticiaNoModelo() {
        Noticia noticia = new Noticia();
        when(noticiaRepository.findById("noticia-1")).thenReturn(Optional.of(noticia));

        String view = noticiaController.mostrarpaginaEditaNoticia("noticia-1", model);

        assertEquals("editarNoticia", view);
        verify(model).addAttribute("noticia", noticia);
    }

    @Test
    void editaNoticiaDeveSalvarENavegarParaLista() {
        Noticia noticia = new Noticia();

        String view = noticiaController.editaNoticia("noticia-1", noticia);

        assertEquals("redirect:listarNoticias", view);

        ArgumentCaptor<Noticia> captor = ArgumentCaptor.forClass(Noticia.class);
        verify(noticiaRepository).save(captor.capture());
        assertEquals(noticia, captor.getValue());
    }
}
