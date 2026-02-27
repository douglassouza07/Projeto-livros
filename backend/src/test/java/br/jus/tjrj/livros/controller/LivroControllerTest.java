package br.jus.tjrj.livros.controller;

import br.jus.tjrj.livros.dto.*;
import br.jus.tjrj.livros.config.SecurityConfig;
import br.jus.tjrj.livros.exception.GlobalExceptionHandler;
import br.jus.tjrj.livros.exception.NotFoundException;
import br.jus.tjrj.livros.service.LivroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivroController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("LivroController")
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LivroService service;

    private LivroResponse livroResponse;
    private LivroRequest livroRequest;

    @BeforeEach
    void setUp() {
        Set<AutorResponse> autores = Set.of(new AutorResponse(1, "Machado de Assis"));
        Set<AssuntoResponse> assuntos = Set.of(new AssuntoResponse(1, "Romance"));

        livroResponse = new LivroResponse(
                1, "Dom Casmurro", "Garnier", 1, "1899",
                new BigDecimal("49.90"), autores, assuntos
        );

        livroRequest = new LivroRequest(
                "Dom Casmurro", "Garnier", 1, "1899",
                new BigDecimal("49.90"), Set.of(1L), Set.of(1L)
        );
    }

    // ─── GET /api/livros ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/livros deve retornar 200 com lista de livros")
    void list_deveRetornar200() throws Exception {
        when(service.list()).thenReturn(List.of(livroResponse));

        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$[0].editora").value("Garnier"));
    }

    // ─── GET /api/livros/{id} ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/livros/{id} deve retornar 200 para livro existente")
    void get_deveRetornar200() throws Exception {
        when(service.get(1L)).thenReturn(livroResponse);

        mockMvc.perform(get("/api/livros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.anoPublicacao").value("1899"))
                .andExpect(jsonPath("$.valor").value(49.90));
    }

    @Test
    @DisplayName("GET /api/livros/{id} deve retornar 404 para livro inexistente")
    void get_deveRetornar404() throws Exception {
        when(service.get(99L)).thenThrow(new NotFoundException("Livro não encontrado: 99"));

        mockMvc.perform(get("/api/livros/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Livro não encontrado: 99"));
    }

    // ─── POST /api/livros ────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/livros deve retornar 201 ao criar livro válido")
    void create_deveRetornar201() throws Exception {
        when(service.create(any())).thenReturn(livroResponse);

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"));
    }


    // ─── PUT /api/livros/{id} ────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/livros/{id} deve retornar 200 ao atualizar livro")
    void update_deveRetornar200() throws Exception {
        when(service.update(eq(1L), any())).thenReturn(livroResponse);

        mockMvc.perform(put("/api/livros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"));
    }

    @Test
    @DisplayName("PUT /api/livros/{id} deve retornar 404 para id inexistente")
    void update_deveRetornar404() throws Exception {
        when(service.update(eq(99L), any()))
                .thenThrow(new NotFoundException("Livro não encontrado: 99"));

        mockMvc.perform(put("/api/livros/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequest)))
                .andExpect(status().isNotFound());
    }

    // ─── DELETE /api/livros/{id} ─────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/livros/{id} deve retornar 204 ao remover livro")
    void delete_deveRetornar204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/livros/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/livros/{id} deve retornar 404 para id inexistente")
    void delete_deveRetornar404() throws Exception {
        doThrow(new NotFoundException("Livro não encontrado: 99"))
                .when(service).delete(99L);

        mockMvc.perform(delete("/api/livros/99"))
                .andExpect(status().isNotFound());
    }
}
