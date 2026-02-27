package br.jus.tjrj.livros.service;

import br.jus.tjrj.livros.dto.*;
import br.jus.tjrj.livros.entity.AssuntoEntity;
import br.jus.tjrj.livros.entity.AutorEntity;
import br.jus.tjrj.livros.entity.LivroEntity;
import br.jus.tjrj.livros.exception.NotFoundException;
import br.jus.tjrj.livros.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LivroService")
class LivroServiceTest {

    @Mock
    private LivroRepository repository;

    @Mock
    private AutorService autorService;

    @Mock
    private AssuntoService assuntoService;

    @InjectMocks
    private LivroService service;

    private AutorEntity autor;
    private AssuntoEntity assunto;
    private LivroEntity livro;
    private LivroRequest livroRequest;

    @BeforeEach
    void setUp() {
        autor = AutorEntity.builder().nome("Machado de Assis").build();
        autor.setId(1);

        assunto = AssuntoEntity.builder().descricao("Romance").build();
        assunto.setId(1);

        livro = new LivroEntity();
        livro.setId(1);
        livro.setTitulo("Dom Casmurro");
        livro.setEditora("Garnier");
        livro.setEdicao(1);
        livro.setAnoPublicacao("1899");
        livro.setValor(new BigDecimal("49.90"));
        livro.getAutores().add(autor);
        livro.getAssuntos().add(assunto);

        livroRequest = new LivroRequest(
                "Dom Casmurro", "Garnier", 1, "1899",
                new BigDecimal("49.90"), Set.of(1L), Set.of(1L)
        );
    }

    // ─── list ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("list() deve retornar lista de LivroResponse")
    void list_deveRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(livro));

        List<LivroResponse> result = service.list();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).titulo()).isEqualTo("Dom Casmurro");
        assertThat(result.get(0).autores()).hasSize(1);
        assertThat(result.get(0).assuntos()).hasSize(1);
    }

    @Test
    @DisplayName("list() deve retornar lista vazia quando não há livros")
    void list_deveRetornarVazio() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.list()).isEmpty();
    }

    // ─── get ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("get() deve retornar LivroResponse para id existente")
    void get_deveRetornarLivro() {
        when(repository.findById(1L)).thenReturn(Optional.of(livro));

        LivroResponse response = service.get(1L);

        assertThat(response.titulo()).isEqualTo("Dom Casmurro");
        assertThat(response.editora()).isEqualTo("Garnier");
        assertThat(response.anoPublicacao()).isEqualTo("1899");
        assertThat(response.valor()).isEqualByComparingTo("49.90");
    }

    @Test
    @DisplayName("get() deve lançar NotFoundException para id inexistente")
    void get_deveLancarNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─── create ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("create() deve persistir livro com autores e assuntos")
    void create_devePersistirLivro() {
        when(autorService.findOrThrow(1L)).thenReturn(autor);
        when(assuntoService.findOrThrow(1L)).thenReturn(assunto);
        when(repository.save(any(LivroEntity.class))).thenReturn(livro);

        LivroResponse response = service.create(livroRequest);

        assertThat(response.titulo()).isEqualTo("Dom Casmurro");
        verify(autorService).findOrThrow(1L);
        verify(assuntoService).findOrThrow(1L);
        verify(repository).save(any(LivroEntity.class));
    }

    @Test
    @DisplayName("create() deve lançar NotFoundException se autor não existir")
    void create_deveLancarNotFoundSeAutorInexistente() {
        when(autorService.findOrThrow(1L))
                .thenThrow(new NotFoundException("Autor não encontrado: 1"));

        assertThatThrownBy(() -> service.create(livroRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Autor não encontrado");
    }

    @Test
    @DisplayName("create() deve lançar NotFoundException se assunto não existir")
    void create_deveLancarNotFoundSeAssuntoInexistente() {
        when(autorService.findOrThrow(1L)).thenReturn(autor);
        when(assuntoService.findOrThrow(1L))
                .thenThrow(new NotFoundException("Assunto não encontrado: 1"));

        assertThatThrownBy(() -> service.create(livroRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Assunto não encontrado");
    }

    // ─── update ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("update() deve atualizar os dados do livro")
    void update_deveAtualizarLivro() {
        LivroRequest req = new LivroRequest(
                "Quincas Borba", "Garnier", 2, "1891",
                new BigDecimal("55.00"), Set.of(1L), Set.of(1L)
        );
        LivroEntity atualizado = new LivroEntity();
        atualizado.setId(1);
        atualizado.setTitulo("Quincas Borba");
        atualizado.setEditora("Garnier");
        atualizado.setEdicao(2);
        atualizado.setAnoPublicacao("1891");
        atualizado.setValor(new BigDecimal("55.00"));
        atualizado.getAutores().add(autor);
        atualizado.getAssuntos().add(assunto);

        when(repository.findById(1L)).thenReturn(Optional.of(livro));
        when(autorService.findOrThrow(1L)).thenReturn(autor);
        when(assuntoService.findOrThrow(1L)).thenReturn(assunto);
        when(repository.save(any())).thenReturn(atualizado);

        LivroResponse response = service.update(1L, req);

        assertThat(response.titulo()).isEqualTo("Quincas Borba");
        assertThat(response.edicao()).isEqualTo(2);
    }

    @Test
    @DisplayName("update() deve lançar NotFoundException para id inexistente")
    void update_deveLancarNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, livroRequest))
                .isInstanceOf(NotFoundException.class);
    }

    // ─── delete ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete() deve remover livro existente")
    void delete_deveDeletarLivro() {
        when(repository.findById(1L)).thenReturn(Optional.of(livro));

        service.delete(1L);

        verify(repository).delete(livro);
    }

    @Test
    @DisplayName("delete() deve lançar NotFoundException para id inexistente")
    void delete_deveLancarNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(NotFoundException.class);
    }
}
