package com.vison.magpie.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vison.magpie.message.Message;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.log4j.Logger;
import com.vison.magpie.message.MessageType;
import com.vison.magpie.message.Text;
import com.vison.magpie.message.User;
import java.lang.reflect.Type;

/**
 *
 * @author vison.cao <visonforcoding@gmail.com>
 */
public class Handler extends Thread {

    private static final Logger log = Logger.getLogger(Server.class.getName());

    Socket sock;
    Map<String, Socket> sockMap;

    public Handler(Socket sock, Map sockMap) {
        this.sock = sock;
        this.sockMap = sockMap;
    }

    @Override
    public void run() {
        try (InputStream input = this.sock.getInputStream()) {
            try (OutputStream output = this.sock.getOutputStream()) {
                handle(input, output);
            }
        } catch (Exception e) {
            System.out.print(e);
            try {
                this.sock.close();
            } catch (IOException ioe) {
            }
            System.out.println("client disconnected.");
        }
    }

    private void handle(InputStream input, OutputStream output) throws IOException {
        var writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        for (;;) {
            String s = reader.readLine();
            log.info(s);
            handMessage(s, writer);
        }

    }

    private void handMessage(String s, BufferedWriter writer) throws IOException {
        Gson gson = new Gson();
        Message message = gson.fromJson(s, Message.class);
        switch (message.getType()) {
            case LOGIN:
                handLogin(s, writer);
                break;
            case TEXT:
                handText(s, writer);
                break;
        }
    }

    private void handLogin(String s, BufferedWriter writer) throws IOException {
        log.info("hand login..");
        Gson gson = new Gson();
        Type jsonType = new TypeToken<Message<User>>() {
        }.getType();
        Message<User> message = gson.fromJson(s, jsonType);
        this.sockMap.put(message.getBody().getUsername(), this.sock);
        log.info(String.format("在线用户数%s", this.sockMap.size()));
        writer.newLine();
        writer.flush();
    }

    private void handText(String s, BufferedWriter writer) throws IOException {
        log.info("hand text..");
        Gson gson = new Gson();
        Type jsonType = new TypeToken<Message<Text>>() {
        }.getType();
        Message<Text> message = gson.fromJson(s, jsonType);
        log.info(message.getBody().getText());
        String chatTo = message.getBody().getTo();
        Socket chatToSocket = this.sockMap.get(chatTo);
        var toWriter = new BufferedWriter(new OutputStreamWriter(chatToSocket.getOutputStream(), StandardCharsets.UTF_8));
        toWriter.write("from " + message.getFrom() + " :" + message.getBody().getText());
        toWriter.newLine();
        toWriter.flush();
    }
}
