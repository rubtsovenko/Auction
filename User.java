import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.JFrame;

public class User {
	public String name;
	public boolean authorization_flag;
	
	public User() {
		authorization_flag = false;
	} 
	
	public String getName() {
		return name;
	}
	
	public boolean isAuthorizated() {
		return authorization_flag;
	}
}

