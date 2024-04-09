package voting;

public interface HasId {

    Integer id();

    default void checkNew() {
        if (id() != null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " must be new");
        }
    }
}
