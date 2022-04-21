package com.vison.magpie.eioWs;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;

import com.vison.magpie.server.Server;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WsServer extends WebSocketServer {

    public static HashSet<WebSocket> conns = new HashSet<>();

    public WsServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        WsServer.conns.add(conn);
        conn.send("Welcome to the server!"); //This method sends a message to the new client
        broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
        System.out.println("new connection to " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        WsServer.conns.remove(conn);
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("received message from "	+ conn.getRemoteSocketAddress() + ": " + message);
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        System.out.println("received ByteBuffer from "	+ conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("server started successfully");
    }


    public static void main(String[] args) {
        String host = "localhost";
        int port = 8088;
        WebSocketServer server = new WsServer(new InetSocketAddress(host, port));
        Thread wsServerThread = new Thread(server,"wsServerThread");
        wsServerThread.start();
        MqWatcher mqWatcher = new MqWatcher();
        mqWatcher.setWebSocketServer(server);
        Thread mqWatcherThread = new Thread (mqWatcher, "mqWatcherThread");
        mqWatcherThread.start();qweqwe
    }
}