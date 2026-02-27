package br.jus.tjrj.livros.controller;

import br.jus.tjrj.livros.dto.AssuntoRequest;
import br.jus.tjrj.livros.dto.AssuntoResponse;
import br.jus.tjrj.livros.config.SecurityConfig;
import br.jus.tjrj.livros.exception.GlobalExceptionHandler;
import br.jus.tjrj.livros.exception.NotFoundException;
import br.jus.tjrj.livros.exception.ValidacaoException;
import br.jus.tjrj.livros.service.AssuntoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssuntoController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("AssuntoController")
class AssuntoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AssuntoService service;

    // ─── GET /api/assuntos ───────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/assuntos deve retornar 200 com lista de assuntos")
    void list_deveRetornar200() throws Exception {
        when(service.list()).thenReturn(List.of(
                new AssuntoResponse(1, "Romance"),
                new AssuntoResponse(2, "Aventura")
        ));

        mockMvc.perform(get("/api/assuntos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].descricao").value("Romance"));
    }

    // ─── GET /api/assuntos/{id} ──────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/assuntos/{id} deve retornar 200 para assunto existente")
    void get_deveRetornar200() throws Exception {
        when(service.get(1L)).thenReturn(new AssuntoResponse(1, "Romance"));

        mockMvc.perform(get("/api/assuntos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descricao").value("Romance"));
    }

    @Test
    @DisplayName("GET /api/assuntos/{id} deve retornar 404 para assunto inexistente")
    void get_deveRetornar404() throws Exception {
        when(service.get(99L)).thenThrow(new NotFoundException("Assunto não encontrado: 99"));

        mockMvc.perform(get("/api/assuntos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ─── POST /api/assuntos ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/assuntos deve retornar 201 ao criar assunto válido")
    void create_deveRetornar201() throws Exception {
        AssuntoRequest req = new AssuntoRequest("Ficção");
        when(service.create(any())).thenReturn(new AssuntoResponse(3, "Ficção"));

        mockMvc.perform(post("/api/assuntos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Ficção"));
    }


    // ─── PUT /api/assuntos/{id} ──────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/assuntos/{id} deve retornar 200 ao atualizar assunto")
    void update_deveRetornar200() throws Exception {
        AssuntoRequest req = new AssuntoRequest("Poesia");
        when(service.update(eq(1L), any())).thenReturn(new AssuntoResponse(1, "Poesia"));

        mockMvc.perform(put("/api/assuntos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Poesia"));
    }

    @Test
    @DisplayName("PUT /api/assuntos/{id} deve retornar 404 para id inexistente")
    void update_deveRetornar404() throws Exception {
        when(service.update(eq(99L), any()))
                .thenThrow(new NotFoundException("Assunto não encontrado: 99"));

        mockMvc.perform(put("/api/assuntos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"X\"}"))
                .andExpect(status().isNotFound());
    }

    // ─── DELETE /api/assuntos/{id} ───────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/assuntos/{id} deve retornar 204 ao remover assunto")
    void delete_deveRetornar204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/assuntos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/assuntos/{id} deve retornar 400 quando assunto tem livros vinculados")
    void delete_deveRetornar400ComLivrosVinculados() throws Exception {
        doThrow(new ValidacaoException("Assunto não pode ser removido. Vinculado aos livros: Dom Casmurro"))
                .when(service).delete(1L);

        mockMvc.perform(delete("/api/assuntos/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Dom Casmurro")));
    }

    @Test
    @DisplayName("DELETE /api/assuntos/{id} deve retornar 404 para id inexistente")
    void delete_deveRetornar404() throws Exception {
        doThrow(new NotFoundException("Assunto não encontrado: 99"))
                .when(service).delete(99L);

        mockMvc.perform(delete("/api/assuntos/99"))
                .andExpect(status().isNotFound());
    }
}
