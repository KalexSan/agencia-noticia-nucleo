package br.edu.utfpr.td.tsi.agencia.noticias.controle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.service.AutorService;

@ExtendWith(MockitoExtension.class)
class AutorControleTest {

    @Mock
    private AutorService autorService;

    @Mock
    private Model model;

    @InjectMocks
    private AutorControle autorControle;

    @Test
    void mostrarPaginaCadastroAutorDeveRetornarTemplate() {
        assertEquals("cadastrarAutor", autorControle.mostrarPaginaCadastroAutor());
    }

    @Test
    void receberDadosFormularioDeveRedirecionarQuandoCadastroSucesso() {
        Autor autor = new Autor();

        String view = autorControle.receberDadosFormulario(autor, model);

        assertEquals("redirect:/", view);
        verify(autorService).cadastrar(autor);
    }

    @Test
    void receberDadosFormularioDeveRetornarErroQuandoExcecao() {
        Autor autor = new Autor();
        RuntimeException erro = new RuntimeException("Autor menor de idade");
        doThrow(erro).when(autorService).cadastrar(autor);

        String view = autorControle.receberDadosFormulario(autor, model);

        assertEquals("erro", view);
        verify(model).addAttribute("motivo", "Autor menor de idade");
    }
}
