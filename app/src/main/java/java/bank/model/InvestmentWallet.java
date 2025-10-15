package java.bank.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import java.bank.model.Money;

import lombok.Getter;

@Getter
public class InvestmentWallet extends Wallet {

    private final Investment investment;
    private final AccountWallet account;

    public InvestmentWallet(Investment investment, AccountWallet account, final long amount) {
        super(BankService.INVESTMENT);
        this.investment = investment;
        this.account = account;
        addMoney(account.reduceMoney(amount), getService(), "investimento");;
    }


    public void updateAmount(final long percent){
        var amount = getFunds() * percent / 100;
        var history = new Money(UUID.randomUUID(), BankService.INVESTMENT, "rendimentos", OffsetDateTime.now());
        var money = Stream.generate(() -> new Money(history)).limit(amount).toList();
        this.money.addAll(money);
    }


    @Override
    public String toString() {
        return super.toString() + "InvestmentWallet [investment=" + investment + ", account=" + account + "]";
    }

    
    
}
