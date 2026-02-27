package br.jus.tjrj.livros.exception;

import br.jus.tjrj.livros.config.SecurityConfig;
import br.jus.tjrj.livros.controller.AutorController;
import br.jus.tjrj.livros.service.AutorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AutorController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AutorService autorService;

    // ─── NotFoundException → 404 ─────────────────────────────────────────────

    @Test
    @DisplayName("NotFoundException deve gerar resposta 404 com corpo correto")
    void notFound_deveRetornar404() throws Exception {
        when(autorService.get(99L))
                .thenThrow(new NotFoundException("Autor não encontrado: 99"));

        mockMvc.perform(get("/api/autores/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Autor não encontrado: 99"))
                .andExpect(jsonPath("$.path").value("/api/autores/99"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    // ─── ValidacaoException → 400 ────────────────────────────────────────────

    @Test
    @DisplayName("ValidacaoException deve gerar resposta 400 com corpo correto")
    void validacao_deveRetornar400() throws Exception {
        doThrow(new ValidacaoException("Autor não pode ser removido. Vinculado aos livros: Dom Casmurro"))
                .when(autorService).delete(1L);

        mockMvc.perform(delete("/api/autores/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Autor não pode ser removido. Vinculado aos livros: Dom Casmurro"));
    }

    // ─── DataIntegrityViolationException → 409 ───────────────────────────────

    @Test
    @DisplayName("DataIntegrityViolationException deve gerar resposta 409")
    void dataIntegrity_deveRetornar409() throws Exception {
        doThrow(new DataIntegrityViolationException("FK violation"))
                .when(autorService).delete(1L);

        mockMvc.perform(delete("/api/autores/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Database constraint violation"));
    }

    // ─── Exception genérica → 500 ────────────────────────────────────────────

    @Test
    @DisplayName("Exception genérica deve gerar resposta 500")
    void generic_deveRetornar500() throws Exception {
        when(autorService.list())
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/autores"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }
}
