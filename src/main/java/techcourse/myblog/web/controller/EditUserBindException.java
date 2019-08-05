package techcourse.myblog.web.controller;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

public class EditUserBindException extends BindException {
    public EditUserBindException(BindingResult bindingResult) {
        super(bindingResult);
    }
}