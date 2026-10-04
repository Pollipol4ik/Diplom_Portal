package org.diplom_backend.exceptions;

public class SubjectTopicAlreadyExistsException extends AlreadyExistsException {
    /**
     * Принимает сообщение ошибки
     */
    public SubjectTopicAlreadyExistsException() {
        super("Такой топик уже существует");
    }
}
