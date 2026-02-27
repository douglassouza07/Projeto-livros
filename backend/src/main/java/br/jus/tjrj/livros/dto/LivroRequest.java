package br.jus.tjrj.livros.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Set;

public record LivroRequest(
        @NotBlank @Size(max = 40) String titulo,
        @NotBlank @Size(max = 40) String editora,
        @NotNull @Min(1) Integer edicao,
        @NotBlank @Pattern(regexp = "\\d{4}") String anoPublicacao,
        @NotNull @DecimalMin(value = "0.00") BigDecimal valor,
        @NotEmpty Set<Long> autoresIds,
        @NotEmpty Set<Long> assuntosIds
) {}
