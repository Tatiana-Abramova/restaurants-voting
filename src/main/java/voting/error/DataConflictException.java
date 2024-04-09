package voting.error;

public class DataConflictException extends ApiException {
    public DataConflictException(String msg) {
        super(msg);
    }
}
