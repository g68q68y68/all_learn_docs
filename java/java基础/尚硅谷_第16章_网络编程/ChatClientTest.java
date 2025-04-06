//package com.atguigu02.tcpudp.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * °¸Àý£ºÁÄÌìÊÒµÄÊµÏÖ £¨¿Í»§¶Ë£©
 *
 * @author ÉÐ¹è¹È-ËÎºì¿µ
 * @create 16:42
 */
public class ChatClientTest {
	public static void main(String[] args)throws Exception {
		//1¡¢Á¬½Ó·þÎñÆ÷
		Socket socket = new Socket("127.0.0.1",8989);
		
		//2¡¢¿ªÆôÁ½¸öÏß³Ì
		//(1)Ò»¸öÏß³Ì¸ºÔð¿´±ðÈËÁÄ£¬¼´½ÓÊÕ·þÎñÆ÷×ª·¢µÄÏûÏ¢
		Receive receive = new Receive(socket);
		receive.start();
		
		//(2)Ò»¸öÏß³Ì¸ºÔð·¢ËÍ×Ô¼ºµÄ»°
		Send send = new Send(socket);
		send.start();
		
		send.join();//µÈÎÒ·¢ËÍÏß³Ì½áÊøÁË£¬²Å½áÊøÕû¸ö³ÌÐò
		
		socket.close();
	}
}
class Send extends Thread{
	private Socket socket;
	
	public Send(Socket socket) {
		super();
		this.socket = socket;
	}

	public void run(){
		try {
			Scanner input = new Scanner(System.in);

			OutputStream outputStream = socket.getOutputStream();
			//°´ÐÐ´òÓ¡
			PrintStream ps = new PrintStream(outputStream);
			
			//´Ó¼üÅÌ²»¶ÏµÄÊäÈë×Ô¼ºµÄ»°£¬¸ø·þÎñÆ÷·¢ËÍ£¬ÓÉ·þÎñÆ÷¸øÆäËûÈË×ª·¢
			while(true){
				System.out.print("×Ô¼ºµÄ»°£º");
				String str = input.nextLine(); //×èÈûÊ½µÄ·½·¨
				if("bye".equals(str)){
					break;
				}
				ps.println(str);
			}
			
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
class Receive extends Thread{
	private Socket socket;
	
	public Receive(Socket socket) {
		super();
		this.socket = socket;
	}
	
	public void run(){
		try {
			InputStream inputStream = socket.getInputStream();
			Scanner input = new Scanner(inputStream);
			
			while(input.hasNextLine()){
				String line = input.nextLine();
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}