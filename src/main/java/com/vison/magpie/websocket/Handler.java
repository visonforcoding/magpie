package com.vison.magpie.websocket;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Handler extends Thread {
    Socket sock;

    public Handler(Socket sock) {
        this.sock = sock;
    }

    public void run() {
        try (InputStream input = this.sock.getInputStream()) {
            try (OutputStream output = this.sock.getOutputStream()) {
                handle(input, output);
            }
        } catch (Exception e) {
            log.error(String.valueOf(e.getClass()));
            try {
                this.sock.close();
            } catch (IOException ioe) {
            }
            log.info("client disconnected.");
        }
    }

    private void handle(InputStream input, OutputStream output) throws IOException, InterruptedException {
        log.info("Process new http request...");
        var writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        Scanner s = new Scanner(input, StandardCharsets.UTF_8);
        String data = s.useDelimiter("\r\n").next();
        Matcher get = Pattern.compile("^GET").matcher(data);
        if (get.find()) {
            log.info("match get");
            String matchWebsocketKey = "";
            while (s.hasNext()) {
                String thisLine = s.next();
                Matcher matchWebsocket = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(thisLine);
                if (matchWebsocket.find()) {
                    matchWebsocketKey = matchWebsocket.group(1);
                    System.out.print(matchWebsocket.group());
                    handlerWebsocket(matchWebsocketKey,writer);
                    while (true){
                        int count=-1;
                        byte[] buff=new byte[1024];
                        count=input.read(buff);
                        System.out.println("接收的字节数："+count);
                        for(int i=0;i<count-6;i++){
                            buff[i+6]=(byte)(buff[i%4+2]^buff[i+6]);
                        }
                        System.out.println("接收的内容："+new String(buff, 6, count-6, "UTF-8"));
                        Thread.sleep(100);
                    }
                }
            }
        }
    }

    private void handlerWebsocket(String secWebSocketKey, BufferedWriter writer) throws IOException {
        log.info("hand with header");
        String secWebSocket = "";
        try {
            byte[] secWebSocketKeyByte = (secWebSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes(StandardCharsets.UTF_8);
            var digest = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_1).
                    digest(secWebSocketKeyByte);
            secWebSocket = Base64.getEncoder().encodeToString(digest);
            System.out.print(secWebSocket);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        writer.write("HTTP/1.1 101 Switching Protocols\r\n");
        writer.write("Connection: Upgrade\r\n");
        writer.write("Upgrade: websocket\r\n");
        writer.write("Sec-WebSocket-Accept: " + secWebSocket + "\r\n");
        writer.write("\r\n"); // 空行标识Header和Body的分隔
        writer.flush();

    }

}