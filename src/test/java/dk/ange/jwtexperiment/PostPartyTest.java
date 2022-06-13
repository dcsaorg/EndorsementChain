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

@WebMvcTest(PartyController.class)
@ContextConfiguration(classes = {SecurityConfig.class, PartyController.class, PartyRepository.class})
public class PostPartyTest {

    @MockBean
    private PartyRepository partyRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PartyController partyController;

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
