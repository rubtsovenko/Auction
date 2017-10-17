import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.JFrame;

public class MyView {
	private Controller controller;
	public Frame frame;
	private Label label_list;
    public JTable table;
    private JScrollPane scroll;
    private Button sign_button;
    public SignInDialog sign_in_dialog;
    public OfferDialog offer_dialog;
	
	public MyView(Controller controller) {
		this.controller = controller;
		prepareGUI();
	}
	
	private void prepareGUI() {
		frame = new Frame("Auction");
		frame.setSize(1000,800);
		frame.setLayout(null);    
		frame.setVisible(true);
		
		label_list = new Label("List of lots");
		label_list.setBounds(450,40,100,30);
		label_list.setAlignment(Label.LEFT);
		label_list.setBackground(Color.GRAY);
		label_list.setForeground(Color.WHITE);
		
		sign_button = new Button("Sign in");
		sign_button.setBounds(800,120,80,30);		
		sign_button.setBackground(Color.LIGHT_GRAY);
		
		table = new JTable();
		init_table();
		scroll = new JScrollPane(table);
		scroll.setBounds(250,80,500,400);
		
		table.getColumn("Button").setCellRenderer(new ButtonRenderer()); 
		ButtonEditor edit = new ButtonEditor(new JCheckBox(), controller);
		table.getColumn("Button").setCellEditor(edit);   
		
		frame.add(label_list);
		frame.add(scroll);
		frame.add(sign_button);
		
		sign_in_dialog = new SignInDialog(frame, controller);
		offer_dialog = new OfferDialog(frame, controller);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				controller.auction_model.close_connection();
				System.exit(0);
			}        
		}); 
		
		sign_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				controller.auction_model.push_sign_botton(); 
			}
		});		
		
	}
	
	private void init_table() {
		ArrayList<String> table_data = new ArrayList<String>(controller.auction_model.get_Auction_data());
		DefaultTableModel model = new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
				if (column == 3)
					return true;
				else
					return false;
			}
		};
		model.setDataVector(new Object[][] {}, new Object[] {"Name of lot", "Price", "Customer", "Button" });
		
		String[] arr = new String[3];
		for (int i = 0; i < table_data.size(); i++) {
			arr = table_data.get(i).split(" ");
			model.addRow(new Object[]{arr[0], arr[1], arr[2], "Choose"});
		}
		table.setModel(model);		
	}
	
	public void sign_attention_messadge() {
		JOptionPane.showMessageDialog(null,"You need to sign in");
	}
	
	public void sign_succeed_messadge() {
		JOptionPane.showMessageDialog(null,"You are signed in");
	}
	
	public void sign_failed_messadge() {
		JOptionPane.showMessageDialog(null,"Incorrect user name or password");
	}
	
	public void registration_failed_message() {
		JOptionPane.showMessageDialog(null,"Error: This loggin is already used");
	}
	
	public void registration_succeed_message() {
		JOptionPane.showMessageDialog(null,"You are registered");
	}
	
	public void run_sign_in_dialog() {
		sign_in_dialog.setVisible(true);
	}
	
	public void making_offer() {
		offer_dialog.setVisible(true);
	}
	
	public void update_table(ArrayList<String> table_data) {
		String[] arr = new String[4];
		for (int i = 0; i < table_data.size(); i++) {
			arr = table_data.get(i).split(" ");
			table.getModel().setValueAt(arr[1],i,1);
			table.getModel().setValueAt(arr[2],i,2);
		}
	}
}

class ButtonRenderer extends JButton implements TableCellRenderer {

  public ButtonRenderer() {
    setOpaque(true);
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    if (isSelected) {
      setForeground(table.getSelectionForeground());
      setBackground(table.getSelectionBackground());
    } else {
      setForeground(table.getForeground());
      setBackground(UIManager.getColor("Button.background"));
    }
    setText((value == null) ? "" : value.toString());
    return this;
  }
}

class ButtonEditor extends DefaultCellEditor {
	private Controller controller;
	protected JButton button;
	private String label;
	private boolean isPushed;

	public ButtonEditor(JCheckBox checkBox, Controller controller) {
		super(checkBox);
		this.controller = controller;
		
		
		button = new JButton();
		button.setOpaque(true);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
		});
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (isSelected) {
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		} else {
			button.setForeground(table.getForeground());
			button.setBackground(table.getBackground());
		}
		label = (value == null) ? "" : value.toString();
		button.setText(label);
		isPushed = true;
		return button;
	}

	public Object getCellEditorValue() {
		if (isPushed) {
			int editing_row = controller.view.table.getEditingRow();
			String current_price_str = (String)controller.view.table.getModel().getValueAt(editing_row,1);
			int current_price = Integer.parseInt(current_price_str);
			controller.auction_model.push_table_button(editing_row, current_price);
		}
		isPushed = false;
		return new String(label);
	}

	public boolean stopCellEditing() {
		isPushed = false;
		return super.stopCellEditing();
	}

	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}	
}

class SignInDialog extends Dialog {
    private Controller controller;
    
    public SignInDialog(final Frame frame, final Controller controller) {
        super(frame, true);
        this.controller = controller;         
		setBackground(Color.white);
		setLayout(null);
		setTitle("Sign in");
		setSize(400,200);

		Label label1 = new Label("Login", Label.CENTER);
		label1.setBounds(10,40,110,30);
		label1.setBackground(Color.LIGHT_GRAY);
		label1.setForeground(Color.WHITE);		
		
		Label label2 = new Label("Password", Label.CENTER);
		label2.setBounds(10,80,110,30);
		label2.setBackground(Color.LIGHT_GRAY);
		label2.setForeground(Color.WHITE);			
			
		final TextField tf1 = new TextField();
		tf1.setColumns(30);
		tf1.setBounds(130,40,150,30);
		final TextField tf2 = new TextField();
		tf2.setColumns(30);
		tf2.setBounds(130,80,150,30);
						
		final Button sign_Btn = new Button("Login");
		sign_Btn.setBounds(105,120,80,30);		
		sign_Btn.setBackground(Color.LIGHT_GRAY);
		
		Button reg_Btn = new Button("Register");
		reg_Btn.setBounds(205,120,80,30);		
		reg_Btn.setBackground(Color.LIGHT_GRAY);
			
		add(label1);
		add(label2);
		add(tf1);	
		add(tf2);
		add(sign_Btn);
		add(reg_Btn);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				dispose();
			}
		});
		
		sign_Btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.auction_model.push_sign_botton_in_dialog(tf1.getText(), tf2.getText());
				dispose();
			}
		});
		
			
		reg_Btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegistrDialog reg_dialog = new RegistrDialog(frame, controller);	
				reg_dialog.setVisible(true);
				dispose();
			}
		});	
	}
}

class OfferDialog extends Dialog {
    private Controller controller;
    
    public OfferDialog(final Frame frame, final Controller controller) {
        super(frame, true); 
        this.controller = controller;        
		setBackground(Color.white);
		setLayout(null);
		setTitle("Your amount");
		setSize(290,160);

		Label label1 = new Label("Enter Amount", Label.CENTER);
		label1.setBounds(10,40,110,30);
		label1.setBackground(Color.LIGHT_GRAY);
		label1.setForeground(Color.WHITE);			
			
		final TextField tf1 = new TextField();
		tf1.setColumns(30);
		tf1.setBounds(130,40,150,30);
						
		Button done_Btn = new Button("Done");
		done_Btn.setBounds(105,120,80,30);		
		done_Btn.setBackground(Color.LIGHT_GRAY);
			
		add(label1);
		add(tf1);	
		add(done_Btn);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				dispose();
			}
		});
		
		done_Btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				controller.auction_model.push_done_button_in_dialog(tf1.getText());			
				dispose();
			}
		});		
	}
}

class RegistrDialog extends Dialog {
    boolean flag = false;
    Button reg_Btn = null;
    Controller controller;
    
    public RegistrDialog(final Frame frame, final Controller controller) {
        super(frame, true);         
		this.controller = controller;
		
		setBackground(Color.white);
		setLayout(null);
		setTitle("Registration");
		setSize(400,200);
		
		Label label1 = new Label("Login", Label.CENTER);
		label1.setBounds(10,40,110,30);
		label1.setBackground(Color.LIGHT_GRAY);
		label1.setForeground(Color.WHITE);		
		
		Label label2 = new Label("Password", Label.CENTER);
		label2.setBounds(10,80,110,30);
		label2.setBackground(Color.LIGHT_GRAY);
		label2.setForeground(Color.WHITE);			
			
		final TextField tf1 = new TextField();
		tf1.setColumns(30);
		tf1.setBounds(130,40,150,30);
		final TextField tf2 = new TextField();
		tf2.setColumns(30);
		tf2.setBounds(130,80,150,30);
		
		reg_Btn = new Button("Register");
		reg_Btn.setBounds(150,160,80,30);		
		reg_Btn.setBackground(Color.LIGHT_GRAY);
			
		add(label1);
		add(label2);
		add(tf1);	
		add(tf2);
		add(reg_Btn);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				dispose();
			}
		});
		
		reg_Btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.auction_model.registration(tf1.getText(), tf2.getText());
				dispose();
			}
		});		
	}
}
