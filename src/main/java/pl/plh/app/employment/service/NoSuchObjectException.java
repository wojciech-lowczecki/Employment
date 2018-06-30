package pl.plh.app.employment.service;

public class NoSuchObjectException extends RuntimeException {
    public NoSuchObjectException() {
    }

    public NoSuchObjectException(Class entityClass, Long id) {
        super(String.format("%s object with id=%d does not exist", entityClass.getSimpleName(), id));
    }
}
