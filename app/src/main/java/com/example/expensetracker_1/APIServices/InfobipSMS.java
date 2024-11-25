package com.example.expensetracker_1.APIServices;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InfobipSMS {
    public static void sendSMS(String apiKey, String senderId, String recipientPhone, String message) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                // Ensure no special characters in phone numbers or sender ID
                // Clean sender ID if necessary

                // Construct the JSON payload
                String jsonPayload = "{"
                        + "\"messages\":["
                        + "{"
                        + "\"destinations\":["
                        + "{\"to\":\"" + recipientPhone + "\"}"
                        + "],"
                        + "\"from\":\"" + senderId + "\","
                        + "\"text\":\"" + message + "\""
                        + "}"
                        + "]"
                        + "}";

                System.out.println("Sending JSON Payload: " + jsonPayload);

                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonPayload);

                Request request = new Request.Builder()
                        .url("https://51x1ej.api.infobip.com/sms/2/text/advanced")
                        .addHeader("Authorization", "App " + apiKey)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .post(body)
                        .build();

                // Execute the request
                Response response = client.newCall(request).execute();

                String responseBody = response.body() != null ? response.body().string() : "null";
                System.out.println("Response Code: " + response.code());
                System.out.println("Response Body: " + responseBody);

                // Handle response
                if (!response.isSuccessful()) {
                    System.out.println("Failed to send SMS. HTTP Code: " + response.code());
                    System.out.println("Response Body: " + responseBody);
                } else {
                    System.out.println("SMS sent successfully.");
                }

            } catch (Exception e) {
                System.out.println("Error occurred while sending SMS.");
                e.printStackTrace();
            }
        });
    }
}
