package com.agroknow.indexer;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MetricsRegistryHolder {
    private static final MetricRegistry METRICS = new MetricRegistry();

    private static final Logger LOG = LoggerFactory.getLogger("indexer.metrics.log");

    /**
     *
     */
    private MetricsRegistryHolder() {
        super();
    }

    public static void report() {
        for(String name : METRICS.getCounters().keySet()) {
            LOG.info("{}, {}", name, METRICS.getCounters().get(name).getCount());
        }
    }

    public static Counter getCounter(String name) {
        synchronized (METRICS) {
            return METRICS.counter(name);
        }
    }
}
