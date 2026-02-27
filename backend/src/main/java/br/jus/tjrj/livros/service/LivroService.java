package br.jus.tjrj.livros.service;

import br.jus.tjrj.livros.dto.AssuntoResponse;
import br.jus.tjrj.livros.dto.AutorResponse;
import br.jus.tjrj.livros.dto.LivroRequest;
import br.jus.tjrj.livros.dto.LivroResponse;
import br.jus.tjrj.livros.entity.AssuntoEntity;
import br.jus.tjrj.livros.entity.AutorEntity;
import br.jus.tjrj.livros.entity.LivroEntity;
import br.jus.tjrj.livros.exception.NotFoundException;
import br.jus.tjrj.livros.exception.ValidacaoException;
import br.jus.tjrj.livros.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository repository;
    private final AutorService autorService;
    private final AssuntoService assuntoService;

    public List<LivroResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public LivroResponse get(Long id) {
        return toResponse(findOrThrow(id));
    }

    public LivroResponse create(LivroRequest req) {
        validateTituloNotExists(req.titulo());
        LivroEntity e = new LivroEntity();
        apply(e, req);
        return toResponse(repository.save(e));
    }

    public LivroResponse update(Long id, LivroRequest req) {
        LivroEntity e = findOrThrow(id);
        apply(e, req);
        return toResponse(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    private void apply(LivroEntity e, LivroRequest req) {
        e.setTitulo(req.titulo());
        e.setEditora(req.editora());
        e.setEdicao(req.edicao());
        e.setAnoPublicacao(req.anoPublicacao());
        e.setValor(req.valor());

        Set<AutorEntity> autores = new LinkedHashSet<>();
        for (Long aid : req.autoresIds()) {
            autores.add(autorService.findOrThrow(aid));
        }

        Set<AssuntoEntity> assuntos = new LinkedHashSet<>();
        for (Long sid : req.assuntosIds()) {
            assuntos.add(assuntoService.findOrThrow(sid));
        }

        e.getAutores().clear();
        e.getAutores().addAll(autores);

        e.getAssuntos().clear();
        e.getAssuntos().addAll(assuntos);
    }

    private LivroEntity findOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Livro não encontrado: " + id));
    }

    public void validateTituloNotExists(String titulo) {
        if (repository.existsByTituloIgnoreCase(titulo.trim())) {
            throw new ValidacaoException("Titulo já cadastrado.");
        }
    }

    private LivroResponse toResponse(LivroEntity e) {
        Set<AutorResponse> autores = e.getAutores().stream()
                .map(a -> new AutorResponse(a.getId(), a.getNome()))
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));

        Set<AssuntoResponse> assuntos = e.getAssuntos().stream()
                .map(s -> new AssuntoResponse(s.getId(), s.getDescricao()))
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));

        return new LivroResponse(
                e.getId(),
                e.getTitulo(),
                e.getEditora(),
                e.getEdicao(),
                e.getAnoPublicacao(),
                e.getValor(),
                autores,
                assuntos
        );
    }
}
