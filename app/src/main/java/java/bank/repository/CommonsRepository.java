package java.bank.repository;

import java.bank.exception.NoFundsEnoughException;
import java.bank.model.BankService;
import java.bank.model.MoneyAudit;
import java.bank.model.Wallet;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import java.bank.model.Money;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonsRepository {

    public static void checkFundsForTransaction(final Wallet source, final long amount){
        if (source.getFunds() < amount){
            throw new NoFundsEnoughException("sua conta não tem dinheiro o suficiente para realizar essa transação");
        }
    }

    public static List<Money> generateMoney(final UUID transactionId, final long funds, final String description) {
    var history = new MoneyAudit(transactionId, BankService.ACCOUNT, description, OffsetDateTime.now());
    return Stream.generate(() -> new Money(history)).limit(funds).toList();
    }
}
