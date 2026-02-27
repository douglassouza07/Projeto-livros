package br.jus.tjrj.livros.repository;

import br.jus.tjrj.livros.entity.AssuntoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssuntoRepository extends JpaRepository<AssuntoEntity, Long> {

    boolean existsByDescricaoIgnoreCase(String descricao);
}
