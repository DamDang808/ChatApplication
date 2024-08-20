package org.chatapplication;

import com.google.common.hash.Hashing;

import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        String mess =  "abc: exit\n abc";
        System.out.println(Hashing.sha256().hashBytes(mess.getBytes()).toString().length());
    }
}
