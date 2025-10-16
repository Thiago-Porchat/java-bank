package dio.bank.exception;

public class AccountNotFoundHereException extends RuntimeException {

    public AccountNotFoundHereException(String message){
        super(message);
    }

}
