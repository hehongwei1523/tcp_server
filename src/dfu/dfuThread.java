package dfu;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import tcp.Tcp_Server;

public class dfuThread extends Thread{
	
	private Object lock;
    public static final String dfu_file_path = "C:/dfu/app1.dfu";  //"C:/dfu/no-key/combined2.dfu";//
    
	public dfuThread(Object lock){
		//���캯��
		super();
		this.lock = lock;		
	}
	
	@Override
	public void run(){ 
		// TODO Auto-generated method stub
		
		try{
			File file=new File(dfu_file_path);
			
			if(!file.exists()||file.isDirectory())
	            throw new FileNotFoundException();
			
	        OutputStream outputStream = Tcp_Server.get_Socket().getOutputStream();//��ȡһ�����������ͻ��˷�����Ϣ
	        
	        DataInputStream read = new DataInputStream(new FileInputStream(file));
			try {
	            System.out.println("DataInputStream��");
	            // һ�ζ��������
	            byte[] tempchars = new byte[1023];
	            int charread = 0;
	            int length = 0;
	
	            // ���������ݵ������У�charreadΪһ�ζ�ȡ�ַ���
	            while ((charread = read.read(tempchars)) != -1) {
	
	            		method_wait();
	            		
		              if (charread == tempchars.length) {
		            	  //ÿ�η���һ��������
		            	  length = length + tempchars.length;
		            	  outputStream.write(tempchars);
		              }
		              else{
		            	  //���һ�η���ʣ�������
		            	  length = length + charread;
		            	  for(int i = 0; i < charread; i++){
		            		  outputStream.write(tempchars[i]);
		            	  }
		              }
	            }
	            System.out.println( "length:"+ length);
	               
			}catch (Exception e1) {
	            e1.printStackTrace();
	        } finally {
	            if (read != null) {
	                try {
	                	read.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
		}catch(Exception e2)
		{
			e2.printStackTrace();
		}
	    			
	}
	
	//�̹߳���
	public void method_wait(){
		synchronized (this.lock) {
			try {
        		//System.out.println("begin wait");				
				this.lock.wait();
				//System.out.println("wait end");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//�̻߳���
	public void method_notify(){
		synchronized (this.lock) {
		  this.lock.notify();
		}
	}
	
	private void dfu_send() throws IOException {
		// TODO Auto-generated method stub
		
			File file=new File(dfu_file_path);
			
			if(!file.exists()||file.isDirectory())
	            throw new FileNotFoundException();
			
            OutputStream outputStream = Tcp_Server.get_Socket().getOutputStream();//��ȡһ�����������ͻ��˷�����Ϣ
            
            DataInputStream read = new DataInputStream(new FileInputStream(file));
			try {
	            System.out.println("DataInputStream��");
	            // һ�ζ��������
	            byte[] tempchars = new byte[1023];
	            int charread = 0;
	            int length = 0;

	            // ���������ݵ������У�charreadΪһ�ζ�ȡ�ַ���
	            while ((charread = read.read(tempchars)) != -1) {

	            		method_wait();
	            		
		              if (charread == tempchars.length) {
		            	  //ÿ�η���һ��������
		            	  length = length + tempchars.length;
		            	  outputStream.write(tempchars);
		              }
		              else{
		            	  //���һ�η���ʣ�������
		            	  length = length + charread;
		            	  for(int i = 0; i < charread; i++){
		            		  outputStream.write(tempchars[i]);
		            	  }
		              } 
	            }
	            System.out.println( "length:"+ length);
	               
			}catch (Exception e1) {
	            e1.printStackTrace();
	        } finally {
	            if (read != null) {
	                try {
	                	read.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
		    			
		}

}
