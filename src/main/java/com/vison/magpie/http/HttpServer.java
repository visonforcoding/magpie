package com.vison.magpie.http;
import java.io.IOException;

import com.vison.magpie.http.Handler;
import com.vison.magpie.http.HttpServer;
import org.apache.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
public class HttpServer {

    private static final Logger log = Logger.getLogger(HttpServer.class.getName());

    private static Map<String, Socket> sockMap = new HashMap();

    private static int port = 9527;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        int port = HttpServer.port;
        ServerSocket ss = new ServerSocket(port);
        log.info(String.format("server is running in %s of %s", port,name));
        for (;;) {
            Socket sock = ss.accept();
            System.out.println("connected from " + sock.getRemoteSocketAddress());
            Handler t = new Handler(sock);
            t.start();
        }
    }
}
