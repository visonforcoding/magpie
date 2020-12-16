package com.vison.magpie.client;

import com.vison.magpie.server.Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author vison.cao <visonforcoding@gmail.com>
 */
public class ReadHandler extends Thread {

    private static final Logger log = Logger.getLogger(Server.class.getName());

    Socket sock;

    public ReadHandler(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run() {
        try (InputStream input = this.sock.getInputStream()) {
            try (OutputStream output = this.sock.getOutputStream()) {
                handle(input, output);
            }
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    private static void handle(InputStream input, OutputStream output) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        for (;;) {
            String resp = reader.readLine();
            System.out.println(resp);
        }

    }

}
