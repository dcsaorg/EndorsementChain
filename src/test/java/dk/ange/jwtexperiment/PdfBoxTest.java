package dk.ange.jwtexperiment;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;

final class PdfBoxTest {
    @Test
    void pdfBoxTest() throws IOException, IllegalAccessException {
        BillOfLading bol = new BillOfLading(Files.readString(new ClassPathResource("bol-example.json").getFile().toPath()));
        PDDocument pdfDocument = bol.toPdf();
        PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
        Field[] fields = BillOfLading.class.getFields();
        System.out.println("Nb of fields:" + fields.length);
        for (Field field : fields) {
            String k = field.toString().split("\\.")[6];
            Assertions.assertNotNull(acroForm.getField(k));
            System.out.println(k);
            System.out.println(acroForm.getField(k).getValueAsString());
        }
        pdfDocument.save("billoflading.pdf");
        pdfDocument.close();
    }
}
