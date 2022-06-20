package dk.ange.jwtexperiment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TransferBlockController.class})
class TransferBlockControllerTest {

  @Autowired MockMvc mockMvc;

  @MockBean TransferBlockService transferBlockService;
  @MockBean RestTemplate restTemplate;

  @Test
  void testReceiveTransferBlockNotification() throws Exception {
    when(transferBlockService.fetchTransferBlockByNotification(any()))
        .thenReturn(Optional.of("12345abcd"));
    mockMvc
        .perform(
            post("/api/v1/transferblocks/notifications")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                    "{\"transferBlockURL\": \"https://example.io/api/v1/transferblocks/7e74df6a7e1c86e1aea44483d9840fe542df24fa693fc3832a33e9d14557979f\"}"))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(
            result ->
                result
                    .getResponse()
                    .getHeader("Location")
                    .equals("http://localhost/api/v1/transferblocks/12345abcd"))
        .andExpect(
            jsonPath("$.transferBlockURL")
                .value("http://localhost/api/v1/transferblocks/12345abcd"));
  }
}
