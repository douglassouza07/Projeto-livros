package br.jus.tjrj.livros.service;

import br.jus.tjrj.livros.dto.AutorRequest;
import br.jus.tjrj.livros.dto.AutorResponse;
import br.jus.tjrj.livros.entity.AutorEntity;
import br.jus.tjrj.livros.exception.NotFoundException;
import br.jus.tjrj.livros.exception.ValidacaoException;
import br.jus.tjrj.livros.repository.AutorRepository;
import br.jus.tjrj.livros.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AutorService")
class AutorServiceTest {

    @Mock
    private AutorRepository repository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private AutorService service;

    private AutorEntity autor;

    @BeforeEach
    void setUp() {
        autor = AutorEntity.builder()
                .nome("Machado de Assis")
                .build();
        // Simula ID gerado pelo banco
        autor.setId(1);
    }

    // ─── list ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("list() deve retornar lista de AutorResponse")
    void list_deveRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(autor));

        List<AutorResponse> result = service.list();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nome()).isEqualTo("Machado de Assis");
        assertThat(result.get(0).id()).isEqualTo(1);
    }

    @Test
    @DisplayName("list() deve retornar lista vazia quando não há autores")
    void list_deveRetornarVazio() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.list()).isEmpty();
    }

    // ─── get ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("get() deve retornar AutorResponse para id existente")
    void get_deveRetornarAutor() {
        when(repository.findById(1L)).thenReturn(Optional.of(autor));

        AutorResponse response = service.get(1L);

        assertThat(response.id()).isEqualTo(1);
        assertThat(response.nome()).isEqualTo("Machado de Assis");
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
    @DisplayName("create() deve persistir e retornar o novo autor")
    void create_devePersistirAutor() {
        AutorRequest req = new AutorRequest("José de Alencar");
        AutorEntity salvo = AutorEntity.builder().nome("José de Alencar").build();
        salvo.setId(2);

        when(repository.save(any(AutorEntity.class))).thenReturn(salvo);

        AutorResponse response = service.create(req);

        assertThat(response.nome()).isEqualTo("José de Alencar");
        assertThat(response.id()).isEqualTo(2);
        verify(repository).save(any(AutorEntity.class));
    }

    // ─── update ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("update() deve atualizar nome e retornar o autor atualizado")
    void update_deveAtualizarNome() {
        AutorRequest req = new AutorRequest("Machado de Assis Atualizado");
        AutorEntity atualizado = AutorEntity.builder().nome("Machado de Assis Atualizado").build();
        atualizado.setId(1);

        when(repository.findById(1L)).thenReturn(Optional.of(autor));
        when(repository.save(any())).thenReturn(atualizado);

        AutorResponse response = service.update(1L, req);

        assertThat(response.nome()).isEqualTo("Machado de Assis Atualizado");
        verify(repository).save(autor);
    }

    @Test
    @DisplayName("update() deve lançar NotFoundException para id inexistente")
    void update_deveLancarNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, new AutorRequest("X")))
                .isInstanceOf(NotFoundException.class);
    }

    // ─── delete ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete() deve remover autor sem livros vinculados")
    void delete_deveDeletarAutorSemLivros() {
        when(repository.findById(1L)).thenReturn(Optional.of(autor));
        when(livroRepository.findTitulosByAutorId(1L)).thenReturn(List.of());

        service.delete(1L);

        verify(repository).delete(autor);
    }

    @Test
    @DisplayName("delete() deve lançar ValidacaoException quando autor possui livros")
    void delete_deveLancarValidacaoExceptionComLivros() {
        when(livroRepository.findTitulosByAutorId(1L)).thenReturn(List.of("Dom Casmurro", "Quincas Borba"));

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Dom Casmurro")
                .hasMessageContaining("Quincas Borba");

        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("delete() deve lançar NotFoundException para id inexistente")
    void delete_deveLancarNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(NotFoundException.class);
    }

    // ─── findOrThrow ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("findOrThrow() deve retornar entidade para id existente")
    void findOrThrow_deveRetornarEntidade() {
        when(repository.findById(1L)).thenReturn(Optional.of(autor));

        AutorEntity result = service.findOrThrow(1L);

        assertThat(result).isEqualTo(autor);
    }

    @Test
    @DisplayName("findOrThrow() deve lançar NotFoundException com mensagem contendo o id")
    void findOrThrow_deveLancarComMensagem() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findOrThrow(42L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("42");
    }
}
