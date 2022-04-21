package com.vison.magpie.websocket;

import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WsServer {


    private static Map<String, Socket> sockMap = new HashMap();

    private static int port = 9527;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        int port = WsServer.port;
        ServerSocket ss = new ServerSocket(port);
        log.info(String.format("server is running in %s of %s", port,name));
        for (;;) {
            Socket sock = ss.accept();
            log.info("connected from " + sock.getRemoteSocketAddress());
            Handler t = new Handler(sock);
            t.start();
        }
    }
}
