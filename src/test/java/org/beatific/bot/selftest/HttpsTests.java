package org.beatific.bot.selftest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;

public class HttpsTests {

    /**
     * Example for sending a synchronous GET request
     *
     * @throws java.io.IOException
     * @throws InterruptedException
     */

    @Test
    public void demo1() {

        System.out.println("Demo 1");
        HttpRequest request = HttpRequest.newBuilder()
                // .uri(URI.create("https://mswgmags.sktelecom.com:8443/Mobile/config/config.json?M=1"))
                .uri(URI.create("https://mswgmags.sktelecom.com:8443/Mobile/config/config.json?M=1"))
                .GET()
                .build();

        var client = HttpClient.newHttpClient();

        HttpResponse.BodyHandler<String> asString = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response;
        try {
            response = client.send(request, asString);
            int statusCode = response.statusCode();
            System.out.printf("Status Code: %s%n", statusCode);
            HttpHeaders headers = response.headers();
            System.out.printf("Response Headers: %s%n", headers);
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

}
