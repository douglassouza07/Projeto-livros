package br.jus.tjrj.livros.controller;

import br.jus.tjrj.livros.report.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService service;

    @GetMapping("/livros-por-autor")
    public ResponseEntity<byte[]> gerar() throws Exception {

        byte[] pdf = service.gerarPdf();

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=relatorio.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
