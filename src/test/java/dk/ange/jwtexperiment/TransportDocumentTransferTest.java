package dk.ange.jwtexperiment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransportDocumentTransferTest {

	@Test
	void testTransportDocumentCreation() throws Exception{
		String transferBlockRequest = "{\n" +
			"    \"payload\": \"eyJ0cmFuc2ZlcmVlIjp7Imt0eSI6IlJTQSIsIm4iOiJpNzFPT0JzbkQ4a1FwakREM0pfVklCaHhkejUzXzR5M3k1NHJXTnNPQ0pSMmppLWdaMzg3Mkw1RDl5T0hxcDNCRmRoMGV4ZVh6Vl9zRlJMeTNpV1NiX0QtOWdXbkpvekdkSlNYd0c0bjZ5QVZremVIYm9vTUpfMXAzT1JvTTVSU2ZpRE5pR2gweGROTDBsXzUweUZDa09rc3ZJSUV1OC0tZWNYb2NxN3hSY0dJSFlhNnFRSlVFd0tBMmdpU2RHTVc3NjlmWVZITTA1ZnBpU0txcjR0eHVJWF9QNzRCR1JnMFNJZV9pMnRGUDBjTTBFeWFxQ3FnQlRHUGE2eDdncXM2bWlpR2tJRXhPNHZVYnRvZkJQcG0tdXN4WEdlb0QyTGwwRk9Ueld6MU1ZUXd3SXZOaFl1RmFMY3YtbW15Z25BSml1VURKdGR3eGV6NS0tRzFaU2ZDeVEiLCJlIjoiQVFBQiJ9LCJibG9ja1BheWxvYWQiOnsidGl0bGVUcmFuc2ZlckJsb2NrSGFzaCI6ImNhZGU5MzAwMjQ1MzU1ZjRhMGU4YmFmYzZlYjVkNWYyODg5YTY0MmY5YzA1OGIyY2QzYmY5MmZmZjdkNWM0N2EifX0\",\n" +
			"    \"signatures\": [\n" +
			"      {\n" +
			"        \"protected\": \"eyJhbGciOiJSUzI1NiJ9\",\n" +
			"        \"signature\": \"uYK8txY6QR6yjwhOHbj_L1S9akr2rLMJDQ2_1nOl3HbOCxHGANwcgwSZgxtAdyCcCi1gYBMu_PcglCuaJ_dfelcfQnGtuJd3MODJaO63rqIZasMAglhnpzVQZFDFM-H9Y_Ijd8D6FULlqHJ7TaSPGj2KGhSpxgSS5zdCL7YVoVWYRUowvAXYim0ST3-j7RHIkPp7XsyoCCI49FH2u8yAIn77MrSvtq2-ovUC5RHEtAUAGHSu3aLuhX7CFwliS8e__cVMvI5sBGpLDXPabE1ntnmkDNKyvS9Cls-xgHW1kmGZTe0Ng8WpmDssitUH3-xyMJH2p2UUsHisybCgBwkksA\"\n" +
			"      }\n" +
			"    ]\n" +
			"  }";

		TransportDocumentTransfer transportDocumentTransfer = TransportDocumentTransfer.of("previousBlockHash", transferBlockRequest);
		assertEquals("0efb9cc5ce2e1681d86e90625f35122711767fffc856eb1a95516b4f37c0aee0", transportDocumentTransfer.getTransferBlockHash());
		assertEquals(transferBlockRequest, transportDocumentTransfer.getTransferBlock());
	}
}
