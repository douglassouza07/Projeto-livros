package br.jus.tjrj.livros.controller;

import br.jus.tjrj.livros.dto.AutorRequest;
import br.jus.tjrj.livros.dto.AutorResponse;
import br.jus.tjrj.livros.config.SecurityConfig;
import br.jus.tjrj.livros.exception.GlobalExceptionHandler;
import br.jus.tjrj.livros.exception.NotFoundException;
import br.jus.tjrj.livros.exception.ValidacaoException;
import br.jus.tjrj.livros.service.AutorService;
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

@WebMvcTest(AutorController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("AutorController")
class AutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AutorService service;

    // ─── GET /api/autores ────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/autores deve retornar 200 com lista de autores")
    void list_deveRetornar200() throws Exception {
        when(service.list()).thenReturn(List.of(
                new AutorResponse(1, "Machado de Assis"),
                new AutorResponse(2, "José de Alencar")
        ));

        mockMvc.perform(get("/api/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Machado de Assis"))
                .andExpect(jsonPath("$[1].nome").value("José de Alencar"));
    }

    // ─── GET /api/autores/{id} ───────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/autores/{id} deve retornar 200 para autor existente")
    void get_deveRetornar200() throws Exception {
        when(service.get(1L)).thenReturn(new AutorResponse(1, "Machado de Assis"));

        mockMvc.perform(get("/api/autores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Machado de Assis"));
    }

    @Test
    @DisplayName("GET /api/autores/{id} deve retornar 404 para autor inexistente")
    void get_deveRetornar404() throws Exception {
        when(service.get(99L)).thenThrow(new NotFoundException("Autor não encontrado: 99"));

        mockMvc.perform(get("/api/autores/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Autor não encontrado: 99"));
    }

    // ─── POST /api/autores ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/autores deve retornar 201 ao criar autor válido")
    void create_deveRetornar201() throws Exception {
        AutorRequest req = new AutorRequest("Clarice Lispector");
        when(service.create(any())).thenReturn(new AutorResponse(3, "Clarice Lispector"));

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nome").value("Clarice Lispector"));
    }

    // ─── PUT /api/autores/{id} ───────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/autores/{id} deve retornar 200 ao atualizar autor")
    void update_deveRetornar200() throws Exception {
        AutorRequest req = new AutorRequest("Clarice Lispector Atualizado");
        when(service.update(eq(1L), any())).thenReturn(new AutorResponse(1, "Clarice Lispector Atualizado"));

        mockMvc.perform(put("/api/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Clarice Lispector Atualizado"));
    }

    @Test
    @DisplayName("PUT /api/autores/{id} deve retornar 404 para id inexistente")
    void update_deveRetornar404() throws Exception {
        when(service.update(eq(99L), any()))
                .thenThrow(new NotFoundException("Autor não encontrado: 99"));

        mockMvc.perform(put("/api/autores/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"X\"}"))
                .andExpect(status().isNotFound());
    }

    // ─── DELETE /api/autores/{id} ────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/autores/{id} deve retornar 204 ao remover autor")
    void delete_deveRetornar204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/autores/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/autores/{id} deve retornar 400 quando autor tem livros vinculados")
    void delete_deveRetornar400ComLivrosVinculados() throws Exception {
        doThrow(new ValidacaoException("Autor não pode ser removido. Vinculado aos livros: Dom Casmurro"))
                .when(service).delete(1L);

        mockMvc.perform(delete("/api/autores/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Dom Casmurro")));
    }

    @Test
    @DisplayName("DELETE /api/autores/{id} deve retornar 404 para id inexistente")
    void delete_deveRetornar404() throws Exception {
        doThrow(new NotFoundException("Autor não encontrado: 99"))
                .when(service).delete(99L);

        mockMvc.perform(delete("/api/autores/99"))
                .andExpect(status().isNotFound());
    }
}
