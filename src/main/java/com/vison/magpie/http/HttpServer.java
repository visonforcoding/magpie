package com.vison.magpie.http;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class HttpServer {
    private static int port = 8081;

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(port); // 监听指定端口
        log.info(String.format("server in running in port %d",port));
        for (;;) {
            Socket sock = ss.accept();
            System.out.println("connected from " + sock.getRemoteSocketAddress());
            Thread t = new Handler(sock);
            t.start();
        }
    }
}
