package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {
    public static void main(String[] args) {
        EchoServer server = new EchoServer();
        GreetClient client = new GreetClient();
        Thread serverThread = new Thread(() -> {
            try {
                server.start(4444);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        serverThread.start();

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String in;
        try {
            client.startConnection("127.0.0.1", 4444);
            while ((in = input.readLine()) != null) {
                String response = client.sendMessage(in);
                System.out.println(response);
                if (".".equals(in)) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
