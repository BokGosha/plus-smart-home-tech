package ru.yandex.practicum.aggregator;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.service.DiscoveryDebugService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DiscoveryDebugRunner implements ApplicationRunner {

    public static final Logger log = LoggerFactory.getLogger(DiscoveryDebugRunner.class);

    private final DiscoveryDebugService discoveryDebugService;

    @Override
    public void run(ApplicationArguments args) {
        List<String> list = List.of("collector", "aggregator", "analyzer");
        for (String s : list) {
            log.warn("Service instances for serviceId={}", s);
            discoveryDebugService.findInstances(s)
                    .forEach(instance -> log.warn(
                            "\nService instance:\nserviceId={},\nhost={},\nport={},\nuri={},\nmetadata={}",
                            instance.serviceId(),
                            instance.host(),
                            instance.port(),
                            instance.uri(),
                            instance.metadata()
                    ));
        }
    }
}
