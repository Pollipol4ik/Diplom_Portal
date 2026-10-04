package org.diplom_backend.exceptions;

public class NotYourCommentException extends NotEnoughRightsException {
    public NotYourCommentException() {
        super("Нельзя изменять чужой комментарий");
    }
}
