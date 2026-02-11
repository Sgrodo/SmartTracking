
package it.univr.track.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import it.univr.track.SmartTrackApplication;

@CucumberContextConfiguration
@SpringBootTest(
        classes = SmartTrackApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class CucumberSpringConfig { }
