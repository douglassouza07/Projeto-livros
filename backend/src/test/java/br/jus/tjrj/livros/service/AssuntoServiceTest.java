package br.jus.tjrj.livros.service;

import br.jus.tjrj.livros.dto.AssuntoRequest;
import br.jus.tjrj.livros.dto.AssuntoResponse;
import br.jus.tjrj.livros.entity.AssuntoEntity;
import br.jus.tjrj.livros.exception.NotFoundException;
import br.jus.tjrj.livros.exception.ValidacaoException;
import br.jus.tjrj.livros.repository.AssuntoRepository;
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
@DisplayName("AssuntoService")
class AssuntoServiceTest {

    @Mock
    private AssuntoRepository repository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private AssuntoService service;

    private AssuntoEntity assunto;

    @BeforeEach
    void setUp() {
        assunto = AssuntoEntity.builder().descricao("Ficção").build();
        assunto.setId(1);
    }

    // ─── list ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("list() deve retornar lista de AssuntoResponse")
    void list_deveRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(assunto));

        List<AssuntoResponse> result = service.list();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).descricao()).isEqualTo("Ficção");
    }

    @Test
    @DisplayName("list() deve retornar lista vazia quando não há assuntos")
    void list_deveRetornarVazio() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.list()).isEmpty();
    }

    // ─── get ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("get() deve retornar AssuntoResponse para id existente")
    void get_deveRetornarAssunto() {
        when(repository.findById(1L)).thenReturn(Optional.of(assunto));

        AssuntoResponse response = service.get(1L);

        assertThat(response.id()).isEqualTo(1);
        assertThat(response.descricao()).isEqualTo("Ficção");
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
    @DisplayName("create() deve persistir e retornar o novo assunto")
    void create_devePersistirAssunto() {
        AssuntoRequest req = new AssuntoRequest("Aventura");
        AssuntoEntity salvo = AssuntoEntity.builder().descricao("Aventura").build();
        salvo.setId(2);

        when(repository.save(any(AssuntoEntity.class))).thenReturn(salvo);

        AssuntoResponse response = service.create(req);

        assertThat(response.descricao()).isEqualTo("Aventura");
        assertThat(response.id()).isEqualTo(2);
        verify(repository).save(any(AssuntoEntity.class));
    }

    // ─── update ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("update() deve atualizar descrição e retornar o assunto atualizado")
    void update_deveAtualizarDescricao() {
        AssuntoRequest req = new AssuntoRequest("Romance");
        AssuntoEntity atualizado = AssuntoEntity.builder().descricao("Romance").build();
        atualizado.setId(1);

        when(repository.findById(1L)).thenReturn(Optional.of(assunto));
        when(repository.save(any())).thenReturn(atualizado);

        AssuntoResponse response = service.update(1L, req);

        assertThat(response.descricao()).isEqualTo("Romance");
        verify(repository).save(assunto);
    }

    @Test
    @DisplayName("update() deve lançar NotFoundException para id inexistente")
    void update_deveLancarNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, new AssuntoRequest("X")))
                .isInstanceOf(NotFoundException.class);
    }

    // ─── delete ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete() deve remover assunto sem livros vinculados")
    void delete_deveDeletarAssuntoSemLivros() {
        when(livroRepository.findTitulosByAssuntoId(1L)).thenReturn(List.of());
        // findOrThrow é chamado internamente somente no caso de vínculo; aqui deleteById é usado
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete() deve lançar ValidacaoException quando assunto possui livros")
    void delete_deveLancarValidacaoExceptionComLivros() {
        when(livroRepository.findTitulosByAssuntoId(1L))
                .thenReturn(List.of("Dom Casmurro", "Iracema"));

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Dom Casmurro")
                .hasMessageContaining("Iracema");

        verify(repository, never()).deleteById(any());
    }

    // ─── findOrThrow ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("findOrThrow() deve retornar entidade para id existente")
    void findOrThrow_deveRetornarEntidade() {
        when(repository.findById(1L)).thenReturn(Optional.of(assunto));

        AssuntoEntity result = service.findOrThrow(1L);

        assertThat(result).isEqualTo(assunto);
    }

    @Test
    @DisplayName("findOrThrow() deve lançar NotFoundException com mensagem contendo o id")
    void findOrThrow_deveLancarComMensagem() {
        when(repository.findById(55L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findOrThrow(55L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("55");
    }
}
