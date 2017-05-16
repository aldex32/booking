package sinanaj.aldo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationIT {

    private TestRestTemplate template;
    private URL base;

    @Value("${local.server.port}")
    private int port;

    @Before
    public void setUp() throws MalformedURLException {
        template = new TestRestTemplate("admin", "WhisperAdmin");
        base = new URL("http://localhost:" + port);
    }

    @Test
    public void testAuthentication() throws IllegalStateException, IOException {
        ResponseEntity<String> response = template.getForEntity(base.toString() + "/api", String.class);

        assertEquals("Authentication failed", HttpStatus.OK, response.getStatusCode());
    }
}
