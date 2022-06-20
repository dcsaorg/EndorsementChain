package dk.ange.jwtexperiment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for the transferBlock service")
class TransferBlockServiceTest {

	@Mock	private TransferBlockRepository transferBlockRepository;
	@Mock private PartyRepository partyRepository;

	@Mock private RestTemplate restTemplate;
	@Spy private ObjectMapper mapper = new ObjectMapper();

	@InjectMocks TransferBlockService transferBlockService;

  String transferBlockRequest =
      "{\"payload\":\"eyJ0cmFuc2ZlcmVlIjp7Imt0eSI6IlJTQSIsIm4iOiJpNzFPT0JzbkQ4a1FwakREM0pfVklCaHhkejUzXzR5M3k1NHJXTnNPQ0pSMmppLWdaMzg3Mkw1RDl5T0hxcDNCRmRoMGV4ZVh6Vl9zRlJMeTNpV1NiX0QtOWdXbkpvekdkSlNYd0c0bjZ5QVZremVIYm9vTUpfMXAzT1JvTTVSU2ZpRE5pR2gweGROTDBsXzUweUZDa09rc3ZJSUV1OC0tZWNYb2NxN3hSY0dJSFlhNnFRSlVFd0tBMmdpU2RHTVc3NjlmWVZITTA1ZnBpU0txcjR0eHVJWF9QNzRCR1JnMFNJZV9pMnRGUDBjTTBFeWFxQ3FnQlRHUGE2eDdncXM2bWlpR2tJRXhPNHZVYnRvZkJQcG0tdXN4WEdlb0QyTGwwRk9Ueld6MU1ZUXd3SXZOaFl1RmFMY3YtbW15Z25BSml1VURKdGR3eGV6NS0tRzFaU2ZDeVEiLCJlIjoiQVFBQiJ9LCJwcmV2aW91c0Jsb2NrSGFzaCI6bnVsbCwiYmxvY2tQYXlsb2FkIjp7ImRvY3VtZW50SGFzaCI6Ijc2YTdkMTRjODNkNzI2OGQ2NDNhZTczNDVjNDQ4ZGU2MDcwMWY5NTVkMjY0YTc0M2U2OTI4YTBiODI2OGIyNGYiLCJpc1RvT3JkZXIiOnRydWUsInRpdGxlSG9sZGVyUGxhdGZvcm0iOiJsb2NhbGhvc3Q6ODQ0MyJ9fQ\"," +
				"\"signatures\":[" +
				"{\"protected\":\"eyJhbGciOiJSUzI1NiJ9\"," +
				"\"signature\":\"BqpBzQTPBzVqqcFoZ-dT3ou6CPlH7OT6N_uMdBL-oe8av5vuus2INVjLS4r56uAcY5UJK46Wv9SZvesi72-xADyNX6kYdKPJv2y5WyujehXsMlbGM-xBh-DEmOsVdXlrSNoUfzOdz4uEKt0jGllZZeNEHccLXe__b7Ru4mqDJIQs6Xale_ylkGImbjWDBhPK2-OiBO08OmJmz7Qvdaw6T6VrCw_3wLS15Gj3Vagenae-Ufi6wpUrssO-pdCy0P65BiQBw_x_mbVpUR7xrU1huFsZ229oOKUWmhkhJmm_SomtCGYUEDtca3fsKfaopf0onCBk7y314gjZk4byvp4HPw\"}]}";

	@Test
	void testValidTransferBlockNotification() throws Exception {
		when(partyRepository.findByEblPlatformContains(any())).thenReturn(Optional.of(new Party()));
		TransferBlock transferBlock = TransferBlock.of(transferBlockRequest);
		when(restTemplate.getForEntity(any(), eq(TransferBlock.class))).thenReturn(ResponseEntity.ok(transferBlock));
		when(transferBlockRepository.save(any())).thenReturn(transferBlock);

		TransferBlockNotification notificationRequest = TransferBlockNotification.builder().transferBlockURL("https://localhost:8443/12345").build();

		Optional<String> transferBlockHashOptional = transferBlockService.fetchTransferBlockByNotification(notificationRequest);

		assertTrue(transferBlockHashOptional.isPresent());
	}

	@Test
	void testUnknownUrlInTransferBlockNotification() throws Exception {
		when(partyRepository.findByEblPlatformContains(any())).thenReturn(Optional.empty());
		TransferBlockNotification notificationRequest = TransferBlockNotification.builder().transferBlockURL("https://localhost:8443/12345").build();

		Optional<String> transferBlockHashOptional = transferBlockService.fetchTransferBlockByNotification(notificationRequest);

		assertTrue(transferBlockHashOptional.isEmpty());
	}

	@Test
	void testTransferBlockNotFound() throws Exception {
		when(partyRepository.findByEblPlatformContains(any())).thenReturn(Optional.of(new Party()));

    when(restTemplate.getForEntity(any(), eq(TransferBlock.class)))
        .thenReturn(ResponseEntity.notFound().build());

		TransferBlockNotification notificationRequest = TransferBlockNotification.builder().transferBlockURL("https://localhost:8443/12345").build();
		Optional<String> transferBlockHashOptional = transferBlockService.fetchTransferBlockByNotification(notificationRequest);

		assertTrue(transferBlockHashOptional.isEmpty());
	}

}
