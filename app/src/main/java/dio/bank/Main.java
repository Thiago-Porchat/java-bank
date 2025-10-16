package dio.bank;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;

import dio.bank.exception.AccountNotFoundHereException;
import dio.bank.exception.NoFundsEnoughException;
import dio.bank.exception.WalletNotFoundException;
import dio.bank.repository.AccountRepository;
import dio.bank.repository.InvestmentRepository;

public class Main {

    private final static AccountRepository accountRepository = new AccountRepository();
    private final static InvestmentRepository investmentRepository = new InvestmentRepository();
    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println("olá, seja bem vindo ao DIO Bank");
        while (true) {
            System.out.println("Selecione a operação desejada:");
            System.out.println("1 - Criar uma conta");
            System.out.println("2 - Criar um investimento");
            System.out.println("3 - Criar uma carteira de investimento");
            System.out.println("4 - Depositar na conta");
            System.out.println("5 - Sacar da conta");
            System.out.println("6 - Transferencia entre contas");
            System.out.println("7 - Investir");
            System.out.println("8 - Sacar investimento");
            System.out.println("9 - Listar contas");
            System.out.println("10 - Listar investimentos");
            System.out.println("11 - Listar carteiras de investimento");
            System.out.println("12 - Atualizar investimentos");
            System.out.println("13 - Histórico de conta");
            System.out.println("14 - Sair");
            var option = scanner.nextInt();
            switch (option) {
                case 1 -> createAccount();
                case 2 -> createInvestment();
                case 3 -> createWalletInvestment();
                case 4 -> deposit();
                case 5 -> withdraw();
                case 6 -> transferToAccount();
                case 7 -> incInvestment();
                case 8 -> rescueInvestment();
                case 9 -> accountRepository.list().forEach(a -> System.out.println(a));
                case 10 -> investmentRepository.list().forEach(a -> System.out.println(a));
                case 11 -> investmentRepository.listWallets().forEach(a -> System.out.println(a));
                case 12 -> {
                    investmentRepository.updateAmount();
                    System.out.println("Investimentos reajustados");
                }
                case 13 -> checkHistory();
                case 14 -> System.exit(0);
                default -> System.out.println("Opção inválida");
            }
        }
    }
    private static void createAccount() {
        System.out.println("Informe as chaves pix (separadas por ';')");
        var pix = Arrays.stream(scanner.next().split(";")).toList();
        System.out.println("Informe o valor inicial de depósito");
        var amount = scanner.nextLong();
        var wallet = accountRepository.create(pix, amount);
        System.out.println("conta criada: " + wallet);

    }

    private static void createInvestment() {
        System.out.println("Informe a taxa do investimento");
        var tax = scanner.nextInt();
        System.out.println("Informe o valor inicial de depósito");
        var initialFunds = scanner.nextLong();
        var investment = investmentRepository.create(tax, initialFunds);
        System.out.println("investimento criado: " + investment);
    }

    private static void withdraw() {
        System.out.println("Informe a chave pix da conta para saque");
        var pix = scanner.next();
        System.out.println("Informe o valor que será sacado");
        var amount = scanner.nextLong();
        try {
        accountRepository.withdraw(pix, amount);
       } catch (NoFundsEnoughException | AccountNotFoundHereException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void deposit() {
        System.out.println("Informe a chave pix da conta para depósito");
        var pix = scanner.next();
        System.out.println("Informe o valor que será depositado");
        var amount = scanner.nextLong();
        try {
        accountRepository.deposit(pix, amount);
        } catch (AccountNotFoundHereException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void transferToAccount() {
        System.out.println("Informe a chave pix da conta de origem");
        var source = scanner.next();
        System.out.println("Informe a chave pix da conta de destino");
        var target = scanner.next();
        System.out.println("Informe o valor que será depositado");
        var amount = scanner.nextLong();
        try {
        accountRepository.transferMoney(source, target, amount);
        } catch (AccountNotFoundHereException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void createWalletInvestment() {
        System.out.println("Informe a chave pix da conta");
        var pix = scanner.next();
        var account = accountRepository.findByPix(pix);
        System.out.println("Informe o identificador do investimento");
        var investmentId = scanner.nextInt();
        var investmentWallet = investmentRepository.initInvestment(account, investmentId);
        System.out.println("conta de investimento criada: " + investmentWallet);

    }

    private static void incInvestment() {
        System.out.println("Informe a chave pix da conta para investir");
        var pix = scanner.next();
        System.out.println("Informe o valor que será investido");
        var amount = scanner.nextLong();
        try {
        investmentRepository.deposit(pix, amount);
        } catch (WalletNotFoundException | AccountNotFoundHereException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void rescueInvestment() {
        System.out.println("Informe a chave pix da conta para resgate do investimento");
        var pix = scanner.next();
        System.out.println("Informe o valor que será sacado");
        var amount = scanner.nextLong();
        try {
        investmentRepository.withdraw(pix, amount);
       } catch (NoFundsEnoughException | AccountNotFoundHereException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void checkHistory() {
        System.out.println("informe a chave pix da conta para verificar extrato");
        var pix = scanner.next();
        try {
            var sortedHistory = accountRepository.getHistory(pix);
            sortedHistory.forEach((k, v) -> {
                System.out.println(k.format(DateTimeFormatter.ISO_DATE_TIME));
                System.out.println(v.getFirst().transactionId());
                System.out.println(v.getFirst().description());
                System.out.println("R$" + v.size()/100);
            });
        } catch (AccountNotFoundHereException ex) {
            System.out.println(ex.getMessage());

        }
    }
}