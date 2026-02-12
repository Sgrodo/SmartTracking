
package it.univr.track.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import it.univr.track.SmartTrackApplication;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(
        classes = SmartTrackApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class CucumberSpringConfig { }
