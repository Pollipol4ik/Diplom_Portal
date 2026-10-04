package org.diplom_backend.exceptions;

public class NotThreadException extends RuntimeException {
    public NotThreadException() {
        super("К этому комментарию нельзя делать треды");
    }
}
