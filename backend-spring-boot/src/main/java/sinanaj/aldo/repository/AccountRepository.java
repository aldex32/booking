package sinanaj.aldo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sinanaj.aldo.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findAccountByUsername(String username);
}
