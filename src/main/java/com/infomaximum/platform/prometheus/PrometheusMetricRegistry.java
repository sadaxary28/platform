package com.infomaximum.platform.prometheus;

import com.infomaximum.platform.prometheus.metric.JvmMetric;
import com.infomaximum.platform.prometheus.metric.PrometheusMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PrometheusMetricRegistry {

    private static final Logger log = LoggerFactory.getLogger(PrometheusMetricRegistry.class);

    private final List<PrometheusMetric> prometheusMetrics;

    public PrometheusMetricRegistry() {
        this.prometheusMetrics = new ArrayList<>();
    }

    public PrometheusMetricRegistry addDefaultMetrics() {
        prometheusMetrics.add(new JvmMetric());
        return this;
    }

    public PrometheusMetricRegistry addCustomMetric(PrometheusMetric prometheusMetric) {
        prometheusMetrics.add(prometheusMetric);
        return this;
    }

    public PrometheusMetricRegistry addCustomMetrics(List<PrometheusMetric> prometheusMetrics) {
        this.prometheusMetrics.addAll(prometheusMetrics);
        return this;
    }

    public void register() {
        prometheusMetrics.forEach(prometheusMetric -> {
            try {
                prometheusMetric.register();
                log.info("Register prometheus metric: {}", prometheusMetric.getName());
            } catch (Exception e) {
                log.warn("Unable register metric: {}. Message: {}", prometheusMetric.getName(), e.getMessage());
            }
        });
    }
}
