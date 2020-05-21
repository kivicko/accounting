package com.kivilcimeray.accounting.conf;

import com.kivilcimeray.accounting.model.CreditTransaction;
import com.kivilcimeray.accounting.model.PaymentTransaction;
import com.kivilcimeray.accounting.model.Player;
import com.kivilcimeray.accounting.model.WithdrawTransaction;
import com.kivilcimeray.accounting.model.api.User;
import com.kivilcimeray.accounting.repository.PlayerRepository;
import com.kivilcimeray.accounting.repository.TransactionRepository;
import com.kivilcimeray.accounting.repository.UserRepository;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.kivilcimeray.accounting.util.AccountingUtils.$;

@Component
public class Bootstrapper implements SmartInitializingSingleton {

    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder encoder;

    private final TransactionRepository transactionRepository;

    public Bootstrapper(UserRepository userRepository, PlayerRepository playerRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void afterSingletonsInstantiated() {
        UUID kiviId = UUID.fromString("219168d2-1da4-4f8a-85d8-95b4377af3c1");
        UUID kardoId = UUID.fromString("328167d1-2da3-5f7a-86d7-96b4376af2c0");

        User user = new User(kiviId, "user", encoder.encode("user"));
        User admin = new User(kardoId, "admin", encoder.encode("admin"));

        user.addAuthority("USER");
        admin.addAuthority("USER");
        admin.addAuthority("ADMIN");

        this.userRepository.save(user);
        this.userRepository.save(admin);

        Player samplePlayer = new Player(5L, $(100));

        List<PaymentTransaction> transactionHistory = Arrays.asList(
                new CreditTransaction($(200), UUID.randomUUID(), samplePlayer),
                new WithdrawTransaction($(100), UUID.randomUUID(), samplePlayer)
        );

        transactionRepository.saveAll(transactionHistory);
    }
}