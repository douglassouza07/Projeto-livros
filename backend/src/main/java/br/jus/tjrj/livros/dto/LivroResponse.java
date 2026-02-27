package br.jus.tjrj.livros.dto;

import java.math.BigDecimal;
import java.util.Set;

public record LivroResponse(
        Integer id,
        String titulo,
        String editora,
        Integer edicao,
        String anoPublicacao,
        BigDecimal valor,
        Set<AutorResponse> autores,
        Set<AssuntoResponse> assuntos
) {}
