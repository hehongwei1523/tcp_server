package tcp;

import java.net.ServerSocket;
import java.net.Socket;
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
     * Socket�����
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException {
    	Object lock = new Object();
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
    
    public void method() throws InterruptedException
    {
        try {
    		
            serverSocket=new ServerSocket(Integer.valueOf(SERVER_PORT));
            System.out.println("��������������ȴ��ͻ�������...");
            socket = serverSocket.accept();//���������ܵ����׽��ֵ�����,����һ��Socket����
            
            //���ӳɹ�������ӡ�ͻ�����Ϣ
            //System.out.println("connected!");
            System.out.println("��ǰ�ͻ���ipΪ��" + socket.getInetAddress().getHostAddress());
            
            //��������������Ϳͻ�������
            InputStream inputStream = socket.getInputStream();//�õ�һ�������������տͻ��˴��ݵ���Ϣ
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);//���Ч�ʣ����Լ��ֽ���תΪ�ַ���
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);//���뻺����
            String temp = null;

            while((temp = bufferedReader.readLine()) != null){

            	//�رշ���������
            	if(temp.equals("dfu_end")){
            		break;
            	}
            		
            	//���յ�dfu��ʼ���͵���������߳�
            	if(temp.equals("dfu_send"))
            	{
            		dfu = new dfuThread(this.dfu_lock);
            		dfu.start();
            		
            		//�����̣߳��ȴ�10ms�󣬿�ʼ��������
            		dfu.sleep(10);
            		dfu.method_notify();
            	}
            	
            	//���յ�dfu��ʼ���͵���������߳�
            	if(temp.equals("dfu_go"))
            	{
            		dfu.method_notify();
            	}
            	
                System.out.println("����˽��յ�����Ϣ��"+temp);
            }
            
            OutputStream outputStream = socket.getOutputStream();//��ȡһ�����������ͻ��˷�����Ϣ
            PrintWriter printWriter = new PrintWriter(outputStream);//���������װ�ɴ�ӡ��
            printWriter.print("��ã�������ѹر�");
            printWriter.flush();
            socket.shutdownOutput();//�ر������
            
            
            //�ر����Ӧ����Դ
            printWriter.close();
            outputStream.close();
            bufferedReader.close();
            inputStream.close();
            socket.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
}