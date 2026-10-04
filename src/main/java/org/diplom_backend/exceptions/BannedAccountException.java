package org.diplom_backend.exceptions;

public class BannedAccountException extends RuntimeException {
    public BannedAccountException() {
        super("Ваш аккаунт был удален");
    }
}
