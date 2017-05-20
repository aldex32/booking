package sinanaj.aldo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import sinanaj.aldo.model.Account;

@Transactional
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findAccountByUsername(String username);

    void deleteAccountByUsername(String username);
}
