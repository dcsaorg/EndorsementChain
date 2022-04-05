package dk.ange.jwtexperiment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(AddressBookEntryController.class)
@ContextConfiguration(classes = {SecurityConfig.class, AddressBookEntryController.class, AddressBookEntryRepository.class})
public class PostAddressBookEntryTest {

    @MockBean
    private AddressBookEntryRepository addressBookEntryRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AddressBookEntryController addressBookEntryController;

    @Test
    public void postAddressBookEntry() throws Exception {
        String uri = "/api/v1/address-book-entries";
        String json = "{\"thumbprint\":\"\", \"name\":\"\", \"publicKey\":\"\", \"eblPlatform\":\"\"}";
        MvcResult mvcResult =
        mvc.perform(MockMvcRequestBuilders.post(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .accept(MediaType.APPLICATION_JSON))
            .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(201, status);
    }
}
