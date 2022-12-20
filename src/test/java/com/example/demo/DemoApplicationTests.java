package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class DemoApplicationTests {

	private final HttpClient client = HttpClient.newHttpClient();
	private final AtomicInteger successCounter = new AtomicInteger(0);
	private final AtomicInteger  failureCounter = new AtomicInteger(0);

	private final int POOL_SIZE = 20;
	private final int REQUEST_COUNT = 200_000;

	@BeforeEach
	void resetCounters() {
		successCounter.set(0);
		failureCounter.set(0);
	}

	@Test
	void testMono() {

		var executor = Executors.newFixedThreadPool(POOL_SIZE);

		var futures = IntStream.range(0, REQUEST_COUNT)
			.mapToObj((__) -> CompletableFuture.supplyAsync(this::doRequest, executor))
			.toList();

		futures.forEach(future -> recordStatusCode(future.join()));
		LOGGER.info("Success: " + successCounter);
		LOGGER.info("Failures: " + failureCounter);
	}

	private int doRequest() {
		try {
			var request = HttpRequest.newBuilder()
				.uri(new URI("http://localhost:8080/v1/hello/delay/mono"))
				.GET()
				.build();

			var response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response.statusCode();
		} catch (Exception e) {
			return 500;
		}
	}

	private void recordStatusCode(int statusCode) {
		if (statusCode == 200) {
			successCounter.incrementAndGet();
		} else {
			failureCounter.incrementAndGet();
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DemoApplicationTests.class);
}
