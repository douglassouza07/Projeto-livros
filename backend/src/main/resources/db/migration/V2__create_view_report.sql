create or replace view vw_rel_livros_por_autor as
select
    l.titulo,
    l.editora,
    l.edicao,
    l.anopublicacao,
    l.valor,
    coalesce(string_agg(distinct a.nome , ', ' order by a.nome ), '') as autores,
    coalesce(string_agg(distinct s.descricao, ', ' order by s.descricao), '') as assuntos
from livros.autor a
         join livros.livro_autor la on la.autor_codau = a.codau
         join livros.livro l on l.codl = la.livro_codl
         left join livros.livro_assunto las on las.livro_codl = l.codl
         left join livros.assunto s on s.codas = las.assunto_codas
group by l.codl, l.titulo, l.editora, l.edicao, l.anopublicacao, l.valor;