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

@WebMvcTest(JwkSetRestController.class)
@ContextConfiguration(classes = {JwkBeanFactory.class, SecurityConfig.class, JwkSetRestController.class})
public class JwkSetEndPointTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwkSetRestController jwkSetRestController;

    @Test
    public void getJwks() throws Exception {
        String uri = "/.well-known/jwks.json";
        MvcResult mvcResult =
        mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assertions.assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(content.length() > 0);
        System.out.println(content);
    }
}
