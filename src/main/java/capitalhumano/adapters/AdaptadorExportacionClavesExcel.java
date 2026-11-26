package capitalhumano.adapters;

import capitalhumano.dtos.ClaveCandidatoDTO;
import capitalhumano.servicios.ServicioExportacionClaves;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class AdaptadorExportacionClavesExcel implements ServicioExportacionClaves {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
    private static final String[] ENCABEZADOS = {
            "Apellido", "Nombre", "Tipo Documento", "Nro. Documento", "Clave"
    };

    @Override
    public void exportarClavesCandidatos(List<ClaveCandidatoDTO> claves, String nombrePuesto, String carpeta) {
        if (claves == null || claves.isEmpty()) {
            throw new IllegalArgumentException("La lista de claves no puede ser nula ni vacía.");
        }
        if (nombrePuesto == null || nombrePuesto.isBlank()) {
            throw new IllegalArgumentException("El nombre del puesto no puede estar vacío.");
        }
        if (carpeta == null || carpeta.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar una carpeta");
        }

        Path directorio = Path.of(carpeta.trim());
        if (Files.exists(directorio) && !Files.isDirectory(directorio)) {
            throw new IllegalArgumentException("La ruta indicada no es una carpeta: " + carpeta);
        }

        String nombrePuestoSanitizado = sanitizarNombreArchivo(nombrePuesto.trim());
        String nombreArchivo = FORMATO_FECHA.format(LocalDateTime.now()) + "-" + nombrePuestoSanitizado + ".xls";
        Path archivoDestino = directorio.resolve(nombreArchivo);

        try {
            Files.createDirectories(directorio);
            try (Workbook workbook = new HSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Claves");

                CellStyle estiloEncabezado = workbook.createCellStyle();
                Font fuenteEncabezado = workbook.createFont();
                fuenteEncabezado.setBold(true);
                estiloEncabezado.setFont(fuenteEncabezado);

                Row filaEncabezado = sheet.createRow(0);
                for (int i = 0; i < ENCABEZADOS.length; i++) {
                    Cell cell = filaEncabezado.createCell(i);
                    cell.setCellValue(ENCABEZADOS[i]);
                    cell.setCellStyle(estiloEncabezado);
                }

                int rowNum = 1;
                for (ClaveCandidatoDTO dto : claves) {
                    if (dto == null) {
                        continue;
                    }
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(valorSeguro(dto.getApellido()));
                    row.createCell(1).setCellValue(valorSeguro(dto.getNombre()));
                    row.createCell(2).setCellValue(dto.getTipoDocumento() != null ? dto.getTipoDocumento().name() : "");
                    row.createCell(3).setCellValue(valorSeguro(dto.getNroDocumento()));
                    row.createCell(4).setCellValue(valorSeguro(dto.getClave()));
                }

                for (int i = 0; i < ENCABEZADOS.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                try (OutputStream out = Files.newOutputStream(archivoDestino)) {
                    workbook.write(out);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error al exportar las claves a Excel en " + archivoDestino, e);
        }
    }

    private static String sanitizarNombreArchivo(String nombre) {
        return nombre.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private static String valorSeguro(String valor) {
        return valor != null ? valor : "";
    }
}
