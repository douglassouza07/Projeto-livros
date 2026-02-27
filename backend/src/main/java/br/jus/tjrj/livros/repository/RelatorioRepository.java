package br.jus.tjrj.livros.repository;

import br.jus.tjrj.livros.entity.LivroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface RelatorioRepository extends JpaRepository<LivroEntity, Integer> {

    @Query(value = "select * from vw_rel_livros_por_autor", nativeQuery = true)
    List<Map<String, ?>> findRelatorio();
}