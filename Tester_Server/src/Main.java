import javax.xml.xpath.XPathEvaluationResult;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.security.spec.RSAOtherPrimeInfo;
import java.time.chrono.ThaiBuddhistEra;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    private static final int port=8080;
    private static  ServerSocket socket;
    private static final AtomicBoolean isRunning=new AtomicBoolean(true);
    public static void main(String args[]){
        try{
            socket=new ServerSocket(port);
            System.out.println("client connected to:"+port);
            System.out.print("Server is Running");
            Thread t = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        System.out.print(".");
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                System.out.println();
            });
            t.start();
            while(isRunning.get()){
                try{
                    Socket client=socket.accept();
                    new  Thread(()-> handel(client,t)).start();

                }
                catch (Exception e){
                    System.out.println("client error" +e.getMessage());
                }
                t.interrupt();
            }
        }
        catch (Exception e){
            System.out.println("socket failed:"+e.getMessage());
        }
    }

    private static void handel(Socket client, Thread t) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                OutputStream output = client.getOutputStream()
        ) {
            String requestLine= reader.readLine();
            if(requestLine==null){
                return;
            }

            String[] token=requestLine.split(" ");
            String method=token[0];
            String path=token[1];
            int contentLength=0;
            String line;
            while(!(line=reader.readLine()).isEmpty()){
                if(line.toLowerCase().startsWith("content-length")){
                    contentLength=Integer.parseInt(line.split(":")[1].trim());
                }
            }

            if(method.equals("GET") && path.equals("/home")){
                welcome(output);
            }
            else if(method.equals("GET") && path.equals("/download")){
                downloadResponse(output);

            }
            else if(method.equals("GET") && path.equals("/upload")){
                uploadResponse(reader,contentLength,output);

            }
            else if (method.equals("GET") && path.equals("/shutdown")) {
                handleShutdown(output);
            } else {
                respondText(output, 404, "Not Found");
            }
        }
        catch (Exception e){
            System.out.println("Something fialed in client response"+e.getMessage());
        }

    }

    private static void uploadResponse(BufferedReader reader,int contentLength, OutputStream output) throws IOException {
        char[] body = new char[contentLength];
        int read=reader.read(body);
        System.out.println("Recived "+read+" byte in /upload");
        respondText(output,200,"Upload Recived:"+read+" byte");
    }

    private static void downloadResponse(OutputStream output) throws IOException {
        int size=10;
        int totalbytes=size*1024*1024;
        byte[] buffer=new byte[1024];
        //this will hold all 0 , so file will be array of byte with 0s as value

        String headers="HTTP/1.1 200 OK\r\n"+
                "Content-Type: application/octet-stream\r\n" +
                "Content-Length:"+totalbytes+"\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        output.write(headers.getBytes());

        int sent=0;
        while(sent<totalbytes){
            output.write(buffer);
            sent+=buffer.length;
        }
        System.out.println("Downloaded the file");
        output.flush();
    }

 private static void handleShutdown(OutputStream out) throws IOException {
        respondText(out, 200, "Server is shutting down...");
        try{
            Thread.sleep(1000);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("Shutdown Completed");
        isRunning.set(false);
        socket.close();
    }


    private static void welcome(OutputStream output) throws IOException {
        String message="Welcome to my custom httpServer";
        String response="HTTP/1.1 200 OK\r\n"+
                "Content-Type: text/plain\r\n"+
                "Content-Length:"+message.length()+"\r\n"+
                "Connection: close\r\n"+
                "\r\n"+
                message;
        output.write(response.getBytes());
        output.flush();
        //output flush here to make the output clear from trash.That is data from before 
    }

    private static void respondText(OutputStream output,int code,String message) throws IOException {
        String response="HTTP/1.1 "+code+" OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: "+message.length()+"\r\n" +
                "\r\n"+
                message;
        output.write(response.getBytes());
        output.flush();
        
    }
}
