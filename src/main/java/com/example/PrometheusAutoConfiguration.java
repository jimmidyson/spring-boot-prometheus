package com.example;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.MetricsServlet;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.actuate.endpoint.MetricReaderPublicMetrics;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(CollectorRegistry.class)
@AutoConfigureBefore(MetricRepositoryAutoConfiguration.class)
public class PrometheusAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public CollectorRegistry metricRegistry() {
    return new CollectorRegistry();
  }

  @Bean
  @ConditionalOnMissingBean({PrometheusMetricServices.class, CounterService.class, GaugeService.class})
  public PrometheusMetricServices prometheusMetricServices(CollectorRegistry metricRegistry) {
    return new PrometheusMetricServices(metricRegistry);
  }

  @Bean
  public MetricReaderPublicMetrics prometheusPublicMetrics(CollectorRegistry metricRegistry) {
    PrometheusRegistryMetricReader reader = new PrometheusRegistryMetricReader(metricRegistry);
    return new MetricReaderPublicMetrics(reader);
  }

  @Bean
  public ServletRegistrationBean registerPrometheusExporterServlet(CollectorRegistry metricRegistry) {
    return new ServletRegistrationBean(new MetricsServlet(metricRegistry), "/prometheus");
  }

}
