package br.jus.tjrj.livros.service;

import br.jus.tjrj.livros.dto.AssuntoRequest;
import br.jus.tjrj.livros.dto.AssuntoResponse;
import br.jus.tjrj.livros.entity.AssuntoEntity;
import br.jus.tjrj.livros.exception.NotFoundException;
import br.jus.tjrj.livros.exception.ValidacaoException;
import br.jus.tjrj.livros.repository.AssuntoRepository;
import br.jus.tjrj.livros.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssuntoService {
    private final AssuntoRepository repository;
    private final LivroRepository livroRepository;

    public List<AssuntoResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public AssuntoResponse get(Long id) {
        return toResponse(findOrThrow(id));
    }

    public AssuntoResponse create(AssuntoRequest req) {
        validateAssuntoNotExists(req.descricao());
        AssuntoEntity e = AssuntoEntity.builder().descricao(req.descricao()).build();
        return toResponse(repository.save(e));
    }

    public AssuntoResponse update(Long id, AssuntoRequest req) {
        AssuntoEntity e = findOrThrow(id);
        e.setDescricao(req.descricao());
        return toResponse(repository.save(e));
    }

    public void delete(Long id) {

        List<String> livros = livroRepository.findTitulosByAssuntoId(id);

        if (!livros.isEmpty()) {
            String nomes = String.join(", ", livros);

            throw new ValidacaoException(
                    "Assunto não pode ser removido. Vinculado aos livros: " + nomes
            );
        }

        repository.deleteById(id);
    }

    public AssuntoEntity findOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Assunto não encontrado: " + id));
    }

    public void validateAssuntoNotExists(String descricao) {
        if (repository.existsByDescricaoIgnoreCase(descricao.trim())) {
            throw new ValidacaoException("Assunto já cadastrado.");
        }
    }

    private AssuntoResponse toResponse(AssuntoEntity e) {
        return new AssuntoResponse(e.getId(), e.getDescricao());
    }
}
