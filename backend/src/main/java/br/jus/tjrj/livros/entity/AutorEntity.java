package br.jus.tjrj.livros.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "autor")
public class AutorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codau")
    private Integer id;

    @Column(name = "nome", length = 40, nullable = false)
    private String nome;

    @ManyToMany(mappedBy = "autores")
    @Builder.Default
    private Set<LivroEntity> livros = new HashSet<>();
}
