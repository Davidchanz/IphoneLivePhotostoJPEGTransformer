package org.bubus.spring.reader;

import org.bubus.spring.bean.BeanDefinition;
import org.bubus.spring.bean.IoCContainer;
import org.bubus.spring.context.Context;

public interface BeanDefinitionReader {
    IoCContainer<BeanDefinition> scan();
    Context run();
}
