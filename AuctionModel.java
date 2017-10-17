import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.JFrame;

public class AuctionModel {
	public ArrayList<String> Auction_data;
	public ArrayList<String> Users_data;
	public Controller controller;
	EditElement temp_el;
	public Socket socket;
	DataInputStream s_in = null;
	DataOutputStream s_out = null;
	
	public AuctionModel(Controller controller, Socket socket, DataInputStream s_in, DataOutputStream s_out) {
		this.controller = controller;
		this.socket = socket;
		this.s_in = s_in;
		this.s_out = s_out;
		Auction_data = new ArrayList<String>();
		Users_data = new ArrayList<String>();
		init_data();
		//print_data();
		
	}
	
	private void init_data() {
		String line;
		try {
			s_out.writeUTF("Initialization");
			while((line = s_in.readUTF()) != null) {
				if (line.equals("End"))
					break;
				Auction_data.add(line);		
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File file_in = null;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			file_in = new File("Users.txt");
			fr = new FileReader(file_in);
			br = new BufferedReader(fr);
			while((line = (String)br.readLine()) != null)
				Users_data.add(line);		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{
				if(fr != null) {
					fr.close();
				}
			} catch(IOException ee) {ee.printStackTrace();}
		}
	}
	
	public void print_data() {
		System.out.println(Auction_data);
		System.out.println(Users_data);
	}
	
	public ArrayList<String> get_Auction_data() {
		return Auction_data;
	} 
	
	public void push_sign_botton() {
		controller.view.run_sign_in_dialog();
	}
	
	public void push_sign_botton_in_dialog(String name, String password) {
		String[] arr = new String[2];
		boolean autho_flag = false;
		
		for (int i = 0; i < Users_data.size(); i++) {
			arr = Users_data.get(i).split(" ");
			if (arr[0].equals(name) == true && arr[1].equals(password) == true) {
				autho_flag = true;
				break;
			}
		}
				
		if (autho_flag == true) {
			controller.user.name = name;
			controller.user.authorization_flag = true;
			controller.view.sign_succeed_messadge();
		}
		else {
			controller.view.sign_failed_messadge();
		}
	}
	
	public void registration(String name, String password) {
		String[] arr = new String[2];
		boolean registr_flag = true;
		
		for (int i = 0; i < Users_data.size(); i++) {
			arr = Users_data.get(i).split(" ");
			if (arr[0].equals(name) == true) {
				registr_flag = false;
				break;
			}
		}
		
		if (registr_flag == true) {
			FileWriter fw = null;
			try {
				fw = new FileWriter("Users.txt", true);
				fw.write(name);
				fw.write(" ");
				fw.write(password);
				fw.write("\n");
			} catch (IOException e) {e.printStackTrace();} 
			finally {
				try{
					if(fw != null) {
						fw.close();
					}
				} catch(IOException ee) {ee.printStackTrace();}
			}
			Users_data.add(name + " " + password);
			controller.view.registration_succeed_message();
		}
		else 
			controller.view.registration_failed_message();
	}
	
	public void push_done_button_in_dialog(String new_price_str) {
		temp_el.new_price_str = new_price_str;
		temp_el.new_price = Integer.parseInt(new_price_str);
		if (temp_el.new_price > temp_el.current_price) {
			send_new_info_to_server(controller.user.name);
			
		}
	}
	
	public void push_table_button(int editing_row, int current_price) {
		if (!controller.user.isAuthorizated()) {
			controller.view.sign_attention_messadge();
		}
		else {
			temp_el = new EditElement(editing_row, current_price);
			controller.view.making_offer();
		}
	}
	
	private void send_new_info_to_server(String new_name) {
		String[] arr = new String[4];
		arr = Auction_data.get(temp_el.editing_row).split(" ");
		String lot = new String(arr[0] + " " + temp_el.new_price_str + " " + new_name);
		StringBuilder sb = new StringBuilder();
		sb.append(temp_el.editing_row);
		String row = sb.toString();
		System.out.println(lot);
		System.out.println(row);
		try {
			s_out.writeUTF("Update");
			s_out.writeUTF(lot);
			s_out.writeUTF(row);
		} catch(IOException ee) {ee.printStackTrace();}
	}
	
	private void update_file(String new_price, int editing_row, String new_name) {
		System.out.println("update");
		FileWriter fw = null;
		String[] arr = new String[4];
		try {
			fw = new FileWriter("Auction.txt", false);
			for (int i = 0; i < Auction_data.size(); i++) {			
				if (i == editing_row) {
					arr = Auction_data.get(i).split(" ");
					String s = new String(arr[0] + " " + new_price + " " + new_name);
					Auction_data.set(i, s);
				}
				
				fw.write(Auction_data.get(i));
				fw.write("\n");
			}	
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{
				if(fw != null) {
					fw.close();
				}
			} catch(IOException ee) {ee.printStackTrace();}
		} 
	}
	
	public void close_connection() {
		try {
			s_out.writeUTF("Exit");
			socket.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void update_table() {
		String line;
		Auction_data = new ArrayList<String>() ;	
		System.out.println("update_table");	
		try {
			s_out.writeUTF("Update_table");
			while((line = s_in.readUTF()) != null) {
				if (line.equals("End"))
					break;
				Auction_data.add(line);		
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("update_table 2");
		System.out.println(Auction_data);		
		controller.view.update_table(Auction_data);
	}
}

class EditElement {
	public int current_price;
	public int new_price;
	public String new_price_str;
	public int editing_row;
	
	public EditElement(int editing_row, int current_price) {
		this.editing_row = editing_row;
		this.current_price = current_price;
	}
}
