package dk.ange.jwtexperiment;

import com.nimbusds.jose.jwk.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class JwkSetRestController {

    @Autowired
    private JWKSet jwkSet;

    @GetMapping("/.well-known/jwks.json")
    @CrossOrigin(origins = "*")
    public Map<String, Object> keys() {
        return this.jwkSet.toJSONObject();
    }
}
