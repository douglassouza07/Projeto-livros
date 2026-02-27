package br.jus.tjrj.livros.controller;

import br.jus.tjrj.livros.dto.AssuntoRequest;
import br.jus.tjrj.livros.dto.AssuntoResponse;
import br.jus.tjrj.livros.service.AssuntoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assuntos")
@RequiredArgsConstructor
public class AssuntoController {

    private final AssuntoService service;

    @GetMapping
    public List<AssuntoResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public AssuntoResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssuntoResponse create(@RequestBody @Valid AssuntoRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public AssuntoResponse update(@PathVariable Long id, @RequestBody @Valid AssuntoRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
