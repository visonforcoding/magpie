package com.vison.magpie.server;

import com.vison.magpie.server.Handler;
import java.io.IOException;
import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vison.cao <visonforcoding@gmail.com>
 */
public class Server {

    private static final Logger log = Logger.getLogger(Server.class.getName());

    private static Map<String, Socket> sockMap = new HashMap();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        int port = 9527;
        ServerSocket ss = new ServerSocket(port);
        log.info(String.format("server is running in %s", port));
        for (;;) {
            Socket sock = ss.accept();
            System.out.println("connected from " + sock.getRemoteSocketAddress());
            Handler t = new Handler(sock, sockMap);
            t.start();
        }
    }

}
