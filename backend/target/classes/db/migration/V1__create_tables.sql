CREATE SCHEMA IF NOT EXISTS livros;

create table if not exists autor (
  codau integer generated always as identity primary key,
  nome  varchar(40) not null
);

create table if not exists assunto (
  codas integer generated always as identity primary key,
  descricao varchar(20) not null
);

create table if not exists livro (
  codl integer generated always as identity primary key,
  titulo varchar(40) not null,
  editora varchar(40) not null,
  edicao integer not null,
  anopublicacao varchar(4) not null,
  valor numeric(10,2) not null default 0
);

create table if not exists livro_autor (
  livro_codl integer not null references livro(codl) on delete cascade,
  autor_codau integer not null references autor(codau) on delete restrict,
  primary key (livro_codl, autor_codau)
);

create table if not exists livro_assunto (
  livro_codl integer not null references livro(codl) on delete cascade,
  assunto_codas integer not null references assunto(codas) on delete restrict,
  primary key (livro_codl, assunto_codas)
);

create index if not exists ix_livro_titulo on livro (titulo);
create index if not exists ix_autor_nome on autor (nome);
create index if not exists ix_assunto_desc on assunto (descricao);
