package br.jus.tjrj.livros.report;

import br.jus.tjrj.livros.exception.ValidacaoException;
import br.jus.tjrj.livros.repository.RelatorioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final RelatorioRepository repository;

    public byte[] gerarPdf() {
        try {
            List<Map<String, ?>> dados = repository.findRelatorio();

            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(dados);

            InputStream jasperStream =
                    Thread.currentThread()
                            .getContextClassLoader()
                            .getResourceAsStream("reports/rel_livros_por_autor.jasper");

            if (jasperStream == null) {
                throw new RuntimeException("Arquivo rel_livros_por_autor.jasper NÃO encontrado no classpath!");
            }

            JasperPrint print = JasperFillManager.fillReport(
                    jasperStream,
                    new HashMap<>(),
                    dataSource
            );

            return JasperExportManager.exportReportToPdf(print);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar relatório PDF", e);
        }
    }
}