package org.bubus.zambara.utils;

import org.bubus.zambara.annotation.Component;

import java.util.Collection;

@Component
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
