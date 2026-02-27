package br.jus.tjrj.livros.controller;

import br.jus.tjrj.livros.dto.AutorRequest;
import br.jus.tjrj.livros.dto.AutorResponse;
import br.jus.tjrj.livros.service.AutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/autores")
@RequiredArgsConstructor
public class AutorController {

    private final AutorService service;

    @GetMapping
    public List<AutorResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public AutorResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AutorResponse create(@RequestBody @Valid AutorRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public AutorResponse update(@PathVariable Long id, @RequestBody @Valid AutorRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
