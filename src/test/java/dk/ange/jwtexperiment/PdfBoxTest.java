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


public final class PdfBoxTest {
    @Test
    void pdfBoxTest() throws IOException, FileNotFoundException, IllegalAccessException {
        PDDocument pdfDocument = PDDocument.load(new File(Thread.currentThread().getContextClassLoader().getResource("BillOfLadingWithForms.pdf").getPath()));
        PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
        Assertions.assertNotNull(acroForm);
        ObjectMapper mapper = new ObjectMapper();
        BillOfLading bol = mapper.readValue(new File(Thread.currentThread().getContextClassLoader().getResource("bol-example.json").getPath()), BillOfLading.class);
        Field[] fields = BillOfLading.class.getFields();
        System.out.println("Nb of fields:" + fields.length);
        for (int i = 0; i < fields.length; ++i) {
            String k = fields[i].toString().split("\\.")[6];
            Assertions.assertNotNull(acroForm.getField(k));
            acroForm.getField(k).setValue((String)fields[i].get(bol));

            System.out.println(k);
        }
        pdfDocument.save("billoflading.pdf");
        pdfDocument.close();
    }
}
