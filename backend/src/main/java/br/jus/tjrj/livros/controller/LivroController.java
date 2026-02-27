package br.jus.tjrj.livros.controller;

import br.jus.tjrj.livros.dto.LivroRequest;
import br.jus.tjrj.livros.dto.LivroResponse;
import br.jus.tjrj.livros.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService service;

    @GetMapping
    public List<LivroResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public LivroResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LivroResponse create(@RequestBody @Valid LivroRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public LivroResponse update(@PathVariable Long id, @RequestBody @Valid LivroRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
