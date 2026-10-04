package org.diplom_backend.exceptions;

public class SchoolAlreadyExistException extends AlreadyExistsException {
    public SchoolAlreadyExistException(String name) {
        super("Школа" + name + " уже существует");
    }
}
