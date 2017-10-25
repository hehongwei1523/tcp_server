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
	
	final int FILE_LONG = 1023;//1000;
	private Object lock;
    //public static final String dfu_file_path = "C:/dfu/no-key/save/sink.dfu";
    		// //"C:/dfu/app40.dfu"; // "C:/dfu/no-key/combined.dfu";  //
    
	private String dfu_file_path;
    public static int send_ok = 0; 
	public dfuThread(Object lock,String path){
		//���캯��
		super();
		this.dfu_file_path = path;
		this.lock = lock;
	}
	
	@Override
	public void run(){ 
		// TODO Auto-generated method stub
		
		try{
			File file=new File(dfu_file_path);
			//System.out.println(file.length());
			
			if(!file.exists()||file.isDirectory())
	            throw new FileNotFoundException();
			
	        OutputStream outputStream = Tcp_Server.get_Socket().getOutputStream();//��ȡһ�����������ͻ��˷�����Ϣ
	        
	        DataInputStream read = new DataInputStream(new FileInputStream(file));
			try {
	            System.out.println("DataInputStream��");
	            // һ�ζ��������
	            byte[] tempchars = new byte[FILE_LONG];
	            byte[] indexchars = new byte[FILE_LONG+10];
	            int charread = 0;
	            int length = 0;
	            int times = 0;
	            
	            indexchars[0] = 'F';
	            indexchars[1] = 'I';
	            indexchars[2] = 'L';
	            indexchars[3] = 'E';
	            
	            indexchars[8] = 'C';
	            indexchars[9] = 'R';

	            System.out.println(new Date());
	            // ���������ݵ������У�charreadΪһ�ζ�ȡ�ַ���
	            while ((charread = read.read(tempchars)) != -1) {
	
	            	    //����⵽���������鳤�Ȳ�����1023ʱ����ʾΪ���һ�顣ȥ����׺��
	            	    if(charread < tempchars.length)
	            	     {
	            	    	//charread = charread - 16;
	            	    	/*//�ѵ�����Ϊ�����ʱ̫�ã�����notifyûִ�У� 2017-9-6
	            	    	System.out.printf("tempchars[%d]:%x \n", 0 , tempchars[0]);
	            	    	System.out.printf("tempchars[%d]:%x \n", 1 , tempchars[1]);
	            	    	System.out.printf("tempchars[%d]:%x \n", charread-2 , tempchars[charread-2]);
	            	    	System.out.printf("tempchars[%d]:%x \n", charread-1 , tempchars[charread-1]);
	            	    	*/
	            	     }
	            		method_wait();
	            		//System.out.println("wait");
	            		//dfu���ݷ��ʹ���
	            		indexchars[4] = (byte) ((times&0xff00)>>8);
	            		indexchars[5] = (byte) (times&0x00ff);
		            	indexchars[6] = (byte) ((charread&0xff00)>>8);
		            	indexchars[7] = (byte) (charread&0x00ff);
                        /*
		            	src:Դ���飻	srcPos:Դ����Ҫ���Ƶ���ʼλ�ã�
		            	dest:Ŀ�����飻	destPos:Ŀ��������õ���ʼλ�ã�	length:���Ƶĳ��ȡ�
		            	*/
		            	System.arraycopy(tempchars, 0, indexchars, 10, charread);
		            	
		            	length = length + charread;
		              
		            	outputStream.write(indexchars,0,charread+10);
		            	outputStream.flush();
		            	  
		              
		              times++;
		              System.out.println("���ʹ����� " + times + " ���ȣ� " + charread);
	            }
	            System.out.println( "�ļ�����lengthΪ:"+ length);
	            //outputStream.flush();
	            
	            send_ok = 1;

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
