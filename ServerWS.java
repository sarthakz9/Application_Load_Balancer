/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asambhava.groupchatws;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sarthak_z9
 */
public class ServerWS extends WebSocketServer{
    
    private static int TCP_PORT = 9000;
    private Set<WebSocket> conns;
    private Socket socket;
    public ServerWS(int port) {
        super(new InetSocketAddress(port));
        TCP_PORT = port;
        conns = new HashSet<>();
        
        //connecting with messaging service
        socket = null;
        try
        {
            socket = new Socket("127.0.0.1", 8888);
            System.out.println("Instance "+TCP_PORT+" Connected to messaging service");
            MessageFromOtherInstances mfoi = new MessageFromOtherInstances(this, socket);
            Thread mfoiT = new Thread(mfoi);
            mfoiT.start();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        System.out.println("Hello");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conns.add(conn);
        System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        conns.remove(conn);
        System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if(message.equals("FTI Pass Alpha destroy")){
            System.out.println("Failing this instance as REQUESTED");
            int x=0;
            x=x/x; // haa haa haa
        }
        System.out.println("Message from client: " + message);
        DataOutputStream dos;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(message);
        } catch (IOException ex) {
            Logger.getLogger(ServerWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (WebSocket sock : conns) {
            if(sock!=conn)
                sock.send(message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        //ex.printStackTrace();
        if (conn != null) {
            conns.remove(conn);
            // do some thing if required
        }
        System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }
    
    public void HandleMessageFOI(String m){
        for (WebSocket sock : conns) {
            sock.send(m);
        }
    }

    public static void main(String[] args){
        ServerWS server;
        if(System.getenv("PORT")!=null){
        System.out.println(System.getenv("PORT")+"oooopsss");
        server = new ServerWS(Integer.parseInt(System.getenv("PORT")));
        }
        else
        {
            System.out.println("null hai ye");
            server = new ServerWS(9000);
        }
        server.start();

    }
}

class MessageFromOtherInstances extends Thread{
    ServerWS server;
    Socket socket;
    public MessageFromOtherInstances(ServerWS ser,Socket s){
        server = ser;
        socket = s;
    }
    
    @Override
    public void run() 
    {  
        while (true) 
        {
            try {
                // receive the message from client
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String rr = dis.readUTF();
                System.out.println("coming to instance "+rr);
                server.HandleMessageFOI(rr);
            } catch (IOException e) {
                e.printStackTrace();
                if(socket==null)
                    break;
            }
        }
    
    }
}