package sinanaj.aldo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sinanaj.aldo.model.Account;
import sinanaj.aldo.repository.AccountRepository;

import javax.validation.constraints.NotNull;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/admin", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> register(@RequestBody @NotNull Account account) {

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(account);

        return ok("Account registered");
    }

    @RequestMapping(value = "/admin/{username}", method = RequestMethod.PUT, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> update(
            @PathVariable("username") @NotNull String username,
            @RequestBody @NotNull Account account) {

        Account currentAccount = accountRepository.findAccountByUsername(username);

        if (currentAccount == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        currentAccount.setUsername(account.getUsername());
        currentAccount.setPassword(passwordEncoder.encode(account.getPassword()));
        currentAccount.setEnabled(account.isEnabled());
        currentAccount.setRole(account.getRole());

        accountRepository.save(currentAccount);

        return ResponseEntity.ok("Account updated");
    }

    @RequestMapping(value = "/update-password/{username}", method = RequestMethod.PUT, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> updatePassword(
            @PathVariable("username") @NotNull String username,
            @RequestParam(value = "oldPassword") @NotNull String oldPassword,
            @RequestParam(value = "newPassword") @NotNull String newPassword) {

        Account currentAccount = accountRepository.findAccountByUsername(username);

        if (currentAccount == null) {
            return ResponseEntity.badRequest().body("Account not found");
        }

        boolean oldPasswordMatch = passwordEncoder.matches(oldPassword, currentAccount.getPassword());

        if (!oldPasswordMatch) {
            return ResponseEntity.badRequest().body("Old password not correct");
        }

        currentAccount.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(currentAccount);

        return ok("Password updated");
    }

    @RequestMapping(value = "/admin/{username}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAccount(@PathVariable(value = "username") @NotNull String username) {

        Account currentAccount = accountRepository.findAccountByUsername(username);

        if (currentAccount == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        accountRepository.deleteAccountByUsername(username);

        return ResponseEntity.ok("Account deleted");
    }
}
