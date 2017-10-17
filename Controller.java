import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.JFrame;

public class Controller {    
	public MyView view;
	public AuctionModel auction_model;
	public User user;
	
	public Controller(Socket socket, DataInputStream s_in, DataOutputStream s_out) {   
		user = new User();
		auction_model = new AuctionModel(this, socket, s_in, s_out);
		view = new MyView(this);
		while (true) {
			auction_model.update_table();
			try {
				Thread.sleep (3000); 
			} catch (InterruptedException e){}
		}
    }    
}
