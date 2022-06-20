package dk.ange.jwtexperiment;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class TransferBlockNotification {
	String transferBlockURL;
}
