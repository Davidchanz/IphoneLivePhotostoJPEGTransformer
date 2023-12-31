package org.bubus.command;

public abstract class AbstractCommand implements Command{
    protected int order;

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
