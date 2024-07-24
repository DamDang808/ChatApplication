package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class UnitTest {
    private GreetClient client;

    @Before
    public void setup() throws IOException {
        client = new GreetClient();
        client.startConnection("127.0.0.1", 4444);

    }

    @After
    public void tearDown() throws IOException {
        client.stopConnection();
    }

    @Test
    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() throws IOException {
        String res1 = client.sendMessage("hello");
        String res2 = client.sendMessage("world");
        String res3 = client.sendMessage("!");
        String res4 = client.sendMessage(".");

        assert res1.equals("hello");
        assert res2.equals("world");
        assert res3.equals("!");
        assert res4.equals("good bye");
    }
}
