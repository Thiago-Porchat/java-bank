package dio.bank.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import dio.bank.exception.AccountNotFoundHereException;
import dio.bank.exception.PixInUseExcepetion;
import dio.bank.model.AccountWallet;
import dio.bank.model.MoneyAudit;
import static dio.bank.repository.CommonsRepository.checkFundsForTransaction;

public class AccountRepository {

    private final List<AccountWallet> accounts = new ArrayList<>();

    public AccountWallet create(final List<String> pix, final long initialFunds){
        if (!accounts.isEmpty()) {
            var pixInUse = accounts.stream().flatMap(a -> a.getPix().stream()).toList();
            for (var p : pix) {
                if  (pixInUse.contains(p)) {
            throw new PixInUseExcepetion("o pix'" + p + "'já está em uso");
          }
        }
        }
        var newAccount = new AccountWallet(initialFunds, pix);
        accounts.add(newAccount);
        return newAccount;
    }
        
        

    public void deposit(final String pix, final long fundsAmount) {
        var target = findByPix(pix);
        target.addMoney(fundsAmount, "depósito");
    }

    public long withdraw(final String pix, final long amount){
        var source = findByPix(pix);
        checkFundsForTransaction(source, amount);
        source.reduceMoney(amount);
        return amount;
    }

    public void transferMoney(final String sourcePix, final String targetPix, final long amount){
        var source = findByPix(sourcePix);
        checkFundsForTransaction(source, amount);
        var target = findByPix(targetPix);
        var message = "pix enviado de'" + sourcePix + "' para '" + targetPix + "'";
        target.addMoney(source.reduceMoney(amount), source.getService(), message);
    }

    public AccountWallet findByPix(final String pix) throws AccountNotFoundHereException {
        return accounts.stream()
        .filter(a -> a.getPix().contains(pix))
        .findFirst()
        .orElseThrow(() -> new AccountNotFoundHereException("A conta com a chave pix '" + pix + "' não existe ou foi encerrada."));
    }

    public List<AccountWallet> list(){
        return this.accounts;
    }

    public Map<OffsetDateTime, List<MoneyAudit>> getHistory(final String pix){
        var wallet = findByPix(pix);
        var audit = wallet.getFinancialTransactions();
        return audit.stream()
            .collect(Collectors.groupingBy(t -> t.createdAt().truncatedTo(ChronoUnit.SECONDS)));
    }



}
