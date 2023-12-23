package com.systemvi.examples.httpexamples.discord;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WebHook {

    public WebHook(){

    }
    public void run(){
        try{
            String url="https://discord.com/api/webhooks/1185963639233069179/Nq3MsyshvgcxqxDVE5eEGA7IjtdyQTaLPQeCNgqwoLxTcNZgu1SFeHny4WpBQ91BFcSv";
            HttpClient client=HttpClient.newHttpClient();
            HttpRequest request= HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(
                    "{" +
                        "\"content\":\"Poruka se salje preko http requesta iz jave\"" +
                    "}")
                )
                .setHeader("Content-Type","application/json")
                .build();

            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
            System.out.println(response.statusCode());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
