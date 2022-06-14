package dk.ange.jwtexperiment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

@DataJpaTest
public class TransferBlockRepositoryTest {
    private final String blockHash = "d17d8fbbd0caf68f010fc80f36d0f8917e4a38c75fc1ebe80607554b8ec0bce6";
    private final String transferBlock = "{\"payload\":\"eyJob2xkZXIiOnsia3R5IjoiUlNBIiwibiI6InAyTElWUHV3MV92VWdoSW9PRFpHQ01lOFVZcHRBQVhxN2JwTzVsREtNSVNSX1RnTVpoLUg2NU5BT25NQmU4RVluV3BCLXJhdElMdThYZU5FVHhnUlJSMUJSVWZYTmlNcUMxeG1BeDJwR2xvSklvbkNSYkNfUmdzdXlUaDlwdExlR09tUVAxaHQtNy1mNHNaT3dMTlhHZTVTTFZ2MEVjVDNYQkNuSE1YQnFNVkRmakQ4MDU2ZDRheUc4T1d5RDAxRExRU0VQSFh5a3gtdDVaTWs3WVJ0RXc0T1RjMWtUVFpJOGU4Zm1IRkplaWlWYmxHVzFaVkptRTJNS3FNeTRVWkgyRUE3eUdFQk9hSlVkQzA2OVN1Nm5vRjJXczFoMG1ULTM0dmE2aFFTUk95enYxbGdkYXkwLXd2ci03TXEtUnhsTDNFcTYzVGk0Qy1USjFGTW9SdDlyUSIsImUiOiJBUUFCIn0sInBvc3Nlc3NvciI6eyJrdHkiOiJSU0EiLCJuIjoieHBGc2l4aWR4MTJ1M3NCNlpwUVluOEljdUlZakJmR00wUmx1M1Rvb1pIT2tBNUhicHZSdnNhal9IVFZLQ0NRZHcxUHFKMFMtY2FaRERqQ1d5R09YaERmeFAwSjA0QzNVMjFrMkt3eVMtNmJCaC04RlZBWlNqekdpa1gtWk9aMzVSeWtMRjhZeXFPNC1SMXQ4cHhBbk5MYk5KQ1IzZXpUTjJMRWN3VWZqWWM2b2hnSWU4S2lCYlVPX0ozVEFyMFhvZnRrQ2ZWTzB3dWZGcjU5QllGMFFpdzJ5Mlp4OEZkanJVWDB5Z3FSd2tTOFNWOUl2ZlVKYkxqNW9PSzIxa3lWZnhoU2hJMlNRZ2lkOHJYX0s5azNkTU51a2E0Q01Bck9HTTFZdHB1TTlxNWYxVnAzbHdTYnBEN0VvLWk0TGYya25DcUpRemdHWkdaeTJjQ1JVcTFfNndRIiwiZSI6IkFRQUIifSwiZG9jdW1lbnRIYXNoIjoiNGJhYzY5Y2IxMWM2MTY0YjkyZjZjNTVjOTA5M2E1OTkxYjg4NTc5NmNjNjNjYzlkY2UwZDU0ZDcwZjE3OWE1YyIsImlzVG9PcmRlciI6dHJ1ZSwicHJldmlvdXNURFRoYXNoIjoiNDY4MGQ2NTgyZDI2OTM2M2VlNTBhZDgxMGIzNDA3MDU0YTYwMDVmMjY3NWI5MjE3MzZjZDk5NDRjOGU0MzFkZiJ9\",\"signatures\":[{\"protected\":\"eyJhbGciOiJSUzI1NiJ9\",\"signature\":\"pD22xByQinirwOZEnMQTDPFYSu02zvH8PkT71j2JGhtlmIUQZQm8RMDukzXgCd4H4L94NK46Z3ZypGglHjjpfCJI4vMuMPPDhVE8pI-SE0WmTyspegTtubDGlRCuPrQAxF3U_NA7-XJGipg8C61gIwGzaohou1mWpwUq-v-2UVHom9IoDZ5v7W0cbhjjXzm5jLWCa-5tv_OZeXBRlqnT_9y-gYPBy6OA19g33Vwg3xqDBqCCPbT_FtEtO12zot6MJiQ3GKEiBvaQsBWB2mvj8GqV1VkpSGTWa4VXQTUFo_sXLk4_TKx603HSUK5GJ_68vNqZkOj9aRIUZDq4XmDCjg\"}]}";
    @Autowired
    private TransferBlockRepository repository;

    @Test
    public void testSaveTDT() throws java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException {
        repository.save(new TransferBlock(blockHash, transferBlock, "current"));
        Optional<TransferBlock> tdt = repository.findById(blockHash);
        Assertions.assertEquals(tdt.get().getTransferBlockHash(),blockHash);
        Assertions.assertEquals(tdt.get().getTransferStatus(),"current");
//         Assertions.assertEquals(tdt.get().previousTransferBlockHash(), null);
    }

    @Test
    public void testAddingPlatformSignature() throws java.text.ParseException, com.nimbusds.jose.JOSEException, java.security.NoSuchAlgorithmException {
        TransferBlock tdt = new TransferBlock(blockHash, transferBlock, "current");
        final int initialLength = tdt.getTransferBlock().length();
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair keyPair = kpg.genKeyPair();
        tdt.addPlatformSignature(keyPair);
        final int finalLength = tdt.getTransferBlock().length();
        Assertions.assertTrue(finalLength > initialLength);
    }
}
