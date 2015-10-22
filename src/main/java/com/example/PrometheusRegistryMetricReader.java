package com.example;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.reader.MetricReader;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PrometheusRegistryMetricReader implements MetricReader {

  private final CollectorRegistry registry;

  public PrometheusRegistryMetricReader(CollectorRegistry registry) {
    this.registry = registry;
  }


  @Override
  public Metric<?> findOne(String metricName) {
    for (Collector.MetricFamilySamples metricFamilySamples : Collections.list(registry.metricFamilySamples())) {
      for (Collector.MetricFamilySamples.Sample sample : metricFamilySamples.samples) {
        if (sample.name.equals(metricName)) {
          return new Metric<Number>(metricName, sample.value);
        }
      }
    }
    return null;
  }

  @Override
  public Iterable<Metric<?>> findAll() {
    return new Iterable<Metric<?>>() {
      @Override
      public Iterator<Metric<?>> iterator() {
        Set<Metric<?>> metrics = new HashSet<Metric<?>>();
        for (Collector.MetricFamilySamples metricFamilySamples : Collections.list(registry.metricFamilySamples())) {
          for (Collector.MetricFamilySamples.Sample sample : metricFamilySamples.samples) {
            metrics.add(new Metric<Number>(sample.name, sample.value));
          }
        }
        return metrics.iterator();
      }
    };
  }

  @Override
  public long count() {
    return Collections.list(registry.metricFamilySamples()).size();
  }
}
