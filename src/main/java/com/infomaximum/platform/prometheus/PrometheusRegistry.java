package com.infomaximum.platform.prometheus;

import com.infomaximum.platform.prometheus.metric.JvmMetric;
import com.infomaximum.platform.prometheus.metric.PrometheusMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PrometheusRegistry {

    private static final Logger log = LoggerFactory.getLogger(PrometheusRegistry.class);

    private final List<PrometheusMetric> prometheusMetrics;

    public PrometheusRegistry() {
        this.prometheusMetrics = new ArrayList<>();
    }

    public PrometheusRegistry withDefaultMetrics() {
        prometheusMetrics.add(new JvmMetric());
        return this;
    }

    public PrometheusRegistry withCustomMetric(PrometheusMetric prometheusMetric) {
        prometheusMetrics.add(prometheusMetric);
        return this;
    }

    public PrometheusRegistry withCustomMetrics(List<PrometheusMetric> prometheusMetrics) {
        this.prometheusMetrics.addAll(prometheusMetrics);
        return this;
    }

    public void register() {
        prometheusMetrics.forEach(prometheusMetric -> {
            prometheusMetric.register();
            log.info("Register prometheus metric: {}", prometheusMetric.getName());
        });
    }
}
