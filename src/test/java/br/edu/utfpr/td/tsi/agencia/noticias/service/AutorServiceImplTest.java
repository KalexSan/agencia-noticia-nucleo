package br.edu.utfpr.td.tsi.agencia.noticias.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Perfil;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.AutorRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AutorServiceImplTest {

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AutorServiceImpl autorService;

    @Test
    void cadastrarDeveSalvarAutorComSenhaHashEPerfilAutorQuandoMaiorDeIdade() {
        Autor autor = new Autor();
        autor.setEmail("novo@nucleo.com.br");
        autor.setSenha("segredo");
        autor.setDataNascimento(LocalDate.now().minusYears(25));

        when(autorRepository.existsByEmail("novo@nucleo.com.br")).thenReturn(false);
        when(passwordEncoder.encode("segredo")).thenReturn("hash");

        autorService.cadastrar(autor);

        assertNotNull(autor.getId());
        assertEquals(Perfil.AUTOR, autor.getPerfil());
        assertEquals("hash", autor.getSenha());
        verify(autorRepository).save(autor);
    }

    @Test
    void cadastrarDeveLancarExcecaoQuandoSenhaEmBranco() {
        Autor autor = new Autor();
        autor.setEmail("sem-senha@nucleo.com.br");
        autor.setDataNascimento(LocalDate.now().minusYears(30));

        when(autorRepository.existsByEmail("sem-senha@nucleo.com.br")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> autorService.cadastrar(autor));
        verify(autorRepository, never()).save(autor);
    }

    @Test
    void cadastrarDeveLancarExcecaoQuandoMenorDeIdade() {
        Autor autor = new Autor();
        autor.setDataNascimento(LocalDate.now().minusYears(15));

        assertThrows(RuntimeException.class, () -> autorService.cadastrar(autor));
        verify(autorRepository, never()).save(autor);
    }

    @Test
    void cadastrarDeveLancarExcecaoQuandoEmailJaExiste() {
        Autor autor = new Autor();
        autor.setEmail("existe@nucleo.com.br");
        autor.setDataNascimento(LocalDate.now().minusYears(30));

        when(autorRepository.existsByEmail("existe@nucleo.com.br")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> autorService.cadastrar(autor));
        verify(autorRepository, never()).save(autor);
    }

    @Test
    void autenticarDeveRetornarUsuarioQuandoSenhaConfere() {
        Autor autor = new Autor();
        autor.setEmail("admin@nucleo.com.br");
        autor.setSenha("hash");

        when(autorRepository.findByEmail("admin@nucleo.com.br")).thenReturn(Optional.of(autor));
        when(passwordEncoder.matches("segredo", "hash")).thenReturn(true);

        Autor resultado = autorService.autenticar("admin@nucleo.com.br", "segredo");

        assertEquals(autor, resultado);
    }

    @Test
    void autenticarDeveRetornarNullQuandoSenhaInvalida() {
        Autor autor = new Autor();
        autor.setEmail("admin@nucleo.com.br");
        autor.setSenha("hash");

        when(autorRepository.findByEmail("admin@nucleo.com.br")).thenReturn(Optional.of(autor));
        when(passwordEncoder.matches("errada", "hash")).thenReturn(false);

        assertNull(autorService.autenticar("admin@nucleo.com.br", "errada"));
    }

    @Test
    void autenticarDeveRetornarNullQuandoEmailNaoExiste() {
        when(autorRepository.findByEmail("nao@existe.com")).thenReturn(Optional.empty());

        assertNull(autorService.autenticar("nao@existe.com", "x"));
    }
}
