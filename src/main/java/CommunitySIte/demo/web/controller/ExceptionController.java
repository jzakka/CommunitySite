package CommunitySIte.demo.web.controller;

import CommunitySIte.demo.exception.ObjectNotExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(ObjectNotExistsException.class)
    public String objectNotExistsExceptionHandler(ObjectNotExistsException e, Model model) {
        log.error(e.getClass().getName());
        log.error(e.getMessage());

        model.addAttribute("exception", e);

        return "error/4xx";
    }
}
