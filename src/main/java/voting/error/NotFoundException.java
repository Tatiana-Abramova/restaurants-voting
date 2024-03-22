package voting.error;

public class NotFoundException extends ApiException {
    public NotFoundException(String msg) {
        super(msg);
    }
}