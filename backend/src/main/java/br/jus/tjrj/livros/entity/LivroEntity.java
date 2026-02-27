package br.jus.tjrj.livros.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "livro")
public class LivroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codl")
    private Integer id;

    @Column(name = "titulo", length = 40, nullable = false)
    private String titulo;

    @Column(name = "editora", length = 40, nullable = false)
    private String editora;

    @Column(name = "edicao", nullable = false)
    private Integer edicao;

    @Column(name = "anopublicacao", length = 4, nullable = false)
    private String anoPublicacao;

    @Column(name = "valor", precision = 10, scale = 2, nullable = false)
    private BigDecimal valor;

    @ManyToMany
    @JoinTable(
            name = "livro_autor",
            joinColumns = @JoinColumn(name = "livro_codl"),
            inverseJoinColumns = @JoinColumn(name = "autor_codau")
    )
    @Builder.Default
    private Set<AutorEntity> autores = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "livro_assunto",
            joinColumns = @JoinColumn(name = "livro_codl"),
            inverseJoinColumns = @JoinColumn(name = "assunto_codas")
    )
    @Builder.Default
    private Set<AssuntoEntity> assuntos = new HashSet<>();
}
