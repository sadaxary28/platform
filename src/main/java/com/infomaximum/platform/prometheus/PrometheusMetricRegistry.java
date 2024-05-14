package com.infomaximum.platform.prometheus;

import com.infomaximum.platform.prometheus.metric.JvmMetric;
import com.infomaximum.platform.prometheus.metric.PrometheusMetric;

import java.util.ArrayList;
import java.util.List;

public class PrometheusMetricRegistry {

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
        prometheusMetrics.forEach(PrometheusMetric::register);
    }
}
