package org.bubus.spring.listener;

import org.bubus.spring.bpp.BeanPostProcessor;
import org.bubus.spring.configurator.factory.BeanPostProcessorFactory;
import org.bubus.spring.context.InternalContext;
import org.bubus.spring.context.state.ContextStarted;

import java.util.Set;

public class BeanPostProcessorContextListener implements ContextListener<ContextStarted>{
    private InternalContext internalContext;
    @Override
    public void config(InternalContext internalContext) {
        this.internalContext = internalContext;
    }

    @Override
    public void listen() {
        BeanPostProcessorFactory beanPostProcessorFactory = this.internalContext.getContextListenerRegister().getConfigurator(BeanPostProcessorFactory.class);
        Set<BeanPostProcessor> beanPostProcessors = this.internalContext.getContextListenerRegister().getConfigurators(BeanPostProcessor.class);
        beanPostProcessorFactory.process(beanPostProcessors);
    }
}
