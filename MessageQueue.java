/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asambhava.groupchatws;

/**
 *
 * @author sarthak_z9
 */



import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.locks.ReentrantLock;


public class MessageQueue {
    public static ReentrantLock lock = new ReentrantLock();
    public static  Queue<String> messageQueue;
    public static  Queue<Socket> lastSender;
    public static ArrayList<Socket> subscribers;
    public static void main(String[] args) throws IOException 
    {
        System.out.println("Server is listening");
        ServerSocket ss = new ServerSocket(8888);
        
        //start message sender service;
        messageQueue = new LinkedList<>();
        lastSender = new LinkedList<>();
        subscribers = new ArrayList<>();
        
        MessageSender msgSen = new MessageSender(messageQueue, lastSender, subscribers, lock);
        Thread mst = new Thread(msgSen);
        mst.start();
        while (true) 
        {
            Socket s = null;
            try
            {
                // socket object to receive incoming client requests
                s = ss.accept();
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                if(!subscribers.contains(s))
                {
                    subscribers.add(s);
                    System.out.println(subscribers.size() );
                }
                ClientHandler conn = new ClientHandler(s, dis, dos, messageQueue, lastSender, lock);
                Thread t = new Thread(conn);
                // Invoking the start() method
                t.start();     
            }
            catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }
    }
}

class MessageSender extends Thread{
    public Queue<String> messageQueue;
    public Queue<Socket> lastSender;
    public ArrayList<Socket> subscribers;
    ReentrantLock lock;
    public MessageSender(Queue<String> m, Queue<Socket> l, ArrayList<Socket> s, ReentrantLock lock) 
    {
        messageQueue = m;
        lastSender = l;
        subscribers = s;
        this.lock = lock; 
    }
  
    @Override
    public void run() 
    { 
        while(true) {
            //System.out.println("11111");
            
            lock.lock();
            
            try {
            
                if(!messageQueue.isEmpty()){

                    String mess = messageQueue.remove();
                    Socket lastS = lastSender.remove();

                    System.out.println("+++++++++++++++++++++++");
                    for(Socket s : subscribers){
                        System.out.println("-----------------------");
                        /*if(s==null)
                        {
                            subscribers.remove(s);
                        }*/
                        if(s==lastS)
                            continue;
                        try{
                            System.out.println("coming"+mess);
                            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                            dos.writeUTF(mess);
                        }
                        catch(Exception e){
                            System.out.println("messaging service broked");
                            //break;
                        }

                    } 
                }
            } finally {
                lock.unlock();
            }
        }    
    }
}



class ClientHandler extends Thread {
    final Queue<String> messageQueue;
    public Queue<Socket> lastSender;
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    String rr;
    ReentrantLock lock;
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, Queue<String> m, Queue<Socket> l, ReentrantLock lock ) 
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        messageQueue = m;
        lastSender = l;
        this.lock = lock;
    }
    
    
    @Override
    public void run() 
    {   
        while (true) 
        {
            try {
                // receive the message from client
                rr = dis.readUTF();
                System.out.println(rr);
                lock.lock();
                try {
                    messageQueue.add(rr);
                    System.out.println("queue "+messageQueue.size());
                    lastSender.add(s);
                } finally {
                    lock.unlock();
                }
                if(rr.equals("862hjvhb7854byv6e5w314v"))
                {
                    System.out.println("removing from subscriber list");    //remove please
                    this.s.close();
                    break;
                }
            } catch (Exception e) {
                if(s==null){
                    System.out.println("socket gone");
                    break;
                }
            }
        }
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();
             
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
