package br.jus.tjrj.livros.service;

import br.jus.tjrj.livros.dto.AutorRequest;
import br.jus.tjrj.livros.dto.AutorResponse;
import br.jus.tjrj.livros.entity.AutorEntity;
import br.jus.tjrj.livros.exception.NotFoundException;
import br.jus.tjrj.livros.exception.ValidacaoException;
import br.jus.tjrj.livros.repository.AutorRepository;
import br.jus.tjrj.livros.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutorService {
    private final AutorRepository repository;
    private final LivroRepository livroRepository;

    public List<AutorResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public AutorResponse get(Long id) {
        return toResponse(findOrThrow(id));
    }

    public AutorResponse create(AutorRequest req) {
        validateAutorNotExists(req.nome());
        AutorEntity e = AutorEntity.builder().nome(req.nome()).build();
        return toResponse(repository.save(e));
    }

    public AutorResponse update(Long id, AutorRequest req) {
        AutorEntity e = findOrThrow(id);
        e.setNome(req.nome());
        return toResponse(repository.save(e));
    }

    public void delete(Long autorId) {
        List<String> livros = livroRepository.findTitulosByAutorId(autorId);

        if (!livros.isEmpty()) {
            throw new ValidacaoException(
                    "Autor não pode ser removido. Vinculado aos livros: "
                            + String.join(", ", livros)
            );
        }

        repository.delete(findOrThrow(autorId));
    }

    public AutorEntity findOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Autor não encontrado: " + id));
    }

    private AutorResponse toResponse(AutorEntity e) {
        return new AutorResponse(e.getId(), e.getNome());
    }


    public void validateAutorNotExists(String nome) {
        if (repository.existsByNomeIgnoreCase(nome.trim())) {
            throw new ValidacaoException("Autor já cadastrado.");
        }
    }
}
