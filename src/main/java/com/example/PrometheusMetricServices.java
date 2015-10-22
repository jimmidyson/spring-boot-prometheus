package com.example;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.hotspot.GarbageCollectorExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PrometheusMetricServices implements CounterService, GaugeService {

  private final CollectorRegistry registry;

  private final ConcurrentMap<String, Gauge> gauges = new ConcurrentHashMap<>();

  public PrometheusMetricServices(CollectorRegistry registry) {
    this.registry = registry;
    new StandardExports().register(registry);
    new MemoryPoolsExports().register(registry);
    new GarbageCollectorExports().register(registry);
  }

  @Override
  public void increment(String name) {
    getOrRegisterGauge(name).inc();
  }

  @Override
  public void decrement(String name) {
    getOrRegisterGauge(name).dec();
  }

  private Gauge getOrRegisterGauge(String name) {
    return gauges.computeIfAbsent(name, k -> {
      Gauge gauge = Gauge.build().name(sanitizeName(k)).help(k).create();
      registry.register(gauge);
      return gauge;
    });
  }

  @Override
  public void submit(String name, double value) {
    getOrRegisterGauge(name).set(value);
  }

  @Override
  public void reset(String name) {
    gauges.computeIfPresent(name, (k, v) -> {
      v.set(0);
      return v;
    });
  }

  private String sanitizeName(String name) {
    return name.replaceAll("[^a-zA-Z0-9_]", "_");
  }

}

