package br.edu.utfpr.td.tsi.agencia.noticias.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.AutorRepository;

@ExtendWith(MockitoExtension.class)
class AutorServiceImplTest {

    @Mock
    private AutorRepository autorRepository;

    @InjectMocks
    private AutorServiceImpl autorService;

    @Test
    void cadastrarDeveSalvarAutorQuandoMaiorDeIdade() {
        Autor autor = new Autor();
        autor.setDataNascimento(LocalDate.now().minusYears(25));

        ReflectionTestUtils.setField(autorService, "autorRepository", autorRepository);

        autorService.cadastrar(autor);

        assertNotNull(autor.getId());
        verify(autorRepository).save(autor);
    }

    @Test
    void cadastrarDeveLancarExcecaoQuandoMenorDeIdade() {
        Autor autor = new Autor();
        autor.setDataNascimento(LocalDate.now().minusYears(15));

        ReflectionTestUtils.setField(autorService, "autorRepository", autorRepository);

        assertThrows(RuntimeException.class, () -> autorService.cadastrar(autor));
        verify(autorRepository, never()).save(autor);
    }
}
