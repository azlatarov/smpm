package com.acm.data.repo;

import com.acm.data.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsRepo extends JpaRepository<Account, Long> {

}
