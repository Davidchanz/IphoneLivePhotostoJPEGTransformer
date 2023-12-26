package org.bubus.validation;

public interface Validator {
    boolean validate(String command);
    String getErrorMessage();
}
