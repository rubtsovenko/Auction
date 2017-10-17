import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.JFrame;

class ServeOne extends Thread {
	private Socket socket;
	private InputStream s_in = null;
	private DataInputStream in = null;
	private OutputStream s_out = null;
	private DataOutputStream out = null;
	
	private File file_in = null;
	private FileReader fr = null;
	private BufferedReader br = null;
	private FileWriter fw = null;
	
	private int size_table;
	
	public ServeOne(Socket socket) {
		this.socket = socket;
		try {
			s_in = socket.getInputStream();
			in = new DataInputStream(s_in);
			s_out = socket.getOutputStream();
			out = new DataOutputStream(s_out);
		} catch (IOException e) {e.printStackTrace();}
		
		start();
	}
	
	public void run() {
		String command;
		String lot;
		String row;
		
		while(true) {
			try {
				command = in.readUTF();
				if (command.equals("Initialization")) 
					init();
				else if (command.equals("Update")) {
					lot = in.readUTF();
					row = in.readUTF();
					update_file(lot, Integer.parseInt(row));
				}
				else if (command.equals("Update_table")) {
					updateAll();
				}
				else if (command.equals("Exit")) {
					try {
						socket.close();
						break;
					} catch (IOException e) {e.printStackTrace();}
				}
					
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	
	private void init() {
		try {
			String line;
			int i = 0;
			file_in = new File("Auction.txt");
			fr = new FileReader(file_in);
			br = new BufferedReader(fr);
			
			while((line = (String)br.readLine()) != null) {
				System.out.println(line);
				out.writeUTF(line);
				i++;
			}
			size_table = i-1;	
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(fr != null) {
					fr.close();
				}
			} catch(IOException ee) {ee.printStackTrace();}
		}
	}
	
	private void update_file(String lot, int row) {
		try {
			file_in = new File("Auction.txt");
			fr = new FileReader(file_in);
			br = new BufferedReader(fr);
			
			String[] lines = new String[size_table];
			for (int i = 0; i < size_table; i++)
				lines[i] = (String)br.readLine();
			fw = new FileWriter("Auction.txt", false);
			for (int i = 0; i < size_table; i++) {	
				if (i == row) {
					fw.write(lot);
				}
				else {
					fw.write(lines[i]);
				}		
				fw.write("\n");
			}	
			fw.write("End");
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(fr != null) {
					fr.close();
					fw.close();
				}
			} catch(IOException ee) {ee.printStackTrace();}
		}
	}
	
	private void updateAll() {
		try {
			System.out.println("UpdateAll");
			System.out.println("size of the table is:" + size_table);
			file_in = new File("Auction.txt");
			fr = new FileReader(file_in);
			br = new BufferedReader(fr);
			String[] lines = new String[size_table+1];
			for (int i = 0; i < size_table+1; i++) {
				lines[i] = (String)br.readLine();
				System.out.println(lines[i]);
				out.writeUTF(lines[i]);
			}
		} catch(IOException ee) {ee.printStackTrace();}
	}
}

public class AuctionServer {
	public static void main (String args[]) {
		ServerSocket ss = null;
		Socket socket = null;
		
		try {
			ss = new ServerSocket(8080);
			while (true) {
				System.out.println("Here");
				socket = ss.accept();
				new ServeOne(socket);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				ss.close();
				socket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}

