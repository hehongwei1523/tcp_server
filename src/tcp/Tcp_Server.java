package tcp;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
  
import dfu.dfuThread;

public class Tcp_Server {
	
    //public static final String SERVER_ADDR = "192.168.1.133";
    public static final String SERVER_PORT = "5000";
    
    public static ServerSocket serverSocket;
    public static Socket socket;
    
    private dfuThread dfu;
    private Object dfu_lock;
    /**
     * Socket服务端
     * @throws InterruptedException 
     * @throws IOException 
     */
    public static void main(String[] args) throws InterruptedException, IOException {
    	Object lock = new Object();
    	System.out.println('3'+'4');System.out.println("3"+"4");
    	Tcp_Server server = new Tcp_Server(lock);
    	server.method();
    }
    
    public static Socket get_Socket(){
    	return socket;
    }
    
    public static ServerSocket get_ServerSocket(){
    	return serverSocket;
    }
    
    public Tcp_Server(Object lock) 
    {
        this.dfu_lock = lock;
    }
    
    public void method() throws InterruptedException,IOException
    {
    	int rcv_dfu_go =0;
        try {
    		
            serverSocket=new ServerSocket(Integer.valueOf(SERVER_PORT));
            System.out.println("服务端已启动，等待客户端连接...");
            socket = serverSocket.accept();//侦听并接受到此套接字的连接,返回一个Socket对象
            //socket.setSoTimeout(60000);
            //连接成功，并打印客户端信息
            //System.out.println("connected!");
            System.out.println("当前客户端ip为：" + socket.getInetAddress().getHostAddress());
            
            //根据输入输出流和客户端连接
            InputStream inputStream = socket.getInputStream();//得到一个输入流，接收客户端传递的信息
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);//提高效率，将自己字节流转为字符流
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);//加入缓冲区
            String temp = null;
            
            while((temp = bufferedReader.readLine()) != null){
            	
            	//关闭服务器命令
            	if(temp.equals("dfu_end")){
            		break;
            	}
            		
            	//接收到dfu开始发送的命令，启动线程
            	if(temp.equals("dfu_send"))
            	{
            		dfu = new dfuThread(this.dfu_lock);
            		dfu.start();
            		
            		//启动线程，等待100ms后，开始发送数据
            		dfu.sleep(100);
            		dfu.method_notify();
            	}
            	
            	//接收到dfu继续命令
            	if(temp.equals("dfu_go"))
            	{
            		dfu.method_notify();
            		if(dfu.send_ok == 1)
            		{
            			rcv_dfu_go++;
            			System.out.println(rcv_dfu_go);
            			if(rcv_dfu_go == 2)
            			 {
            				break;
            			 }
            		}
            	}
            	
                System.out.println("信息："+temp);
            }
            
            /*
            OutputStream outputStream = socket.getOutputStream();//获取一个输出流，向客户端发送信息
            PrintWriter printWriter = new PrintWriter(outputStream);//将输出流包装成打印流
            printWriter.print("你好，服务端已关闭");
            printWriter.flush();
            socket.shutdownOutput();//关闭输出流
            
            //关闭相对应的资源
            printWriter.close();
            outputStream.close();
            */
            bufferedReader.close();
            inputStream.close();
            socket.close();
            
        } catch (IOException e) {
        	System.out.println(new Date());
        	socket.close();
            e.printStackTrace();
                        
        }
    } 
}