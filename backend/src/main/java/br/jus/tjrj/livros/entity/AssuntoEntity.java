package br.jus.tjrj.livros.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "assunto")
public class AssuntoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codas")
    private Integer id;

    @Column(name = "descricao", length = 20, nullable = false)
    private String descricao;

    @ManyToMany(mappedBy = "assuntos")
    @Builder.Default
    private Set<LivroEntity> livros = new HashSet<>();
}
