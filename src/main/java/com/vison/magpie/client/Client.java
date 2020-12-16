package com.vison.magpie.client;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import com.vison.magpie.message.Message;
import com.vison.magpie.message.MessageType;
import com.vison.magpie.message.Text;
import com.vison.magpie.message.User;

/**
 *
 * @author vison.cao <visonforcoding@gmail.com>
 */
public class Client {
    
    private static String host = "127.0.0.1";
    private static int port = 9527;
    private static final int MESSAGE_SIZE = 1024;//每次允许接受数据的最大长度

    private static String[] args;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        System.out.print(args);
        Socket socket = new Socket(host, port);
        Client.args = args;
        handle(socket);
        
        socket.close();
        System.out.println("disconnected.");
    }
    
    private static void handle(Socket socket) throws IOException {
        OutputStream output = socket.getOutputStream();
        var writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        handleLogin(writer);
        ReadHandler readHandler = new ReadHandler(socket);
        readHandler.start();
        Scanner scanner = new Scanner(System.in);
        for (;;) {
            String s = scanner.nextLine(); // 读取一行输入
            handText(s, writer);
        }
    }
    
    private static void handleLogin(BufferedWriter writer) throws IOException {
        String username = args[0];
        System.out.print(username);
        Message<User> message = new Message();
        message.setType(MessageType.LOGIN);
        User user = new User();
        user.setUsername(username);
        message.setBody(user);
        Gson gson = new Gson();
        String loginInfo = gson.toJson(message);
        writer.write(loginInfo);
        writer.newLine();
        writer.flush();
    }
    
    private static void handText(String s, BufferedWriter writer) throws IOException {
        Message<Text> message = new Message();
        message.setType(MessageType.TEXT);
        message.setFrom(args[0]);
        Text text = new Text();
        text.setTo(args[1]);
        text.setText(s);
        message.setBody(text);
        Gson gson = new Gson();
        String loginInfo = gson.toJson(message);
        writer.write(loginInfo);
        writer.newLine();
        writer.flush();
    }
    
}
