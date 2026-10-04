package org.diplom_backend.exceptions;

public class LoginFailException extends RuntimeException {
    public LoginFailException() {
        super("Не удалось верифицировать ваш аккаунт");
    }
}
