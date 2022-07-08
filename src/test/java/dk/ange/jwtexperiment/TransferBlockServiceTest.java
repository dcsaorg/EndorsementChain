package dk.ange.jwtexperiment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.security.KeyPair;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for the transferBlock service")
class TransferBlockServiceTest {

  @Mock private TransferBlockRepository transferBlockRepository;
  @Mock private TransportDocumentRepository transportDocumentRepository;
  @Mock private PartyRepository partyRepository;
  @Mock private RestTemplate restTemplate;
  @Spy private ObjectMapper mapper = new ObjectMapper();

  @InjectMocks TransferBlockService transferBlockService;

  @BeforeEach
  public void init() {
		ClassPathResource ksFile = new ClassPathResource("certificates/dcsa-jwk.jks");
		KeyStoreKeyFactory ksFactory = new KeyStoreKeyFactory(ksFile, "dcsa-pass".toCharArray());
		KeyPair keyPair =  ksFactory.getKeyPair("dcsa-kid");
		ReflectionTestUtils.setField(transferBlockService, "platformKeyPair", keyPair);
	}

  String exportTransferblock =
      "{\n"
          + "    \"payload\": \"eyJ0cmFuc2ZlcmVlIjp7Imt0eSI6IlJTQSIsIm4iOiJ4U0NpRzFpVlZOSWV5Wk1vSEVYbnhqVUlpVmtrQlFRUTdaMDhUX0gxbThjRWMwUHFiVDJhVnVfaEd0Q2hvR0k5VkJuX0JWVGlNM0hWclJhSVVyaEE5blhtdlZrck5peElrSGl1Y2xBQ1kxZnZxT2NhTzJlcmNsSG9YLVlxYUNVVDZzSUNDUGVoemFYY2tpaGRxZVFuUGluTWdfeXRoZmZFQ1pHcGNCZ2NROUtxQnp1RUZWVzJKXy1hbkk0NkdZLURhY2cyeG5uOXczVno1WUNZTmZmSjRVbWlqQnpMOGZDaUNTZi16ZVp6WE85Wi0xbHMzVF8zTHV6YXFGNDNLbm5QM0lmNWxjZ3ItMHRmbEkzVmRzS0l3bktOT0hJTkphZHBTQ25jYnBQVGEzSVRaOHFEVzZhWlNBS0hkVElUaVctdjRZMF9xWDNOZ3ZFQ01MbXJhejdISHciLCJlIjoiQVFBQiJ9LCJ0aW1lc3RhbXAiOjE2NTc2MTQ0NTU3MDYsInByZXZpb3VzQmxvY2tIYXNoIjoiNzAwZDQzYTdhMTI4OGIwNzU5MGRiZTMyYjA5ZDZmZDhlMjMxZDhmNDkzMjAzNzg1YTExNmM4ZWEwOTYzMDIwMyIsImJsb2NrUGF5bG9hZCI6eyJ0aXRsZVRyYW5zZmVyQmxvY2tIYXNoIjoiOGY0Y2M2NzUwZjdlY2Q0ZjZhNGFmM2FjOTNkMmY2OTYxMjJjZGQ4N2U0Y2UxZDI3NmExNTgxMTUxZDQyMGU0MyIsIm5leHRSZWdpc3RyeUpXSyI6eyJrZXlzIjpbeyJrdHkiOiJSU0EiLCJlIjoiQVFBQiIsInVzZSI6InNpZyIsImtpZCI6ImRjc2Eta2lkIiwiYWxnIjoiUlMyNTYiLCJuIjoiekJzbXJ5NlQ2Ry1nN3F5cUJvQUI2MkVYRkw3SUxDNGlsaUE0aFc5U2JnWTlnNVEtNTVMM0FuSzdIdkdwVUF4akNzcTRqYWNKZk9YOUFVa2dTMTRidkFtSld1OU1TWDNMeV96c2hqY1M3cG5PRElqbi1DLW5Xa0xIMnlZTFVwWW9BazNSblRhLUh5UWljV2lOYlNiUDVDT1ZSWEFMdWVGOXJFX1FYM3QtNUtub3lXU1o1XzcyRG9TMDhxSTEzR29DZGZhNjgxUE5NcXZSN3gtTjVlMVYxclBxQlVQQXJ4bFRzaU1DeHNEQmNiZERUWDZqTWV3UWk5a1ZoZm4xdmp3cHJLaTZWZ01wTkZ6cGgwNzlXVjZIMjF2WWZlODRNMFBBX3JHTUpQazRNVFRwMzNabk14MHFSaWI3YllWYURyWlhMeHg5eVdRbHNEUDJCbmxZTXlhRU5RIn1dfSwibmV4dFJlZ2lzdHJ5SG9zdCI6ImxvY2FsaG9zdDo4NDQ0In19\",\n"
          + "    \"signatures\": [\n"
          + "        {\n"
          + "            \"protected\": \"eyJhbGciOiJSUzI1NiJ9\",\n"
          + "            \"signature\": \"auW4fD5cSbDMz26mIPYm2MgkPTpMuJSf7XKjaLGxNsI8N-Bg9a118nWdQphcQHgpFyUTt-z9eBiMK6tHifEDZHoA200GAPXZM-5_Zkx1R15tBaOjqJmm7ABUV8C9eq73X-Q4Fds-sGEMiWAjrvreb36xe7toBNDJXyJVC_haTLUEYvfEBqsWRy5CqGyftyS-y3rQFcznyGEP8fd73ZADbCzS5N829ERnoeDIBI3fQ4yimbbBZPz5DKntOKsiI3RDkzrCVagktWZIjqve5nd8dCDSLhabB5xLvov1rqkPjSrz7e4eWPDRk6R6OuIoGkofiWIwjK2ysRE7hOLcXkppRg\"\n"
          + "        },\n"
          + "        {\n"
          + "            \"protected\": \"eyJhbGciOiJSUzI1NiJ9\",\n"
          + "            \"signature\": \"iElGaoJg0HCpz_9HAQJV9SPSMsrzM258SGFbUmSAXG8AwXCAC5Ibp8e8fP9XOq9d6E-YHKt9gnpZ6Cf08pjq3rQibNFe9h5QKz8ixny42m1Lf2J2ueRPx-Gf5bNaOMGGilBeKcJN8bgvqUH-aHHlGlmIaaTtgT8KLmsCSZPhnyUxkk_Lyr0Lq86cTbAov_FK_t_nNSt-ETUfTMOAmXLTIwtBTn8s1cTdoNuRu3T4B6v5-x7hHPo1-1OZlJkm_4bzrNY3XHrms8XRMGDNxe6COzt5DExHfWacvSzwDl6nUXxMm-3N-Q3wnXsULIT4-WwqZ7bRUtLL7dpybD83P3LjhQ\"\n"
          + "        }\n"
          + "    ]\n"
          + "}";

  String titleTransferBlock =
      "{\n"
          + "    \"payload\": \"eyJ0cmFuc2ZlcmVlIjp7Imt0eSI6IlJTQSIsIm4iOiJpNzFPT0JzbkQ4a1FwakREM0pfVklCaHhkejUzXzR5M3k1NHJXTnNPQ0pSMmppLWdaMzg3Mkw1RDl5T0hxcDNCRmRoMGV4ZVh6Vl9zRlJMeTNpV1NiX0QtOWdXbkpvekdkSlNYd0c0bjZ5QVZremVIYm9vTUpfMXAzT1JvTTVSU2ZpRE5pR2gweGROTDBsXzUweUZDa09rc3ZJSUV1OC0tZWNYb2NxN3hSY0dJSFlhNnFRSlVFd0tBMmdpU2RHTVc3NjlmWVZITTA1ZnBpU0txcjR0eHVJWF9QNzRCR1JnMFNJZV9pMnRGUDBjTTBFeWFxQ3FnQlRHUGE2eDdncXM2bWlpR2tJRXhPNHZVYnRvZkJQcG0tdXN4WEdlb0QyTGwwRk9Ueld6MU1ZUXd3SXZOaFl1RmFMY3YtbW15Z25BSml1VURKdGR3eGV6NS0tRzFaU2ZDeVEiLCJlIjoiQVFBQiJ9LCJ0aW1lc3RhbXAiOjE2NTc2MTQ0NDY4MTYsInByZXZpb3VzQmxvY2tIYXNoIjpudWxsLCJibG9ja1BheWxvYWQiOnsiZG9jdW1lbnRIYXNoIjoiNzZhN2QxNGM4M2Q3MjY4ZDY0M2FlNzM0NWM0NDhkZTYwNzAxZjk1NWQyNjRhNzQzZTY5MjhhMGI4MjY4YjI0ZiIsImlzVG9PcmRlciI6dHJ1ZSwidGl0bGVIb2xkZXJQbGF0Zm9ybSI6ImxvY2FsaG9zdDo4NDQzIn19\",\n"
          + "    \"signatures\": [\n"
          + "        {\n"
          + "            \"protected\": \"eyJhbGciOiJSUzI1NiJ9\",\n"
          + "            \"signature\": \"OoC3Qs7wxaPNjf5ePnoGbcoYVd7CrOD4PiLp8wVT-6Jj2RU-m2BmfkTb33qBeXAVKW9XmB0HO6ZM8ea5Yu9Y1_4SV4I0wInIe1kfOnPeS4_tcynbgHVptEyn3tUuKcCMF110v07ylsIUQirpgfc0ZsIeyGhpy07KK1cg2uB9hqqUNJLYhMPb4qtEL1Vwhdj5-POi8Mw3YSQmsSOqkN5eiV9PHPHf8YQLvBa5ud2c18fri1Hpa8bPo-rMS2c7mG4hy61-GdNHjFIQ6ngviebjX-xkFkkkQ6GEjgHd3Wsks70jmwaXlXgYoUbM4oVOQitDJvFxjRHPKgQzylDTWjtMrQ\"\n"
          + "        }\n"
          + "    ]\n"
          + "}";

  @Test
  void testValidTransferBlockNotification() throws Exception {
    when(partyRepository.findByEblPlatformContains(any())).thenReturn(Optional.of(List.of(new Party())));
    TransferBlock exportTransferBlock = TransferBlock.of(exportTransferblock);
    TransferBlock importedTitleTransferBlock = TransferBlock.of(titleTransferBlock);
    TransportDocument transportDocument = new TransportDocument();
    transportDocument.setDocumentHash("dummyHash");
    transportDocument.setTransportDocumentJson("dummy");
    when(restTemplate.getForEntity(URI.create("https://localhost:8443/12345"), TransferBlock.class))
        .thenReturn(ResponseEntity.ok(exportTransferBlock));
    when(restTemplate.getForEntity(
            "https://localhost:8443/api/v1/transferblocks/8f4cc6750f7ecd4f6a4af3ac93d2f696122cdd87e4ce1d276a1581151d420e43",
            TransferBlock.class))
        .thenReturn(ResponseEntity.ok(importedTitleTransferBlock));
    when(transferBlockRepository.save(any())).thenReturn(importedTitleTransferBlock);
    when(restTemplate.getForEntity("https://localhost:8443/api/v1/transport-documents/76a7d14c83d7268d643ae7345c448de60701f955d264a743e6928a0b8268b24f", TransportDocument.class))
      .thenReturn(ResponseEntity.ok(transportDocument));
    when(transportDocumentRepository.save(any())).thenReturn(transportDocument);

    TransferBlockNotification notificationRequest =
        TransferBlockNotification.builder()
            .transferBlockURL("https://localhost:8443/12345")
            .build();

    Optional<String> transferBlockHashOptional =
        transferBlockService.fetchTransferBlockByNotification(notificationRequest);

    assertTrue(transferBlockHashOptional.isPresent());
  }

  @Test
  void testUnknownUrlInTransferBlockNotification() throws Exception {
    when(partyRepository.findByEblPlatformContains(any())).thenReturn(Optional.empty());
    TransferBlockNotification notificationRequest =
        TransferBlockNotification.builder()
            .transferBlockURL("https://localhost:8443/12345")
            .build();

    Optional<String> transferBlockHashOptional =
        transferBlockService.fetchTransferBlockByNotification(notificationRequest);

    assertTrue(transferBlockHashOptional.isEmpty());
  }

  @Test
  void testTransferBlockNotFound() throws Exception {
    when(partyRepository.findByEblPlatformContains(any())).thenReturn(Optional.of(List.of(new Party())));

    when(restTemplate.getForEntity(any(), eq(TransferBlock.class)))
        .thenReturn(ResponseEntity.notFound().build());

    TransferBlockNotification notificationRequest =
        TransferBlockNotification.builder()
            .transferBlockURL("https://localhost:8443/12345")
            .build();
    Optional<String> transferBlockHashOptional =
        transferBlockService.fetchTransferBlockByNotification(notificationRequest);

    assertTrue(transferBlockHashOptional.isEmpty());
  }
}
