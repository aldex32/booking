package sinanaj.aldo;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;
import sinanaj.aldo.model.Account;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountIT {

    private static final String TEST_USERNAME = "test";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_NEW_PASSWORD = "newPass";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String USERNAME_PARAM = "username";
    private static final String OLD_PASSWORD_PARAM = "oldPassword";
    private static final String NEW_PASSWORD_PARAM = "newPassword";

    private final TestRestTemplate restTemplate = new TestRestTemplate("admin", "WhisperAdmin");
    private String baseURL;

    @Value("${local.server.port}")
    private int port;

    @Before
    public void setUp() throws MalformedURLException {
        baseURL = "http://localhost:" + port;
    }

    @Test
    public void test1_registerAccount() {
        final Account account = new Account(TEST_USERNAME, TEST_PASSWORD);
        account.setEnabled(true);
        account.setRole(ROLE_USER);

        final ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseURL + "/account/admin", account, String.class);

        assertEquals("Account registration failed", HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Account registered", responseEntity.getBody());
    }

    @Test
    public void test2_updatePassword() {
        final Map<String, String> uriParams = Collections.singletonMap(USERNAME_PARAM, TEST_USERNAME);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseURL + "/account/update-password/{username}")
                .queryParam(OLD_PASSWORD_PARAM, TEST_PASSWORD)
                .queryParam(NEW_PASSWORD_PARAM, TEST_NEW_PASSWORD);

        final ResponseEntity<String> responseEntity = restTemplate.withBasicAuth(TEST_USERNAME, TEST_PASSWORD)
                .exchange(uriBuilder.buildAndExpand(uriParams).toUriString(), HttpMethod.PUT, HttpEntity.EMPTY, String.class);

        assertEquals("Password updating failed", HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Password updated", responseEntity.getBody());
    }

    @Test
    public void test3_updatePasswordErrorAccountNotFound() {
        final String accountNotExist = "accountNotExist";
        final Map<String, String> uriParams = Collections.singletonMap(USERNAME_PARAM, accountNotExist);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseURL + "/account/update-password/{username}")
                .queryParam(OLD_PASSWORD_PARAM, TEST_PASSWORD)
                .queryParam(NEW_PASSWORD_PARAM, TEST_NEW_PASSWORD);

        final ResponseEntity<String> responseEntity = restTemplate.withBasicAuth(TEST_USERNAME, TEST_NEW_PASSWORD)
                .exchange(uriBuilder.buildAndExpand(uriParams).toUriString(), HttpMethod.PUT, HttpEntity.EMPTY, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Account not found", responseEntity.getBody());
    }

    @Test
    public void test4_updatePasswordErrorOldPasswordNotMatching() {
        final String wrongOldPassword = "wrongPassword";
        final Map<String, String> uriParams = Collections.singletonMap(USERNAME_PARAM, TEST_USERNAME);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseURL + "/account/update-password/{username}")
                .queryParam(OLD_PASSWORD_PARAM, wrongOldPassword)
                .queryParam(NEW_PASSWORD_PARAM, TEST_NEW_PASSWORD);

        final ResponseEntity<String> responseEntity = restTemplate.withBasicAuth(TEST_USERNAME, TEST_NEW_PASSWORD)
                .exchange(uriBuilder.buildAndExpand(uriParams).toUriString(), HttpMethod.PUT, HttpEntity.EMPTY, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Old password not correct", responseEntity.getBody());
    }

    @Test
    public void test5_updateAccount() {
        final Account account = new Account(TEST_USERNAME, TEST_NEW_PASSWORD);
        account.setEnabled(false);
        account.setRole(ROLE_ADMIN);
        final HttpEntity<Account> requestEntity = new HttpEntity<>(account);

        final ResponseEntity<String> responseEntity =
                restTemplate.exchange(baseURL + "/account/admin/{username}", HttpMethod.PUT, requestEntity, String.class, TEST_USERNAME);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Account updated", responseEntity.getBody());
    }

    @Test
    public void test6_deleteAccount() {
        final ResponseEntity<String> responseEntity =
                restTemplate.exchange(baseURL + "/account/admin/{username}", HttpMethod.DELETE, HttpEntity.EMPTY, String.class, TEST_USERNAME);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Account deleted", responseEntity.getBody());
    }
}
