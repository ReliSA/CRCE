package cz.zcu.kiv.ccer;

public interface Provider {

    public void storeComponent(Object component);

    public void runTestsOnComponent(Object component);
}
