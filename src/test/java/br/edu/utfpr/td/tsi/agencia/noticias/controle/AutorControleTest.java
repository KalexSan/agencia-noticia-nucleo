package br.edu.utfpr.td.tsi.agencia.noticias.controle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.seguranca.SessaoUtil;
import br.edu.utfpr.td.tsi.agencia.noticias.service.AutorService;

import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class AutorControleTest {

    @Mock
    private AutorService autorService;

    @Mock
    private SessaoUtil sessaoUtil;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private AutorControle autorControle;

    @Test
    void mostrarPaginaCadastroAutorDeveRetornarTemplate() {
        assertEquals("cadastrarAutor", autorControle.mostrarPaginaCadastroAutor());
    }

    @Test
    void listarAutoresDeveAdicionarListaNoModelo() {
        when(autorService.listarTodos()).thenReturn(java.util.List.of());

        String view = autorControle.listarAutores(model);

        assertEquals("listarAutores", view);
        verify(model).addAttribute("autores", java.util.List.of());
    }

    @Test
    void receberDadosFormularioDeveRedirecionarQuandoAdminECadastroSucesso() {
        Autor autor = new Autor();
        Autor admin = new Autor();
        when(sessaoUtil.getUsuarioLogado(session)).thenReturn(admin);
        when(sessaoUtil.ehAdmin(admin)).thenReturn(true);

        String view = autorControle.receberDadosFormulario(autor, session, model);

        assertEquals("redirect:/listarAutores", view);
        verify(autorService).cadastrar(autor);
    }

    @Test
    void receberDadosFormularioDeveBloquearQuandoNaoAdmin() {
        Autor autor = new Autor();
        when(sessaoUtil.getUsuarioLogado(session)).thenReturn(null);
        when(sessaoUtil.ehAdmin(null)).thenReturn(false);

        String view = autorControle.receberDadosFormulario(autor, session, model);

        assertEquals("erro", view);
        verify(autorService, org.mockito.Mockito.never()).cadastrar(autor);
    }

    @Test
    void receberDadosFormularioDeveRetornarErroQuandoExcecao() {
        Autor autor = new Autor();
        Autor admin = new Autor();
        when(sessaoUtil.getUsuarioLogado(session)).thenReturn(admin);
        when(sessaoUtil.ehAdmin(admin)).thenReturn(true);
        doThrow(new RuntimeException("Autor menor de idade")).when(autorService).cadastrar(autor);

        String view = autorControle.receberDadosFormulario(autor, session, model);

        assertEquals("erro", view);
        verify(model).addAttribute("motivo", "Autor menor de idade");
    }
}
