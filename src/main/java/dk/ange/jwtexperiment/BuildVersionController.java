package dk.ange.jwtexperiment;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

/*
 * An end-point to see what version the server is running (git commit hash)
 */

@RestController
public class BuildVersionController {

    @Value("${git.commit.id}")
    private String commitId;

    @GetMapping("/build-version")
    public String buildVersion() {
        return commitId;
    }
}
