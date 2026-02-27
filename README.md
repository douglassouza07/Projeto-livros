# 📚 Sistema de Cadastro de Livros

## Projeto Técnico -- Tribunal de Justiça do Estado do Rio de Janeiro (TJRJ)

Angular 17 • Spring Boot 3 • Java 17 • JasperReports • TDD • Clean
Architecture

------------------------------------------------------------------------

# Visão Geral

Sistema Web desenvolvido para gerenciamento de:

-   Livros\
-   Autores\
-   Assuntos

Funcionalidades:

-   CRUD completo
-   Associação N:N
-   Relatório PDF agrupado por autor
-   Tratamento global de exceções
-   Testes automatizados (TDD)
-   Interface moderna

------------------------------------------------------------------------

# Arquitetura

Controller → Service → Repository → Database

------------------------------------------------------------------------

# Tecnologias Utilizadas

Backend: - Java 17 - Spring Boot 3 - Spring Data JPA - Hibernate -
JasperReports 7 - OpenPDF - JUnit 5 - Mockito - ArchUnit

Frontend: - Angular 17 - TypeScript - Bootstrap 5 - RxJS - HTTP
Interceptor - Loading Overlay

------------------------------------------------------------------------

# Modelo de Dados

Entidades: - livro - autor - assunto

Relacionamentos: - livro_autor - livro_assunto

View: vw_rel_livros_por_autor

------------------------------------------------------------------------

# Relatório PDF

Endpoint: GET /api/relatorios/livros-por-autor

------------------------------------------------------------------------

# Testes (TDD)

Executar: mvn test

------------------------------------------------------------------------

# Como Executar

Backend: cd backend mvn clean install mvn spring-boot:run

Frontend: cd frontend npm install ng serve

------------------------------------------------------------------------

# Autor

Douglas Souza Projeto técnico -- TJRJ
