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
		//构造函数
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
			
	        OutputStream outputStream = Tcp_Server.get_Socket().getOutputStream();//获取一个输出流，向客户端发送信息
	        
	        DataInputStream read = new DataInputStream(new FileInputStream(file));
			try {
	            System.out.println("DataInputStream：");
	            // 一次读多个数据
	            byte[] tempchars = new byte[1023];
	            int charread = 0;
	            int length = 0;
	            int times = 0;
	            // 读入多个数据到数组中，charread为一次读取字符数
	            while ((charread = read.read(tempchars)) != -1) {
	
	            		method_wait();
	            		
		              if (charread == tempchars.length) {
		            	  //每次发送一整个数组
		            	  length = length + tempchars.length;
		            	  outputStream.write(tempchars);
		            	  outputStream.flush();
		              }
		              else{
		            	  //最后一次发送剩余的数据
		            	  length = length + charread;
		            	  for(int i = 0; i < charread; i++){
		            		  outputStream.write(tempchars[i]);
		            		  outputStream.flush();
		            	  }
		              }
		              times++;
		              System.out.println("发送次数： " + times + "长度： " + charread);
	            }
	            System.out.println( "文件长度length为:"+ length);
	            outputStream.flush();
	            
	            send_ok = 1;
	            /*
	            System.out.println(new Date());
	            this.sleep(3000);
	            System.out.println(new Date());
	            //文件传输完成后，发送一串特殊数据
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
	//线程挂起
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
	
	//线程唤醒
	public void method_notify(){
		synchronized (this.lock) {
		  this.lock.notify();
		}
	}

}
