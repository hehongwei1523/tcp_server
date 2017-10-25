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
		//构造函数
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
			
	        OutputStream outputStream = Tcp_Server.get_Socket().getOutputStream();//获取一个输出流，向客户端发送信息
	        
	        DataInputStream read = new DataInputStream(new FileInputStream(file));
			try {
	            System.out.println("DataInputStream：");
	            // 一次读多个数据
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
	            // 读入多个数据到数组中，charread为一次读取字符数
	            while ((charread = read.read(tempchars)) != -1) {
	
	            	    //当检测到读到的数组长度不等于1023时，表示为最后一块。去除后缀。
	            	    if(charread < tempchars.length)
	            	     {
	            	    	//charread = charread - 16;
	            	    	/*//难道是因为这里耗时太久，导致notify没执行？ 2017-9-6
	            	    	System.out.printf("tempchars[%d]:%x \n", 0 , tempchars[0]);
	            	    	System.out.printf("tempchars[%d]:%x \n", 1 , tempchars[1]);
	            	    	System.out.printf("tempchars[%d]:%x \n", charread-2 , tempchars[charread-2]);
	            	    	System.out.printf("tempchars[%d]:%x \n", charread-1 , tempchars[charread-1]);
	            	    	*/
	            	     }
	            		method_wait();
	            		//System.out.println("wait");
	            		//dfu数据发送次数
	            		indexchars[4] = (byte) ((times&0xff00)>>8);
	            		indexchars[5] = (byte) (times&0x00ff);
		            	indexchars[6] = (byte) ((charread&0xff00)>>8);
		            	indexchars[7] = (byte) (charread&0x00ff);
                        /*
		            	src:源数组；	srcPos:源数组要复制的起始位置；
		            	dest:目的数组；	destPos:目的数组放置的起始位置；	length:复制的长度。
		            	*/
		            	System.arraycopy(tempchars, 0, indexchars, 10, charread);
		            	
		            	length = length + charread;
		              
		            	outputStream.write(indexchars,0,charread+10);
		            	outputStream.flush();
		            	  
		              
		              times++;
		              System.out.println("发送次数： " + times + " 长度： " + charread);
	            }
	            System.out.println( "文件长度length为:"+ length);
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
	//线程挂起
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
	
	//线程唤醒
	public void method_notify(){
		synchronized (this.lock) {
		  this.lock.notify();
		}
	}

}
