package org.bubus.validation.prefix;

import org.bubus.validation.Validator;

public interface PrefixValidator extends Validator {
    String getCommand(String arg);
}
