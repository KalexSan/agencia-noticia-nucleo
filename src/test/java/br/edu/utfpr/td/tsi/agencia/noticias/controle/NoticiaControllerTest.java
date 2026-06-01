package br.edu.utfpr.td.tsi.agencia.noticias.controle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Noticia;
import br.edu.utfpr.td.tsi.agencia.noticias.seguranca.SessaoUtil;
import br.edu.utfpr.td.tsi.agencia.noticias.service.AcaoNaoPermitidaException;
import br.edu.utfpr.td.tsi.agencia.noticias.service.AutorService;
import br.edu.utfpr.td.tsi.agencia.noticias.service.NoticiaService;

import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class NoticiaControllerTest {

    @Mock
    private AutorService autorService;

    @Mock
    private NoticiaService noticiaService;

    @Mock
    private SessaoUtil sessaoUtil;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private NoticiaController noticiaController;

    @Test
    void exibirPaginaCadastrarNoticiaDeveAdicionarAutoresNoModelo() {
        List<Autor> autores = List.of(new Autor());
        when(autorService.listarTodos()).thenReturn(autores);

        String view = noticiaController.exibirPaginaCadastrarNoticia(model);

        assertEquals("cadastrarNoticia", view);
        verify(model).addAttribute("autores", autores);
    }

    @Test
    void cadastrarDocumentoDeveDelegarAoServicoERedirecionarParaAdmin() {
        Noticia noticia = new Noticia();
        Autor logado = new Autor();
        when(sessaoUtil.getUsuarioLogado(session)).thenReturn(logado);

        String view = noticiaController.cadastrarDocumento(noticia, session, model);

        assertEquals("redirect:/admin", view);
        verify(noticiaService).criar(noticia, logado);
    }

    @Test
    void cadastrarDocumentoDeveIrParaErroQuandoAcaoNaoPermitida() {
        Noticia noticia = new Noticia();
        Autor logado = new Autor();
        when(sessaoUtil.getUsuarioLogado(session)).thenReturn(logado);
        org.mockito.Mockito.doThrow(new AcaoNaoPermitidaException("nao pode"))
                .when(noticiaService).criar(noticia, logado);

        String view = noticiaController.cadastrarDocumento(noticia, session, model);

        assertEquals("erro", view);
        verify(model).addAttribute("motivo", "nao pode");
    }

    @Test
    void exibirPaginaListarNoticiasSemFiltroDeveListarPublicas() {
        List<Noticia> publicas = List.of(new Noticia());
        when(noticiaService.listarPublicas()).thenReturn(publicas);
        when(autorService.listarTodos()).thenReturn(List.of());

        String view = noticiaController.exibirPaginaListarNoticias(null, model);

        assertEquals("listarNoticias", view);
        verify(model).addAttribute("noticias", publicas);
        verify(model).addAttribute("idAutorSelecionado", (String) null);
    }

    @Test
    void exibirPaginaListarNoticiasComFiltroDeveListarPublicasPorAutor() {
        List<Noticia> publicas = List.of(new Noticia());
        when(noticiaService.listarPublicasPorAutor("autor-1")).thenReturn(publicas);
        when(autorService.listarTodos()).thenReturn(List.of());

        String view = noticiaController.exibirPaginaListarNoticias("autor-1", model);

        assertEquals("listarNoticias", view);
        verify(model).addAttribute("noticias", publicas);
    }

    @Test
    void detalheDeveAdicionarNoticiaPublicaNoModelo() {
        Noticia noticia = new Noticia();
        when(noticiaService.buscarPublicaPorId("noticia-1")).thenReturn(noticia);

        String view = noticiaController.detalhe("noticia-1", model);

        assertEquals("detalheNoticia", view);
        verify(model).addAttribute("noticia", noticia);
    }

    @Test
    void adminDeveListarParaPainelEAutores() {
        Autor logado = new Autor();
        List<Noticia> noticias = List.of(new Noticia());
        when(sessaoUtil.getUsuarioLogado(session)).thenReturn(logado);
        when(noticiaService.listarParaPainel(logado)).thenReturn(noticias);
        when(autorService.listarTodos()).thenReturn(List.of());

        String view = noticiaController.admin(session, model);

        assertEquals("admin", view);
        verify(model).addAttribute("noticias", noticias);
    }

    @Test
    void removerDocumentosDeveDelegarAoServicoERedirecionar() {
        Autor logado = new Autor();
        when(sessaoUtil.getUsuarioLogado(session)).thenReturn(logado);

        String view = noticiaController.removerDocumentos("noticia-1", session, model);

        assertEquals("redirect:/admin", view);
        verify(noticiaService).remover("noticia-1", logado);
    }

    @Test
    void aprovarDeveDelegarAoServicoERedirecionar() {
        Autor logado = new Autor();
        when(sessaoUtil.getUsuarioLogado(session)).thenReturn(logado);

        String view = noticiaController.aprovar("noticia-1", session, model);

        assertEquals("redirect:/admin", view);
        verify(noticiaService).aprovar("noticia-1", logado);
    }

    @Test
    void reabrirDeveDelegarAoServicoERedirecionar() {
        Autor logado = new Autor();
        when(sessaoUtil.getUsuarioLogado(session)).thenReturn(logado);

        String view = noticiaController.reabrir("noticia-1", session, model);

        assertEquals("redirect:/admin", view);
        verify(noticiaService).reabrir("noticia-1", logado);
    }

    @Test
    void mostrarPaginaEditaNoticiaDeveMarcarVistoEAdicionarNoModelo() {
        Autor logado = new Autor();
        Noticia noticia = new Noticia();
        when(sessaoUtil.getUsuarioLogado(session)).thenReturn(logado);
        when(noticiaService.buscarPorId("noticia-1")).thenReturn(noticia);
        when(autorService.listarTodos()).thenReturn(List.of());

        String view = noticiaController.mostrarPaginaEditaNoticia("noticia-1", session, model);

        assertEquals("editarNoticia", view);
        verify(noticiaService).marcarVistoPeloAutor("noticia-1", logado);
        verify(model).addAttribute("noticia", noticia);
    }

    @Test
    void editaNoticiaDeveDelegarAoServicoERedirecionarParaAdmin() {
        Noticia noticia = new Noticia();
        Autor logado = new Autor();
        when(sessaoUtil.getUsuarioLogado(session)).thenReturn(logado);

        String view = noticiaController.editaNoticia("noticia-1", noticia, session, model);

        assertEquals("redirect:/admin", view);
        verify(noticiaService).editar("noticia-1", noticia, logado);
    }
}
