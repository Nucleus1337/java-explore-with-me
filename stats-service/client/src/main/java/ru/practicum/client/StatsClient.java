package ru.practicum.client;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;

@Service
public class StatsClient extends BaseClient {

  public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
    super(
        builder
            .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
            .requestFactory(HttpComponentsClientHttpRequestFactory::new)
            .build());
  }

  public ResponseEntity<Object> addHit(EndpointHitDto endpointHitDto) {
    return post("/hit", endpointHitDto);
  }

  public ResponseEntity<Object> findStatistics(
      String start, String end, String[] uris, boolean unique) {
    if (uris == null) {
      return findStatisticsForAll(start, end, unique);
    } else {
      Map<String, Object> parameters =
          Map.of("start", start, "end", end, "uris", uris, "unique", unique);

      return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }
  }

  private ResponseEntity<Object> findStatisticsForAll(String start, String end, boolean unique) {
    Map<String, Object> parameters = Map.of("start", start, "end", end, "unique", unique);

    return get("/stats?start={start}&end={end}&unique={unique}", parameters);
  }
}
