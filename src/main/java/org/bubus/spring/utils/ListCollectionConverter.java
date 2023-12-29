package org.bubus.spring.utils;

import org.bubus.spring.annotation.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ListCollectionConverter implements CollectionConverter<List>{
    @Override
    public Collection<?> convert(Collection<?> collection) {
        return collection.stream().collect(Collectors.toList());
    }
}
