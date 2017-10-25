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
     * Socket�����
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
		
		//�����̣߳��ȴ�100ms�󣬿�ʼ��������
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
            System.out.println("��������������ȴ��ͻ�������...");
            socket = serverSocket.accept();//���������ܵ����׽��ֵ�����,����һ��Socket����
            //socket.setSoTimeout(60000);
            //���ӳɹ�������ӡ�ͻ�����Ϣ
            //System.out.println("connected!");
            System.out.println("��ǰ�ͻ���ipΪ��" + socket.getInetAddress().getHostAddress());
            
            //��������������Ϳͻ�������
            InputStream inputStream = socket.getInputStream();//�õ�һ�������������տͻ��˴��ݵ���Ϣ
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);//���Ч�ʣ����Լ��ֽ���תΪ�ַ���
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);//���뻺����
            String temp = null;
            
            while((temp = bufferedReader.readLine()) != null){
            	  
            	//�ļ��������
            	if(temp.equals("dfu_end")){
            		System.out.println("�ļ�����: " + new Date());
            		//break; //�Ȳ�Ҫ�˳���׼�����ͺ������ļ�
            	}
            	//�رշ���������
            	if(temp.equals("dfu_stop")){
            		System.out.println("���ؽ���: " + new Date());
            		Thread.sleep(100);
            		break;
            	}
            	
            	//���յ�dfu��ʼ���͵���������߳�
            	if(temp.equals("dfu_send"))
            	{
            		Thread_create("C:/dfu/no-key/save/image_sink.dfu");
            	}
            	
            	//���յ�dfu_send1��ʼ���͵ڶ���DFU�ļ��������߳�
            	if(temp.equals("dfu_send1"))
            	{
            		Thread.sleep(1000);//����һ���߳���ȫ����  2017-10-19
            		Thread_create("C:/dfu/no-key/save/sink.dfu");
            	}
            	//���յ�dfu_send2��ʼ���͵�����DFU�ļ��������߳�
            	if(temp.equals("dfu_send2"))
            	{
            		Thread.sleep(1000);
            		Thread_create("C:/dfu/no-key/save/sink.dfu");
            	}
            	
            	//���յ�dfu��������
            	if(temp.equals("dfu_go"))
            	{

            		dfu.method_notify();
            		if(dfu.send_ok == 1)
            		{
            			dfu.send_ok = 0;//rcv_dfu_go++;
            			System.out.println(rcv_dfu_go);
            			
            			OutputStream outputStream = socket.getOutputStream();
            			byte[] tempchars = new byte[10];
            	        //�ļ�������ɺ󣬷���һ����������
            	  	    for(int i = 0; i < 10; i++){
            	  	    	tempchars[i] = (byte) 0xAA;
            		    }
            	  	    outputStream.write(tempchars,0,10);
            	  	    System.out.println("end");
            		}
            	}
            	
                System.out.println("��Ϣ��"+temp);
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