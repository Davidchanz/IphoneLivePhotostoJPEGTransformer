package org.bubus.zambara.utils;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class SetCollectionConverter implements CollectionConverter<Set> {
    @Override
    public Collection<?> convert(Collection<?> collection) {
        return collection.stream().collect(Collectors.toSet());
    }
}
