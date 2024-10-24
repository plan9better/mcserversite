package com.example.website;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class Main{

    @GetMapping("/run-command")
    public static void main(String args[]) throws IOException {
        SseEmitter emitter = new SseEmitter();
        
        String command = "/minecraft/spigot/run.sh";
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("bash", "-c", command);
        Process process = builder.start();

        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    emitter.send(line); // Send each line to the client
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
    }
}
