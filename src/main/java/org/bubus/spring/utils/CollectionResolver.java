package org.bubus.spring.utils;

import java.util.Collection;

public class CollectionResolver {

    private CollectionConverter[] collectionConverters = {
            new ListCollectionConverter(),
            new SetCollectionConverter()
    };

    public  <T> Collection<T> convertToFieldCollection(Collection<?> collection, Class<T> fieldType) {
        for (CollectionConverter collectionConverter : this.collectionConverters) {
            if(collectionConverter.isSupport(fieldType))
                return collectionConverter.convert(collection);
        }
        throw new RuntimeException("Error to convert Bean Collection [" + collection + "] into [" + fieldType + "]");
    }
}
