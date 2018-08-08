package cz.zcu.kiv.crce.restimpl.indexer.definition;

import java.util.Map;

/**
 * Created by ghessova on 08.05.2018.
 */
public class DispatcherDefinition {

    private Map<String, Dispatcher> dispatchers;

    public Map<String, Dispatcher> getDispatchers() {
        return dispatchers;
    }

    public void setDispatchers(Map<String, Dispatcher> dispatchers) {
        this.dispatchers = dispatchers;
    }

    public static class Dispatcher {
        private String dispatcher;
        private String providers;

        public String getDispatcher() {
            return dispatcher;
        }

        public void setDispatcher(String dispatcher) {
            this.dispatcher = dispatcher;
        }

        public String getProviders() {
            return providers;
        }

        public void setProviders(String providers) {
            this.providers = providers;
        }
    }
}
