package dk.ange.jwtexperiment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Jacksonized
@Builder
public class TransferBlockRequest {
	String payload;
	List<Signatures> signatures;

	@Builder
	@Jacksonized
	static class Signatures {
		@JsonProperty("protected")
		String protectedSignature;
		@JsonProperty("signature")
		String signature;
	}

}
