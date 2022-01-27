import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import java.io.FileReader;
import java.util.*;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Test;
import dk.ange.jwtexperiment.BillOfLading;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Assertions;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class PdfBoxTest {
    @Test
    void pdfBoxTest() throws IOException, FileNotFoundException, IllegalAccessException {
        BillOfLading bol = new BillOfLading(Files.readString(Paths.get(Thread.currentThread().getContextClassLoader().getResource("bol-example.json").getPath())));
        PDDocument pdfDocument = bol.toPdf();
        PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
        Field[] fields = BillOfLading.class.getFields();
        System.out.println("Nb of fields:" + fields.length);
        for (int i = 0; i < fields.length; ++i) {
            String k = fields[i].toString().split("\\.")[6];
            Assertions.assertNotNull(acroForm.getField(k));
            System.out.println(k);
            System.out.println(((PDTextField)acroForm.getField(k)).getValueAsString());
        }
        pdfDocument.save("billoflading.pdf");
        pdfDocument.close();
    }
}
