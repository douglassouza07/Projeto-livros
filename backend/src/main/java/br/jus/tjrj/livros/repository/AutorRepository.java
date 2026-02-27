package br.jus.tjrj.livros.repository;

import br.jus.tjrj.livros.entity.AutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<AutorEntity, Long> {

    boolean existsByNomeIgnoreCase(String nome);
}
