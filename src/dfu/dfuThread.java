package dfu;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import tcp.Tcp_Server;

public class dfuThread extends Thread{
	
	private Object lock;
    public static final String dfu_file_path = "C:/dfu/app40.dfu"; //"C:/dfu/no-key/save/dump1.dfu"; //"C:/dfu/no-key/save/headset.dfu";  // 
    
    public static int send_ok = 0; 
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
	            int times = 0;
	            // ���������ݵ������У�charreadΪһ�ζ�ȡ�ַ���
	            while ((charread = read.read(tempchars)) != -1) {
	
	            		method_wait();
	            		
		              if (charread == tempchars.length) {
		            	  //ÿ�η���һ��������
		            	  length = length + tempchars.length;
		            	  outputStream.write(tempchars);
		            	  outputStream.flush();
		              }
		              else{
		            	  //���һ�η���ʣ�������
		            	  length = length + charread;
		            	  for(int i = 0; i < charread; i++){
		            		  outputStream.write(tempchars[i]);
		            		  outputStream.flush();
		            	  }
		              }
		              times++;
		              System.out.println("���ʹ����� " + times + "���ȣ� " + charread);
	            }
	            System.out.println( "�ļ�����lengthΪ:"+ length);
	            outputStream.flush();
	            
	            send_ok = 1;
	            /*
	            System.out.println(new Date());
	            this.sleep(3000);
	            System.out.println(new Date());
	            //�ļ�������ɺ󣬷���һ����������
          	    for(int i = 0; i < 4; i++){
        		  outputStream.write(0xaa);
        	    }
          	    */
	            //Tcp_Server.get_Socket().close(); 
	            
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

    private long start_time = System.currentTimeMillis();
    //System.out.println(start_time);
	//�̹߳���
	public void method_wait() throws IOException{
		synchronized (this.lock) {
			try {
				/*
            	if(System.currentTimeMillis() - start_time > 20000)
            	{
            		System.out.println("timeout socket close!");
            		Tcp_Server.get_Socket().close();
            	}
            	*/
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

}
