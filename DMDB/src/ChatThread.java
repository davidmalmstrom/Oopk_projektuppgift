import java.awt.*;
import java.io.*;
import java.lang.Thread;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
/**
 * Created by Dexter on 2017-02-09.
 */
public class ChatThread extends Thread implements Runnable{
    private int hostPortNumber;
    protected String name;
    protected Color textColor;
    private InetAddress hostIP;
    private Socket socket;
    private ChatFrame chatFrame;       // TODO: fixa så de inte stänger av direkt utan visar "putte har loggat ut".
    protected BufferedReader inText;
    protected PrintWriter outText;
    private InputStream inFile;
    private OutputStream outFile;
    private Thread receivingThread;
    protected Boolean done;

    public ChatThread(Socket socket, String name, Color textColor){
        this.socket = socket;
        this.name = name;
        this.textColor = textColor;
        System.out.println(socket);

        try{
            outText = new PrintWriter(socket.getOutputStream(), true);
            inText = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (UnknownHostException e){
            System.out.println("bam");
        }catch (IOException f){
            System.out.println("b88oom");
        }

        chatFrame = new ChatFrame(name, textColor);

    }

    public ChatThread() {

    }

    public void run(){
        chatFrame.setChatThread(this);
        done = false;
        while(!done){
            try{
                String s = inText.readLine();
                System.out.println(s);
                if (s==null){
                    System.out.println("Server disconnect");
                    chatFrame.frameClose();
                    done = true;
                }else {
                    String[] parsedArray = XmlParser.parse(s);
                    chatFrame.writeToChat(parsedArray[0], parsedArray[1], Color.decode(parsedArray[2]));
                }
            }catch (IOException e){

            }
        }
    }

    protected void runMethod() {

    }

    public void closeThread(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*done = true;    //hur funkar detta med reveivingthread?
        receivingThread.stop();  //hur göra?*/

    }

    public void sendText(String str){
        outText.println(str);
    }

    public void receiveText(String str){

    }

}
