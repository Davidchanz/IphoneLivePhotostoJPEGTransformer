package org.bubus.validation;

public interface Validator {
    boolean validate(String command);
    String process(String command);
    String getErrorMessage();
}
