import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.JFrame;

public class AuctionClient {
	public static void main (String args[]) {
		InetAddress addr = null;
		Socket socket = null;
		try {
			addr = InetAddress.getByName(null);
			//addr = InetAddress.getByName("192.168.1.9");
			socket = new Socket(addr, 8080);
			InputStream s_in = socket.getInputStream();
            OutputStream s_out = socket.getOutputStream();
            DataInputStream in = new DataInputStream(s_in);
            DataOutputStream out = new DataOutputStream(s_out);
            Controller controller = new Controller(socket, in, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("End");
	}
}

