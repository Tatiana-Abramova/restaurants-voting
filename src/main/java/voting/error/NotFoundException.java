package voting.error;

public class NotFoundException extends ApiException {
    public NotFoundException(String msg) {
        super(msg);
    }

    public <T> NotFoundException(int id, Class<T> clazz) {
        super(clazz.getSimpleName() + "with id=" + id + " not found");

    }
}