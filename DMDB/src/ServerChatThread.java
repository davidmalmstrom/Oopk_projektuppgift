import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Dexter on 2017-02-12.
 */
public class ServerChatThread extends ChatThread implements Runnable {
    private ArrayList<String> nameList;
    private Socket socket;
    private ServerChatFrame serverChatFrame;

    private Hashtable<Socket, PrintWriter> socketPrintWriterHashtable;
    private Hashtable<Socket, String> socketStringHashTable;

    public ServerChatThread(Socket socket, String name, Color textColor){
        this.socket = socket;
        this.name = name;
        this.textColor = textColor;

        nameList = new ArrayList<>();

        /*try{
            outText = new PrintWriter(socket.getOutputStream(), true);
            inText = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (UnknownHostException e){
            System.out.println("bam");
        }catch (IOException f){
            System.out.println("b88oom");
        }*/

        socketPrintWriterHashtable = new Hashtable<>();
        socketStringHashTable = new Hashtable<>();

        //socketPrintWriterHashtable.put(socket, outText);
        System.out.println("yo what up");
        serverChatFrame = new ServerChatFrame(name, textColor);


    }     // TODO: fixa så att serverchatthread kan skapas flera gånger till samma serverchatframe, eller ska serverchatthread skapa flera trådar självt?

    @Override
    public void run() {
        done = false;
        while(!done){
            try{
                String s = inText.readLine();
                System.out.println(s);
                if (s==null){
                    System.out.println("Server disconnect");
                    done = true;
                }else {
                    String[] parsedArray = XmlParser.parse(s);
                    serverChatFrame.writeToChat(parsedArray[0], parsedArray[1], Color.decode(parsedArray[2]));
                }
            }catch (IOException e){

            }
        }
    }

    public void createInputListenerThread() {
        //ny socket med mera
    }

    public void setServerChatFramesServerChatThread() {
        serverChatFrame.setServerChatThread(this);
    }

    public String toString(){
        String retString = "Samtal nr " + serverChatFrame.getServerNumber();
        /*for (int i=0 ; i<nameList.size()-1 ; i++){
            retString = retString + nameList.get(i) + ", ";
        }
        retString = retString + nameList.get(nameList.size()-1);*/
        return retString;
    }

    public void disconnect(Socket socket){
        /*for (int i=0;i<socketList.size();i++){
            if (socket==socketList.get(i)){
                socketList.remove(i);
            }
        }*/
    }

    @Override
    public void sendText(String str) {
        for (Socket key : socketPrintWriterHashtable.keySet()) {
            socketPrintWriterHashtable.get(key).println(str);

        }
    }

    public void addToSocketList(Socket socket){
        ReceivingThread receivingThread = new ReceivingThread(socket);
        receivingThread.start();
    }

    private class ReceivingThread extends Thread implements Runnable {
        private Socket threadSocket;
        BufferedReader myInText;
        PrintWriter myOutText;

        ReceivingThread(Socket threadSocket) {
            this.threadSocket = threadSocket;
            try{
                myInText = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));
                myOutText = new PrintWriter(threadSocket.getOutputStream(), true);
            }catch (UnknownHostException e){
                System.out.println("bam");
            }catch (IOException f){
                System.out.println("b88oom");
            }
            socketPrintWriterHashtable.put(threadSocket, myOutText);
        }

        @Override
        public void run() {
            done = false;
            while(!done){
                try{
                    String s = myInText.readLine();
                    System.out.println(s);
                    if (s==null){
                        System.out.println("Server disconnect in receivThread");
                        done = true;
                    }else {
                        for (Socket key : socketPrintWriterHashtable.keySet()) {
                            if (!key.equals(threadSocket)) {
                                socketPrintWriterHashtable.get(key).println(s);
                            }
                        }
                        String[] parsedArray = XmlParser.parse(s);
                        serverChatFrame.writeToChat(parsedArray[0], parsedArray[1], Color.decode(parsedArray[2]));
                    }
                }catch (IOException e){

                }
            }
        }
    }
}
