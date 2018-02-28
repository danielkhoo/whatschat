import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;


public class Whatschat {
	
	//UserGroupChat Variable
	private JPanel sidePanel,whatschatGroupChat,mainChatPanel,conversationPanel,onlineUsersPanel,groupPanel,groupTitlePanel;
	private JLabel lblWhatchatLine01,lblWhatchatLine02,lblWhatchatLogo,lblGroupChat,lblProfile,lblMembers;
	private JList memberList,onlineUserlist,groupsList;
	private JTextArea taConverstaion,taComment;
	private JButton btnRemove,btnSend,btnAdd,btnViewProfile,btnCreate,btnLeave,btnEdit;
	private JTextField txtTitle; 
	
	//Profile Variable
	private JLabel lblProfilePicture,lblimgSrc,lblUserId;
	private JButton btnAttachButton,btnSubmitEdit,btnCancel;
	private JTextArea txtrDescription;
	private JFileChooser chooser;
	private ImageIcon ii;
	private ArrayList<User> userList;
	private User currentUser, selectedProfile;
	int currentPage = 1 ;// 1 is userprofile , 2 ischat
	
	//===============daniel==============11
	MulticastSocket multicastSocket = null;
	InetAddress multicastGroup = null;
	
	MulticastSocket defaultSocket = null;
	InetAddress defaultGroup = null;
	String defaultIP = "228.1.1.1";
	
	volatile Map<String, String> groupHashMap = new HashMap<String,String>(); //GroupName:IP
	volatile Map<String, String> messageHashMap = new HashMap<String,String>(); // GroupName:Messages
	volatile Map<String, ArrayList> userHashMap = new HashMap<String,ArrayList>(); // GroupName:Users
	//volatile Map<String, Integer> onlineHashMap = new HashMap<String,Integer>(); // GroupName:Users
	private String username;
	private String activeGroupName;
	
	private DefaultListModel<String> listModelGroups = new DefaultListModel();;
	private DefaultListModel<String> listModelUsers = new DefaultListModel();;
	//==============daniel==============
	
	//===============Joey==============
	private boolean userAvail;
	private String myID = "", PID;
	MulticastSocket userMulticastSocket = null;
	InetAddress userMulticastGroup = null;
	ArrayList<String> users;
	JPanel whatschatHomePanel,whatsChatProfilePanel;
	private JTextField tfUserId; 
	//===============Common function==================
	
	//Common Function
	private JFrame frame;
	private Border border;
	private Color primaryColor,selectedColor,buttonColor, headerColor;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Whatschat window = new Whatschat();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Whatschat() {
		
		/*@@@@@@@@@@@@@@@@@JoeyCode@@@@@@@@@@@*/
		System.setProperty("java.net.preferIPv4Stack", "true");
		PID = ManagementFactory.getRuntimeMXBean().getName();//Track process ID for each application
		users = new ArrayList<String>();//Instantiate list of online users
		userList = new ArrayList<User>();//Instantiate list of online users
		
		//Create multicast address 
		try {
			userMulticastGroup = InetAddress.getByName("230.1.1.1");
			userMulticastSocket = new MulticastSocket(6789);
			userMulticastSocket.joinGroup(userMulticastGroup);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte buf1[] = new byte[1000];
				DatagramPacket dgpReceived = new DatagramPacket(buf1, buf1.length);
				while (true) {
					try {
						userMulticastSocket.receive(dgpReceived);
						byte[] receivedData = dgpReceived.getData();
						int length = dgpReceived.getLength();
						
						
						//Check: new String, String, arrayList<user> //YanHsia
						
						Object recievedDataObject = convertBytesToObject(receivedData,length);
						
						
						if(recievedDataObject instanceof String){
							String command = new String(receivedData, 0, length);
							String[] pkt = command.split(":");
							datagramHandler(pkt);
							System.out.println("Recieved String");
						}
						if(recievedDataObject instanceof ArrayList<?>){
							userList=(ArrayList<User>) convertBytesToObject(receivedData,length);
							System.out.println("Recieved Array: "+userList.size());
						}
						
						
					} catch (IOException ex) {
						ex.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}).start();
		
		
		initialize();
	}
	
	private void initialize() {
		primaryColor = new Color(18,22,49);
		frame = new JFrame();
		frame.setBounds(350,100,1000,750);
		frame.getContentPane().setBackground(primaryColor);
		frame.getContentPane().setLayout(null);
		border = BorderFactory.createLineBorder(Color.black);
		primaryColor = new Color(18,22,49);
		selectedColor = new Color(50,53,74);
		buttonColor = new Color(46,56,125);
		headerColor = new Color(46,56,125);
		
		//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@JoeyCode-Dialog Close@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
            	try {
					if (JOptionPane.showConfirmDialog(new JFrame(),
							"Close Application?", "", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
						System.out.println("Hello");
						String message = "UL:" + myID + ":" + myID
								+ " have left!";
						byte[] msg = message.getBytes();
						DatagramPacket dgpLeaveApp = new DatagramPacket(msg, msg.length, userMulticastGroup, 6789);
						userMulticastSocket.send(dgpLeaveApp);
						userMulticastSocket.leaveGroup(userMulticastGroup);
						System.exit(0);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
            }
        });
		frame.setVisible(true);
		//===========================registration===========================================
		registration();
	
		JButton btnRegister = new JButton("Register");
		btnRegister.setBounds(434, 421, 168, 59);
		whatschatHomePanel.add(btnRegister);
		
		//button part
		btnRegister.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@JoeyCode@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@2
				if (tfUserId.getText().equals("")) {
					JOptionPane.showMessageDialog(new JFrame(), "Username not entered", "Error",
							JOptionPane.ERROR_MESSAGE);	
					tfUserId.setText("");
				} else if (tfUserId.getText().substring(0, 1).matches("[0-9]")) {
					JOptionPane.showMessageDialog(new JFrame(), "Username cannot begin with a number", "Error",
							JOptionPane.ERROR_MESSAGE);
					tfUserId.setText("");
				} else if (tfUserId.getText().contains(" ")) {
					JOptionPane.showMessageDialog(new JFrame(), "Username cannot have spaces", "Error",
							JOptionPane.ERROR_MESSAGE);
					tfUserId.setText("");
				} else if (tfUserId.getText().length() > 8) {
					JOptionPane.showMessageDialog(new JFrame(), "Username cannot be longer than 8 characters", "Error",
							JOptionPane.ERROR_MESSAGE);
					tfUserId.setText("");
				} else {
					try {
						userAvail = true;
						String checkPkt = "CU:" + tfUserId.getText().trim() + ":" + PID;
						System.out.println("btnRegister: "+checkPkt);
						
						//RegisterationSide
						byte[] buf = checkPkt.getBytes();
						DatagramPacket dgpCheckUser = new DatagramPacket(buf, buf.length, userMulticastGroup, 6789);
						userMulticastSocket.send(dgpCheckUser);
						
						//Profile Side -YH
						byte[] buf2 = convertObjectToBytes(userList);
						DatagramPacket dgpUserList = new DatagramPacket(buf2, buf.length, userMulticastGroup, 6789);
						userMulticastSocket.send(dgpUserList);

						TimeUnit.MILLISECONDS.sleep(1000);

						if (userAvail == true) {
							btnRegister.setEnabled(false);
							myID = tfUserId.getText().toString();
//							String getUsers = "GETALLUSER:" + myID;
//							byte[] buf1 = getUsers.getBytes();
//							DatagramPacket dgpNewUser = new DatagramPacket(buf1, buf1.length, userMulticastGroup,
//									6789);
//							userMulticastSocket.send(dgpNewUser);
							
							//@@@@@@@@@@@@@@@@@@@@@@@@@@YH@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
							String[] splittedString = checkPkt.split(":");
							selectedProfile= new User();
							currentUser = new User(splittedString[1]);
							frame.getContentPane().setBackground(Color.WHITE);
							sideMenu();
							groupChat();
							userProfile();
							whatschatHomePanel.setVisible(false);
							whatschatGroupChat.setVisible(false);
							whatsChatProfilePanel.setVisible(true);
							currentPage=1;
							
							
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				
				//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Joeycode@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				
				
			}
		});
		
	
	}
	
	/*@@@@@@@@@@@@@@@@@JoeyCode@@@@@@@@@@@*/

	private void datagramHandler(String[] packet){
		//CU - Check User
		//GU - Get Users
		//UR - User Reply
		//UL - User Leave
		System.out.println(packet[0]);
		try{  
			if (packet[0].equals("CU")) {
				if (!(myID.equals(""))) {
					//Check if myID = ID requested & check if the PID != the PID of request
					if (packet[1].equals(myID.trim()) && !(PID.equals(packet[2]))) {
						String response = "UR:True:" + packet[2].toString();
						byte[] buf = response.getBytes();
						DatagramPacket dgpCheckUser = new DatagramPacket(buf, buf.length, userMulticastGroup, 6789);
						userMulticastSocket.send(dgpCheckUser);
					}else{
						// If ID request is not myID, check if ID exists in user arraylist
						if(!users.contains(packet[1])){
							//Add ID into arraylist if it does not exists
							users.add(packet[1]);
							userList.add(new User(packet[1])); //yh
							listModelUsers.addElement(packet[1]);//daniel
						}
						//Sends a reply to let new user know of your current ID
						String response = "GU:"+ myID+ ":" + packet[2].toString();
						byte[] buf = response.getBytes();
						DatagramPacket dgpGetUser = new DatagramPacket(buf, buf.length, userMulticastGroup, 6789);
						userMulticastSocket.send(dgpGetUser);
					}
				}
			}else if(packet[0].equals("UR")){
				//Let requester know that ID has been taken
				if(packet[1].equals("True") && packet[2].equals(PID)){
					//Set application to userAvail to false if PID tallies 
					userAvail = false;
					JOptionPane.showMessageDialog(new JFrame(), "Username exists in the application", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}else if(packet[0].equals("UL")){
				//Lets application know that a user has left the application and removes it 
				//off the user arraylist
				if(users.contains(packet[1])){
					for(int i=0;i<users.size();i++){
						if(users.get(i).equals(packet[1])){
							users.remove(i);
							userList.remove(new User(packet[1]));
							listModelUsers.removeElement(packet[1]);//daniel
						}
					}
				}
			}else if(packet[0].equals("GU")){
				if(PID.equals(packet[2])){
					//Adds userID into arraylist if it does not exist in arraylist
					if(!(users.contains(packet[1]))){
						users.add(packet[1]);
						userList.add(new User(packet[1]));//yh
						listModelUsers.addElement(packet[1]);//daniel
					}
				}
				//Shows the results of user arraylist
				//JOptionPane.showMessageDialog(new JFrame(),users.toString(), "Error",
						//JOptionPane.ERROR_MESSAGE);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * Initialize the contents of the frame.
	 */
	
	//========================================Registration=====================================================
	
	private void registration(){
		
		whatschatHomePanel = new JPanel();
		whatschatHomePanel.setBackground(primaryColor);
		whatschatHomePanel.setBounds(0, 0, 982, 703);
		frame.getContentPane().add(whatschatHomePanel);
		whatschatHomePanel.setLayout(null);
		//=============================================userLogin=====================================
		JLabel lblLine01 = new JLabel("________");
		lblLine01.setBounds(0, 0, 836, 106);
		whatschatHomePanel.add(lblLine01);
		lblLine01.setForeground(Color.WHITE);
		lblLine01.setFont(new Font("Tahoma", Font.PLAIN, 99));
		
		JLabel lblLogo = new JLabel("WhatsChat");
		lblLogo.setBounds(206, 125, 664, 225);
		whatschatHomePanel.add(lblLogo);
		lblLogo.setForeground(Color.WHITE);
		lblLogo.setFont(new Font("Bookman Old Style", Font.BOLD, 99));
		
		tfUserId = new JTextField();
		tfUserId.setBounds(238, 300, 558, 91);
		whatschatHomePanel.add(tfUserId);
		tfUserId.setHorizontalAlignment(SwingConstants.CENTER);
		tfUserId.setFont(new Font("Bookman Old Style", Font.PLAIN, 24));
		tfUserId.setColumns(10);
		
		
		
		JLabel label = new JLabel("________");
		label.setBounds(544, 455, 438, 106);
		whatschatHomePanel.add(label);
		label.setForeground(Color.WHITE);
		label.setFont(new Font("Tahoma", Font.PLAIN, 99));
		

	}
	
	//===================================Side Menu==============================================================
	private void sideMenu(){
		//----------------------------------------Design Logo Part---------------------------------------------------
		JPanel sidePanel = new JPanel();
		sidePanel.setBackground(primaryColor);
		sidePanel.setBounds(0, 0, 250, 853);
		frame.getContentPane().add(sidePanel);
		sidePanel.setLayout(null);
		
		
		JLabel lblWhatchatLine01 = new JLabel("_______");
		lblWhatchatLine01.setBounds(0, -14, 290, 43);
		sidePanel.add(lblWhatchatLine01);
		lblWhatchatLine01.setFont(new Font("Bookman Old Style", Font.BOLD, 36));
		lblWhatchatLine01.setForeground(Color.WHITE);
		
		JLabel lblWhatchatLine02 = new JLabel("_______");
		lblWhatchatLine02.setForeground(Color.WHITE);
		lblWhatchatLine02.setFont(new Font("Bookman Old Style", Font.BOLD, 36));
		lblWhatchatLine02.setBounds(121, 80, 131, 43);
		sidePanel.add(lblWhatchatLine02);
		
		JLabel lblWhatchatLogo = new JLabel("WhatsChat");
		lblWhatchatLogo.setBounds(0, 26, 315, 94);
		sidePanel.add(lblWhatchatLogo);
		lblWhatchatLogo.setFont(new Font("Bookman Old Style", Font.BOLD | Font.ITALIC, 36));
		lblWhatchatLogo.setForeground(Color.WHITE);
		
		
		//----------------------------------------Design Group Chat Part---------------------------------------------------
		JLabel lblGroupChat = new JLabel(new ImageIcon(".\\images\\groupchat_icon.png"));

		lblGroupChat.setBounds(0, 240, 250, 59);
		sidePanel.add(lblGroupChat);
		
		JLabel lblProfile = new JLabel(new ImageIcon(".\\images\\profile_icon.png"));

		lblProfile.setBounds(0, 170, 252, 59);
		sidePanel.add(lblProfile);
		
		JPanel selectedPanel = new JPanel();
		selectedPanel.setBounds(0, 170, 255, 59);
		selectedPanel.setBackground(selectedColor);
		sidePanel.add(selectedPanel);
		
		lblProfile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
//				userProfile();
				System.out.println("Profile clicked");
				selectedPanel.setBounds(0, 170, 255, 59);
				selectedPanel.setBackground(selectedColor);
				whatschatGroupChat.setVisible(false);
				whatsChatProfilePanel.setVisible(true);
				currentPage=2;
			}
		});
		
		lblGroupChat.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
//				userProfile();
//				groupChat();
				
				//ArrayList Part
				for(User u: userList){
					if(u.getUsername().equalsIgnoreCase(currentUser.getUsername())){
						u.setDescription(currentUser.getDescription());
						u.setProfilePicture(currentUser.getProfilePicture());		
					}
				}
				
				
				//Network Part
				profileSendRequest();

				//Interface part
				System.out.println("Groupchat clicked");
				whatsChatProfilePanel.setVisible(false);
				whatschatGroupChat.setVisible(true);
				
				currentPage=2;
				selectedPanel.setBounds(0, 240, 255, 59);
			}
		});
		
		
	}
	
	private void profileSendRequest(){
		try {
			byte[] buf = convertObjectToBytes(userList);
			System.out.println("Checking userList:" +userList.size());
			DatagramPacket dgpCheckUser = new DatagramPacket(buf, buf.length, userMulticastGroup, 6789);
			userMulticastSocket.send(dgpCheckUser);
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	//====================================Groupchat page=====================================
	private void groupChat(){
		
		whatschatGroupChat = new JPanel();
		whatschatGroupChat.setBackground(Color.WHITE);
		whatschatGroupChat.setBounds(250, 0, 732, 704);
		frame.getContentPane().add(whatschatGroupChat);
		whatschatGroupChat.setLayout(null);
		
		mainChatPanel = new JPanel();
		mainChatPanel.setBounds(0, 59, 732, 307);
		whatschatGroupChat.add(mainChatPanel);
		mainChatPanel.setLayout(null);
		
		btnRemove = new JButton("Remove");
		btnRemove.setBounds(596, 265, 102, 29);
		mainChatPanel.add(btnRemove);
		btnRemove.setBackground(buttonColor);
		btnRemove.setBorder(null);
		btnRemove.setForeground(Color.WHITE);
		
		lblMembers = new JLabel("Members");
		lblMembers.setBounds(596, 8, 90, 29);
		mainChatPanel.add(lblMembers);
		lblMembers.setFont(new Font("Bookman Old Style", Font.BOLD, 18));
		
		memberList = new JList();
		memberList.setBounds(543, 42, 189, 265);
		mainChatPanel.add(memberList);
		memberList.setBorder(border);
		taConverstaion = new JTextArea();
		taConverstaion.setBounds(0, 0, 544, 307);
		mainChatPanel.add(taConverstaion);
		taConverstaion.setEditable(false);
		taConverstaion.setBorder(border);
		taConverstaion.setBackground(Color.WHITE);
		//taConverstaion.setBackground(Color.GREEN);
		

		
		conversationPanel = new JPanel();
		conversationPanel.setBounds(0, 367, 732, 103);
		whatschatGroupChat.add(conversationPanel);
		conversationPanel.setBackground(Color.WHITE);
		conversationPanel.setBorder(BorderFactory.createLineBorder(headerColor));
		conversationPanel.setLayout(null);
		
		taComment = new JTextArea();
		//taComment.setBackground(Color.BLACK);
		taComment.setText("Comment");
		taComment.setBounds(12, 13, 535, 77);
		conversationPanel.add(taComment);
		
		btnSend = new JButton("Send");
		btnSend.setBounds(597, 39, 97, 41);
		conversationPanel.add(btnSend);
		btnSend.setBackground(buttonColor);
		btnSend.setBorder(null);
		btnSend.setForeground(Color.WHITE);
		
		
		
		onlineUsersPanel = new JPanel();
		onlineUsersPanel.setBounds(372, 483, 346, 208);
		whatschatGroupChat.add(onlineUsersPanel);
		onlineUsersPanel.setBackground(Color.WHITE);
		onlineUsersPanel.setBorder(BorderFactory.createTitledBorder("Online Users"));
		onlineUsersPanel.setLayout(null);
		
		
		
		onlineUserlist = new JList(listModelUsers);//daniel
		onlineUserlist.setBounds(12, 20, 322, 116);//daniel
		onlineUsersPanel.add(onlineUserlist);
		
		btnAdd = new JButton("Add");
		btnAdd.setBackground(buttonColor);
		btnAdd.setBorder(null);
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setBounds(12, 170, 97, 25);
		onlineUsersPanel.add(btnAdd);
		
		btnViewProfile = new JButton("View Profile");
		btnViewProfile.setBackground(buttonColor);
		btnViewProfile.setBorder(null);
		btnViewProfile.setForeground(Color.WHITE);
		btnViewProfile.setBounds(121, 170, 107, 25);
		onlineUsersPanel.add(btnViewProfile);
		
		groupPanel = new JPanel();
		groupPanel.setBounds(10, 483, 350, 208);
		whatschatGroupChat.add(groupPanel);
		groupPanel.setLayout(null);
		groupPanel.setBorder(BorderFactory.createTitledBorder("Groups"));
		groupPanel.setBackground(Color.WHITE);
		
		btnCreate = new JButton("Create");
		btnCreate.setBackground(buttonColor);
		btnCreate.setBorder(null);
		btnCreate.setForeground(Color.WHITE);
		btnCreate.setBounds(12, 176, 97, 25);
		groupPanel.add(btnCreate);
		
		btnLeave = new JButton("Leave");
		btnLeave.setBackground(buttonColor);
		btnLeave.setBorder(null);
		btnLeave.setForeground(Color.WHITE);
		btnLeave.setBounds(121, 176, 97, 25);
		groupPanel.add(btnLeave);
		
		groupsList = new JList();
		groupsList.setBounds(12, 24, 326, 139);
		//groupsList.setBackground(Color.BLUE); //To check if display
		groupPanel.add(groupsList);
		
		groupTitlePanel = new JPanel();
		groupTitlePanel.setBounds(0, 0, 732, 59);
		whatschatGroupChat.add(groupTitlePanel);
		groupTitlePanel.setBackground(headerColor);
		groupTitlePanel.setLayout(null);
		
		txtTitle = new JTextField();
		txtTitle.setHorizontalAlignment(JTextField.CENTER);
		txtTitle.setBorder(null);
		txtTitle.setOpaque(false);
		txtTitle.setEnabled(false);
		txtTitle.setForeground(Color.WHITE);
		txtTitle.setFont(new Font("Bookman Old Style", Font.PLAIN, 24));
		txtTitle.setText("ICT2107");
		txtTitle.setBounds(97, 10, 342, 36);
		groupTitlePanel.add(txtTitle);
		txtTitle.setColumns(10);
		
		btnEdit = new JButton("Edit");
		btnEdit.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
		btnEdit.setBackground(buttonColor);
		btnEdit.setBorder(null);
		btnEdit.setForeground(Color.WHITE);
		btnEdit.setBounds(598, 10, 97, 40);
		groupTitlePanel.add(btnEdit);
		
		btnEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(btnEdit.getText().toString().equalsIgnoreCase("Edit")){
					txtTitle.setOpaque(true);
					txtTitle.setEnabled(true);
					txtTitle.setBorder(border);
					txtTitle.setForeground(Color.BLACK);
					txtTitle.setBackground(Color.WHITE);
					btnEdit.setText("Done");
					
				}
				else if(btnEdit.getText().toString().equalsIgnoreCase("Done")){
					txtTitle.setOpaque(false);
					txtTitle.setEnabled(false);
					txtTitle.setBorder(null);
					txtTitle.setForeground(Color.WHITE);
					btnEdit.setText("Edit");
				}
				
			}
		});
		
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("PLEASE");
				
				System.out.println(listModelUsers.getSize());
			}
		});

	}
	
	
	//====================================user profile page======================================
	//====================================user profile page======================================
	private void userProfile(){
		//Recive Request
		//profileRecieveRequest();
		//===============================Interface=================================================
		whatsChatProfilePanel = new JPanel();
		whatsChatProfilePanel.setBackground(Color.WHITE);
		whatsChatProfilePanel.setBounds(249, 0, 733, 750);
		frame.getContentPane().add(whatsChatProfilePanel);
		whatsChatProfilePanel.setLayout(null);
		
		lblProfilePicture = new JLabel("");
		lblProfilePicture.setBounds(54, 43, 165, 161);
		whatsChatProfilePanel.add(lblProfilePicture);
		
		lblimgSrc = new JLabel("");
		lblimgSrc.setBounds(12, 246, 399, 16);
		whatsChatProfilePanel.add(lblimgSrc);
		lblimgSrc.setForeground(Color.LIGHT_GRAY);
			
		btnAttachButton = new JButton("Attach");
		btnAttachButton.setBounds(91, 214, 97, 25);
		whatsChatProfilePanel.add(btnAttachButton);
		btnAttachButton.setBackground(buttonColor);
		btnAttachButton.setBorder(null);
		btnAttachButton.setForeground(Color.WHITE);
		lblUserId = new JLabel("");
		lblUserId.setBounds(254, 45, 325, 44);
		whatsChatProfilePanel.add(lblUserId);
		lblUserId.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
		
		txtrDescription = new JTextArea();
		txtrDescription.setBounds(253, 92, 441, 113);
		whatsChatProfilePanel.add(txtrDescription);
		txtrDescription.setBorder(border);
		txtrDescription.setDisabledTextColor(Color.BLACK);
		txtrDescription.setEnabled(false);
		
		btnSubmitEdit = new JButton("Edit");
		btnSubmitEdit.setBounds(594, 212, 97, 25);
		whatsChatProfilePanel.add(btnSubmitEdit);
		btnSubmitEdit.setBackground(buttonColor);
		btnSubmitEdit.setBorder(null);
		btnSubmitEdit.setForeground(Color.WHITE);
		btnSubmitEdit.setBackground(buttonColor);
		btnSubmitEdit.setBorder(null);
		btnSubmitEdit.setForeground(Color.WHITE);
		
		btnCancel = new JButton("Cancel");
		btnCancel.setBackground(buttonColor);
		btnCancel.setBorder(null);
		btnCancel.setForeground(Color.WHITE);
		
		btnCancel.setBounds(480, 211, 97, 25);
		whatsChatProfilePanel.add(btnCancel);
		btnCancel.setEnabled(false);
		btnCancel.setVisible(false);
		
		//=================================Network part==============================================
		
		//=================================Setting Page==============================================
		
		if(currentPage==1){
			//currentUser = new User();
			lblProfilePicture.setIcon(scaledImage(currentUser.getProfilePicture(),lblProfilePicture,null));
			lblUserId.setText("UserID: "+currentUser.getUsername());
			txtrDescription.setText(currentUser.getDescription());
			btnSubmitEdit.setVisible(true);
			btnSubmitEdit.setEnabled(true);
			btnAttachButton.setEnabled(true);
			btnAttachButton.setVisible(true);

		}
		else if(currentPage==2){
			lblProfilePicture.setIcon(scaledImage(selectedProfile.getProfilePicture(),lblProfilePicture,null));
			lblUserId.setText("UserID: "+selectedProfile.getUsername());
			txtrDescription.setText(selectedProfile.getDescription());	
			btnSubmitEdit.setVisible(false);
			btnSubmitEdit.setEnabled(false);
			btnAttachButton.setEnabled(false);
			btnAttachButton.setVisible(false);
		}
		
		//================================Button Action==============================================
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtrDescription.setEnabled(false);
				txtrDescription.setBorder(null);
				btnCancel.setEnabled(false);
				btnCancel.setVisible(false);
				btnSubmitEdit.setText("Edit");
				txtrDescription.setText("");
				
				
			}
		});
		
		//Button action
		btnSubmitEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String buttonSelected = btnSubmitEdit.getText().toString();
				if(buttonSelected.equalsIgnoreCase("Edit")){
					txtrDescription.setEnabled(true);
					btnCancel.setEnabled(true);
					btnCancel.setVisible(true);
					txtrDescription.setBorder(border);
					btnSubmitEdit.setText("Submit");
				}
				else if(buttonSelected.equalsIgnoreCase("Submit")){
					txtrDescription.setEnabled(false);
					txtrDescription.setBorder(null);
					btnCancel.setEnabled(false);
					btnCancel.setVisible(false);	
					btnSubmitEdit.setText("Edit");
					
					//User Part
					currentUser.setDescription(txtrDescription.getText().toString());
					
				}
				
				
			}
		});
		btnAttachButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser = new JFileChooser();
			    chooser.showOpenDialog(null);
			    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			    //chooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", ImageIO.getReaderFileSuffixes()));
			    File f = chooser.getSelectedFile();
			    String filename = f.getAbsolutePath();
			    if(filename.endsWith(".jpg") || filename.endsWith(".png")){
				    try {
				    	String fileName=currentUser.getUsername()+".png";
				        ii=scaledImage(fileName, lblProfilePicture, ImageIO.read(new File(f.getAbsolutePath())));//get the image from file chooser and scale it to match JLabel size
				        lblProfilePicture.setIcon(ii);
				        lblimgSrc.setText(filename);
				        lblimgSrc.setForeground(Color.gray);
				        
						//User Part
						currentUser.setProfilePicture(fileName);
						
				       
						
				    } catch (Exception ex) {
				    	lblimgSrc.setText("Incorrect File Source. Please Upload again.");
				    	lblimgSrc.setForeground(Color.RED);
				        ex.printStackTrace();
				    }
			    }
			    else{
			    	lblimgSrc.setText("Incorrect File Source. Please Upload again.");
			    	lblimgSrc.setForeground(Color.RED);
			    }

			 
			    

			}
		});
		
	}
	
	
	
	

	
	//=============================================Additional Icon============================================
	//---------------------------------------------Image Icon---------------------------------------------
	private ImageIcon scaledImage(String imgName, JLabel label, BufferedImage img){
		ImageIcon imageicon =null;
		if(img ==null){
			    try {
					img = ImageIO.read(new File(".\\images\\"+imgName));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		else{
			File file = new File(".\\images\\"+imgName);
			try {
				ImageIO.write(img, "png", file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		Image dimg = img.getScaledInstance(label.getWidth(), label.getHeight(),Image.SCALE_SMOOTH);
	    imageicon = new ImageIcon(dimg);

		
		
		return imageicon;
	}
	
	//---------------------------------------------Object Conversion------------------------------------
	 private static byte[] convertObjectToBytes(Object object) throws IOException {
		    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
		         ObjectOutput out = new ObjectOutputStream(bos)) {
		        out.writeObject(object);
		        return bos.toByteArray();
		    } 
		}
	 
	 private static Object convertBytesToObject(byte[] bytes, int size) throws IOException, ClassNotFoundException {
		    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		         ObjectInput in = new ObjectInputStream(bis)) {
		    	return in.readObject();
		    }catch(Exception e){
		    	String a = new String(bytes, 0, size);
		    	return a;
		    }
			
		}
}
