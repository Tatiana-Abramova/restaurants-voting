package voting.error;

public class NotFoundException extends ApiException {
    public NotFoundException(String msg) {
        super(msg);
    }

    public NotFoundException(int id, String msg) {
        super("Entity with id = " + id + " not found" + (msg == null ? "" : msg));
    }
}