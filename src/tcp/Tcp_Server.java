package tcp;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Timer;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
  
import dfu.dfuThread;

public class Tcp_Server extends Thread{
	
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
    	//System.out.println('3'+'4');System.out.println("3"+"4");
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
    
    public void end()
    {
  	    
    }
    
    public void run()
    {
    	
    	
    	
    }
    
    public void Thread_create(String path)
    {
		dfu = new dfuThread(this.dfu_lock,path);
		dfu.start();
		
		//启动线程，等待100ms后，开始发送数据
		try {
			dfu.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dfu.method_notify();
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
            	  
            	//文件发送完毕
            	if(temp.equals("dfu_end")){
            		System.out.println("文件结束: " + new Date());
            		//break; //先不要退出，准备发送后续的文件
            	}
            	//关闭服务器命令
            	if(temp.equals("dfu_stop")){
            		System.out.println("下载结束: " + new Date());
            		Thread.sleep(100);
            		break;
            	}
            	
            	//接收到dfu开始发送的命令，启动线程
            	if(temp.equals("dfu_send"))
            	{
            		Thread_create("C:/dfu/no-key/save/image_sink.dfu");
            	}
            	
            	//接收到dfu_send1开始发送第二个DFU文件，启动线程
            	if(temp.equals("dfu_send1"))
            	{
            		Thread.sleep(1000);//等上一个线程完全结束  2017-10-19
            		Thread_create("C:/dfu/no-key/save/sink.dfu");
            	}
            	//接收到dfu_send2开始发送第三个DFU文件，启动线程
            	if(temp.equals("dfu_send2"))
            	{
            		Thread.sleep(1000);
            		Thread_create("C:/dfu/no-key/save/sink.dfu");
            	}
            	
            	//接收到dfu继续命令
            	if(temp.equals("dfu_go"))
            	{

            		dfu.method_notify();
            		if(dfu.send_ok == 1)
            		{
            			dfu.send_ok = 0;//rcv_dfu_go++;
            			System.out.println(rcv_dfu_go);
            			
            			OutputStream outputStream = socket.getOutputStream();
            			byte[] tempchars = new byte[10];
            	        //文件传输完成后，发送一串特殊数据
            	  	    for(int i = 0; i < 10; i++){
            	  	    	tempchars[i] = (byte) 0xAA;
            		    }
            	  	    outputStream.write(tempchars,0,10);
            	  	    System.out.println("end");
            		}
            	}
            	
                System.out.println("信息："+temp);
                /*
    			if(rcv_dfu_go == 2)
	   			 {
    				rcv_dfu_go = 0;
    				dfu.send_ok = 0;
	   				//break;
	   			 }
	   			 */
            }

            bufferedReader.close();
            inputStream.close();
            socket.close();
            System.out.println("socket close !");
            
        } catch (IOException e) {
        	socket.close();
            e.printStackTrace();         
        }
    } 
}