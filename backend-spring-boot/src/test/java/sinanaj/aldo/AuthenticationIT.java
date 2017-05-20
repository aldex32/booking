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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationIT {

    private TestRestTemplate template;
    private String baseURL;

    @Value("${local.server.port}")
    private int port;

    @Before
    public void setUp() throws MalformedURLException {
        baseURL = "http://localhost:" + port;
    }

    @Test
    public void testAuthentication() throws IllegalStateException, IOException {
        template = new TestRestTemplate("admin", "WhisperAdmin");

        ResponseEntity<String> response = template.getForEntity(baseURL + "/api", String.class);

        assertEquals("Authentication failed", HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void authenticationMustFail() throws IllegalStateException, IOException {
        template = new TestRestTemplate("admin", "invalidPassword");

        ResponseEntity<String> response = template.getForEntity(baseURL + "/api", String.class);

        assertEquals("Authentication must failed", HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
