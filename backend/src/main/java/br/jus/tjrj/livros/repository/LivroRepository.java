package br.jus.tjrj.livros.repository;

import br.jus.tjrj.livros.entity.LivroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LivroRepository extends JpaRepository<LivroEntity, Long> {

    boolean existsByTituloIgnoreCase(String descricao);

    @Query("""
   select l.titulo
   from LivroEntity l
   join l.autores a
   where a.id = :autorId
""")
    List<String> findTitulosByAutorId(Long autorId);

    @Query("""
        select l.titulo
        from LivroEntity l
        join l.assuntos s
        where s.id = :assuntoId
    """)
    List<String> findTitulosByAssuntoId(Long assuntoId);

}
