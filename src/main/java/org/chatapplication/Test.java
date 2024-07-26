package org.chatapplication;

import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        String message = "[" + "a" + "]: " + "b" + "\n" + "to:" + "c";

        String content = message.split("to:")[0];
        String receiver = message.split("to:")[1];

        System.out.println(content + receiver);
        System.out.println();
    }
}
