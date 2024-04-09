package voting.error;

public class InvalidTimeException extends ApiException {
    public InvalidTimeException(String msg) {
        super(msg);
    }

    public InvalidTimeException() {
        super("Vote can't be changed for today");
    }
}
