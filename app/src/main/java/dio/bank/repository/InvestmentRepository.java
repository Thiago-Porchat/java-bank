package dio.bank.repository;

import java.util.ArrayList;
import java.util.List;

import dio.bank.exception.AccountWithInvestmentException;
import dio.bank.exception.InvestmentNotFoundException;
import dio.bank.exception.WalletNotFoundException;
import dio.bank.model.AccountWallet;
import dio.bank.model.Investment;
import dio.bank.model.InvestmentWallet;
import static dio.bank.repository.CommonsRepository.checkFundsForTransaction;

public class InvestmentRepository {

    private long nextId = 0;
    private final List<Investment> investments = new ArrayList<>();
    private final List<InvestmentWallet> wallets = new ArrayList<>();

    public Investment create(final long tax, final long initialFunds){
        this.nextId ++;
        var investment = new Investment(this.nextId, tax, initialFunds);
        investments.add(investment);
        return investment;
    }

    public InvestmentWallet initInvestment(final AccountWallet account, final long id){
        if (!wallets.isEmpty()) {
            var accountInUse = wallets.stream().map(InvestmentWallet::getAccount).toList();
            if  (accountInUse.contains(account)) {
                throw new AccountWithInvestmentException("a conta '" + account + "' já possui um investimento");
            }
        }
        var investment = findById(id);
        checkFundsForTransaction(account, investment.initialFunds());
        var wallet = new InvestmentWallet(investment, account, investment.initialFunds());
        wallets.add(wallet);
        return wallet;
    }
    
    public InvestmentWallet deposit(final String pix, final long funds){
        var wallet = findWalletByAccountPix(pix);
        wallet.addMoney(wallet.getAccount().reduceMoney(funds), wallet.getService(), "investimento");
        return wallet;
    }
    
    public InvestmentWallet withdraw(final String pix, final long funds){
        var wallet = findWalletByAccountPix(pix);
        checkFundsForTransaction(wallet, funds);
        wallet.getAccount().addMoney(wallet.reduceMoney(funds), wallet.getService(),"saque de investimentos");
        if (wallet.getFunds() == 0){
            wallets.remove(wallet);
        }
        return wallet;
    }

    public void updateAmount(final int percent){
        wallets.forEach(w -> w.updateAmount(percent));
    }

    public Investment findById(final long id){
        return investments.stream().filter(a -> a.id() == id)
                .findFirst()
                .orElseThrow(
                    () -> new InvestmentNotFoundException("o investinmento '" + id + "' não foi encontrado.")
                );
    }

    public InvestmentWallet findWalletByAccountPix(final String pix) {
        return wallets.stream()
                .filter(w -> w.getAccount().getPix().contains(pix))
                .findFirst()
                .orElseThrow(
                        () -> new WalletNotFoundException("a carteira não foi encontrada")
                );
    }
    
    public List<InvestmentWallet> listWallets(){
        return this.wallets;
    }

    public List<Investment> list() {
        return this.investments;
    }

    public void updateAmount() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAmount'");
    }

    
}


