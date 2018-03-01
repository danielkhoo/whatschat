
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.ScrollPane;
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
import java.io.FileFilter;
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
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollPaneUI.HSBChangeListener;


public class Whatschat {
	
	//UserGroupChat Variable
	private JPanel sidePanel,whatschatGroupChat,mainChatPanel,conversationPanel,onlineUsersPanel,groupPanel,groupTitlePanel;
	private JLabel lblWhatchatLine01,lblWhatchatLine02,lblWhatchatLogo,lblGroupChat,lblProfile,lblMembers;
	private JList memberList,onlineUserlist,groupsList;
	private JTextArea taConverstaion,taComment;
	private JButton btnRemove,btnSend,btnAdd,btnViewProfile,btnCreate,btnLeave,btnEdit;
	private JTextField txtTitle,txtNewGroupName; 
	private ScrollPane converstationScroll,commentScroll;
	
	//Profile Variable
	private JLabel lblProfilePicture,lblimgSrc,lblUserId;
	private JButton btnAttachButton,btnSubmitEdit,btnCancel,btnBack;
	private JTextArea taDescription;
	private JFileChooser chooser;
	private ImageIcon ii;
	private ArrayList<User> userList;
	private User currentUser, selectedProfile;
	int currentPage = 1 ;// 1 is userprofile , 2 ischat
	boolean firstTimer;
	private ScrollPane descriptionScroll;
	
	
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
	
	private DefaultListModel<String> listModelGroups = new DefaultListModel();
	private DefaultListModel<String> listModelUsers = new DefaultListModel();
	//==============daniel==============
	
	//===============Joey==============
	private boolean userAvail;
	private String myID = "", PID;
	MulticastSocket userMulticastSocket = null;
	InetAddress userMulticastGroup = null;
	ArrayList<String> users;
	JPanel whatschatHomePanel,whatsChatProfilePanel;
	private JTextField tfUserId; 
	
	// ===============Phoebe==============
	String oldGroupName = "";
	private DefaultListModel<String> groupMember = new DefaultListModel();
	//====================================
	
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
		selectedProfile= new User();
		
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
							int x = 0;
							System.out.println("=========Latest==============");
							for(User u : userList){
								
								System.out.println(x+":"+ u.getProfilePicture());
								System.out.println(x+":"+ u.getDescription());
								System.out.println(x+":"+ u.getUsername());
								x+=1;
								System.out.println("");
								
							}
							System.out.println("=======================");
							
							System.out.println("Recieved Array: "+userList.size());
						}
						else if(recievedDataObject instanceof User){
							selectedProfile = (User)convertBytesToObject(receivedData, length);
							System.out.println("Recieved User: "+selectedProfile.getUsername());
							System.out.println("Recieved User Profile: "+selectedProfile.getProfilePicture());
							System.out.println("Recieved User statis: "+selectedProfile.getDescription());
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
		
		backgroundThread();//daniel
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
						


						TimeUnit.MILLISECONDS.sleep(1000);
						
						//Profile Side -YH
						for(User u :userList){
							System.out.println("btnRegister:"+u.getUsername()+u.getDescription());
						}
						


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
							
							firstTimer=true;
							currentUser = new User(splittedString[1]);
							frame.getContentPane().setBackground(Color.WHITE);
							sideMenu();
							groupChat();
							userProfile();
							whatschatHomePanel.setVisible(false);
							whatschatGroupChat.setVisible(false);
							whatsChatProfilePanel.setVisible(true);
							currentPage=1;
							//-------------YH----------------------
							
							userList.add(new User(tfUserId.getText().trim()));
							byte[] buf2 = convertObjectToBytes(userList);
							DatagramPacket dgpUserList = new DatagramPacket(buf2, buf2.length, userMulticastGroup, 6789);
							userMulticastSocket.send(dgpUserList);
							TimeUnit.MILLISECONDS.sleep(1000);
							
							
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
		System.out.println("Datagramhandler: "+packet[0]);
		try{  
			if (packet[0].equals("CU")) {
				if (!(myID.equals(""))) {
					//Check if myID = ID requested & check if the PID != the PID of request
					if (packet[1].equals(myID.trim()) && !(PID.equals(packet[2]))) {
						String response = "UR:True:" + packet[2].toString();
						System.out.println("Response: "+response );
						byte[] buf = response.getBytes();
						DatagramPacket dgpCheckUser = new DatagramPacket(buf, buf.length, userMulticastGroup, 6789);
						userMulticastSocket.send(dgpCheckUser);
					}else{
						// If ID request is not myID, check if ID exists in user arraylist
						if(!users.contains(packet[1])){
							//Add ID into arraylist if it does not exists
							System.out.println("Add ID if not exist");
							users.add(packet[1]);
							userList.add(new User(packet[1])); //yh
							listModelUsers.addElement(packet[1]);//daniel
						}
						//Sends a reply to let new user know of your current ID
						System.out.println("Sends a reply to let new user know of your current ID");
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
							listModelUsers.removeElement(packet[1]);//daniel
						}
						/*if(userList.get(i).getUsername().equalsIgnoreCase(packet[1])){
							userList.remove(i);
						}*/
					}
					for(int i=0;i<userList.size();i++){
						if(userList.get(i).getUsername().equalsIgnoreCase(packet[1])){
							userList.remove(i);
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
				System.out.println("Profile clicked");
				selectedPanel.setBounds(0, 170, 255, 59);
				selectedPanel.setBackground(selectedColor);
				whatschatGroupChat.setVisible(false);
				whatsChatProfilePanel.setVisible(true);
				currentPage=1;
				reloadProfile();
			}
		});
		
		lblGroupChat.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Arraylist and Network Part
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
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.setBounds(592, 272, 102, 29);
		mainChatPanel.add(btnRemove);
		btnRemove.setBackground(buttonColor);
		btnRemove.setBorder(null);
		btnRemove.setForeground(Color.WHITE);
		
		
		
		lblMembers = new JLabel("Members");
		lblMembers.setBounds(596, 8, 90, 29);
		mainChatPanel.add(lblMembers);
		lblMembers.setFont(new Font("Bookman Old Style", Font.BOLD, 18));
		

		JScrollPane scrollPaneMember = new JScrollPane();
		scrollPaneMember.setBounds(542, 42, 190, 225);
		mainChatPanel.add(scrollPaneMember);
		memberList = new JList(groupMember);
		scrollPaneMember.setViewportView(memberList);
		memberList.setBorder(border);
		
		
		//textarea and conversation
		JScrollPane scrollPaneConversation = new JScrollPane();
		scrollPaneConversation.setBounds(0, 0, 544, 310);
		taConverstaion = new JTextArea();
		scrollPaneConversation.setViewportView(taConverstaion);
		taConverstaion.setEditable(false);
		taConverstaion.setBorder(border);
		taConverstaion.setBackground(Color.WHITE);
		mainChatPanel.add(scrollPaneConversation);
		
	
		
		conversationPanel = new JPanel();
		conversationPanel.setBounds(0, 367, 732, 103);
		whatschatGroupChat.add(conversationPanel);
		conversationPanel.setBackground(Color.WHITE);
		conversationPanel.setBorder(BorderFactory.createLineBorder(headerColor));
		conversationPanel.setLayout(null);
		
		//JscrollPane And textARea
		JScrollPane scrollPaneComment = new JScrollPane();
		scrollPaneComment.setBounds(12, 13, 535, 77);
		taComment = new JTextArea();
		scrollPaneComment.setViewportView(taComment);
		conversationPanel.add(scrollPaneComment);
		
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
		
		
		listModelUsers.addElement("user1");
		listModelUsers.addElement("user2");
		listModelUsers.addElement("user3");


		
		JScrollPane scrollPaneOnlineUsers = new JScrollPane();
		scrollPaneOnlineUsers.setBounds(12, 27, 322, 130);
		onlineUsersPanel.add(scrollPaneOnlineUsers);
		onlineUserlist = new JList(listModelUsers);//daniel
		scrollPaneOnlineUsers.setViewportView(onlineUserlist);
		
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
		
		//daniel
		txtNewGroupName = new JTextField();
		txtNewGroupName.setBounds(12, 176, 97, 25);
		txtNewGroupName.setText("group1");
		groupPanel.add(txtNewGroupName);
		
		btnCreate = new JButton("Create");
		btnCreate.setBackground(buttonColor);
		btnCreate.setBorder(null);
		btnCreate.setForeground(Color.WHITE);
		btnCreate.setBounds(121, 176, 97, 25);
		groupPanel.add(btnCreate);
		
		btnLeave = new JButton("Leave");
		btnLeave.setBackground(buttonColor);
		btnLeave.setBorder(null);
		btnLeave.setForeground(Color.WHITE);
		btnLeave.setBounds(230, 176, 97, 25);
		groupPanel.add(btnLeave);
		
		
		JScrollPane scrollPaneGroups = new JScrollPane();
		scrollPaneGroups.setBounds(12, 24, 326, 139);
		groupPanel.add(scrollPaneGroups);
		groupsList = new JList(listModelGroups);
		groupsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		groupsList.addListSelectionListener(new ListSelectionListener() {
		    @Override
		    public void valueChanged(ListSelectionEvent e)
		    {
		        if(!e.getValueIsAdjusting()) {
		        		if(!listModelGroups.isEmpty() && !groupsList.isSelectionEmpty()){
		        			
		        			activeGroupName = groupsList.getSelectedValue().toString();
			        		txtTitle.setText(activeGroupName); 
			        		taConverstaion.setText(messageHashMap.get(activeGroupName));//Update textArea
		        		}
		        		
		        }
		        
		        
		    }
		}); 
		scrollPaneGroups.setViewportView(groupsList);
		
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
		
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("CREATE");
				//Check if group exists
				String groupName = txtNewGroupName.getText().trim();
				String check = "GET:"+groupName;
				sendBroadcast(check, defaultGroup, defaultSocket, 6789);
				
				//If group name is unique create the group
				if(!groupHashMap.containsKey(groupName)) {
					Random num = new Random();
					String ip = "228.0.0."+num.nextInt(256);
					String groupIP = "SET:"+groupName +":"+ip;
					sendBroadcast(groupIP, defaultGroup, defaultSocket, 6789);
					
					
					//txtJoinGroup.setText(groupName);
					
					groupHashMap.put(groupName, ip);
					
					
					//selfjoin
					joinChatGroup(groupName);
					
					ArrayList<String> localArrayList = new ArrayList<String>();
					localArrayList.add(myID);
					
					groupMember.clear();
					// get each user in the list and send them join command
					int selected[]= onlineUserlist.getSelectedIndices();
					for(int i=0; i<selected.length;i++) {
						
						String msg = "JOIN:"+listModelUsers.getElementAt(selected[i])+":"+txtNewGroupName.getText().trim();
						sendBroadcast(msg, defaultGroup, defaultSocket, 6789);
						
						//Add to arrayList
						localArrayList.add(listModelUsers.getElementAt(selected[i]));
						
						//Update members
						
						 groupMember.addElement(listModelUsers.getElementAt(selected[i]));
						
					}
					
					userHashMap.put(groupName, localArrayList);
					
					//Add self to ui memberlist
					groupMember.addElement(username);
				}
				
			}
		});
		
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("SEND");
				
				String msg = taComment.getText().toString();	
				if(!msg.equalsIgnoreCase("")) {
					taComment.setText("");
					System.out.println(msg);
					msg = activeGroupName+":"+username+ ": " + msg;
					sendBroadcast(msg, multicastGroup, multicastSocket, 6789);
				}
				
				System.out.println("refresh the list in " + username);
				
				
				if(userHashMap.get(txtTitle.getText().toString())!=null){
					ArrayList<String> localArrayList = userHashMap.get(txtTitle.getText().toString());
					groupMember.clear();
					for (String item : localArrayList) {
						System.out.println(item);
						groupMember.addElement(item);
					}
				}
				
			}
		});
		
		//Phobe and YH
		btnViewProfile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Network and go to layout
				viewProfile(onlineUserlist.getSelectedValue()+"");
				whatschatGroupChat.setVisible(false);
				whatsChatProfilePanel.setVisible(true);
				reloadProfile();
				
				
			}
		});
		
		btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // getSelectedIndices = returns array
                addMember(onlineUserlist.getSelectedValuesList());
            }
        });
 
        btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // getSelectedIndices = returns array
                deleteMember(memberList.getSelectedValuesList());
            }
        });
		
		btnLeave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// getSelectedIndices = returns array
				leaveGroup(username, activeGroupName);
			}
		});
		
		

	}
	//====================================user profile page======================================
	private void userProfile(){
		//Recive Request
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
		
		
		
		
		
		//textarea
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(253, 92, 441, 113);
		
		taDescription = new JTextArea();
		taDescription.setBorder(border);
		taDescription.setDisabledTextColor(Color.BLACK);
		taDescription.setEnabled(false);
		taDescription.setLineWrap(true);
		taDescription.setWrapStyleWord(true);
		scrollPane.setViewportView(taDescription);
		whatsChatProfilePanel.add(scrollPane);
		
		
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
		
		btnBack = new JButton("Back");
		btnBack.setBounds(334, 439, 127, 44);
		btnBack.setBackground(buttonColor);
		btnBack.setForeground(Color.WHITE);
		btnBack.setBorder(null);
		btnBack.setVisible(false);
		whatsChatProfilePanel.add(btnBack);
		
		//=================================Setting Page==============================================
		reloadProfile();
		
		//================================Button Action==============================================
		btnBack.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Button Back");
				whatsChatProfilePanel.setVisible(false);
				whatschatGroupChat.setVisible(true);
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				taDescription.setEnabled(false);
				taDescription.setBorder(null);
				btnCancel.setEnabled(false);
				btnCancel.setVisible(false);
				btnSubmitEdit.setText("Edit");
				taDescription.setText("");
				
				
			}
		});
		
		//Button action
		btnSubmitEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String buttonSelected = btnSubmitEdit.getText().toString();
				if(buttonSelected.equalsIgnoreCase("Edit")){
					taDescription.setEnabled(true);
					btnCancel.setEnabled(true);
					btnCancel.setVisible(true);
					taDescription.setBorder(border);
					btnSubmitEdit.setText("Submit");
				}
				else if(buttonSelected.equalsIgnoreCase("Submit")){
					taDescription.setEnabled(false);
					taDescription.setBorder(null);
					btnCancel.setEnabled(false);
					btnCancel.setVisible(false);	
					btnSubmitEdit.setText("Edit");
					profileSendRequest();
					
					//User Part
					currentUser.setDescription(taDescription.getText().toString());
					
				}
				
				
			}
		});
		btnAttachButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser = new JFileChooser();
			    chooser.showOpenDialog(null);
			    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			    chooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp"));
			    //chooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", ImageIO.getReaderFileSuffixes()));
			    File f = chooser.getSelectedFile();
			    String filename = f.getAbsolutePath();
			    if(filename.endsWith(".jpg") || filename.endsWith(".png") ){
				    try {
				    	String fileName=currentUser.getUsername()+".png";
				        ii=scaledImage(fileName, lblProfilePicture, ImageIO.read(new File(f.getAbsolutePath())));//get the image from file chooser and scale it to match JLabel size
				        lblProfilePicture.setIcon(ii);
				        lblimgSrc.setText(filename);
				        lblimgSrc.setForeground(Color.gray);
				        
						//User Part
				        profileSendRequest();
						currentUser.setProfilePicture(fileName);
						lblimgSrc.setText("");
						
						
				       
						
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
	
	//======================================Profile===================================
	
	private void profileSendRequest(){
		try {
			for(int u =0; u<userList.size();u++){
				if(userList.get(u).getUsername().equalsIgnoreCase(currentUser.getUsername())){
					userList.get(u).setDescription(currentUser.getDescription());
					userList.get(u).setProfilePicture(currentUser.getProfilePicture());	
					break;
				}
			}
			
			byte[] buf = convertObjectToBytes(userList);
			DatagramPacket dgpCheckUser = new DatagramPacket(buf, buf.length, userMulticastGroup, 6789);
			userMulticastSocket.send(dgpCheckUser);
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	private void viewProfile(String userName){
		try {
			
			System.out.println("Checking userList:" +userList.size());
			
			for(User u : userList){
				if(u.getUsername().equalsIgnoreCase(userName)){
					selectedProfile = u;
					System.out.println("u:__"+u.getDescription());
					System.out.println("u:__"+u.getProfilePicture());
					System.out.println("u:__"+u.getUsername());
					
					
				}
			}
			byte[] buf = convertObjectToBytes(selectedProfile);
			DatagramPacket dgpCheckUser = new DatagramPacket(buf, buf.length, userMulticastGroup, 6789);
			userMulticastSocket.send(dgpCheckUser);
			TimeUnit.MILLISECONDS.sleep(500);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	private void reloadProfile(){
		if(currentPage==1){
			//currentUser = new User();
			lblProfilePicture.setIcon(scaledImage(currentUser.getProfilePicture(),lblProfilePicture,null));
			lblUserId.setText("UserID: "+currentUser.getUsername());
			taDescription.setText(currentUser.getDescription());
			btnSubmitEdit.setVisible(true);
			btnSubmitEdit.setEnabled(true);
			btnAttachButton.setEnabled(true);
			btnAttachButton.setVisible(true);
			btnBack.setEnabled(false);
			btnBack.setVisible(false);
			

		}
		else if(currentPage==2){
			lblProfilePicture.setIcon(scaledImage(selectedProfile.getProfilePicture(),lblProfilePicture,null));
			lblUserId.setText("UserID: "+selectedProfile.getUsername());
			taDescription.setText(selectedProfile.getDescription());	
			btnSubmitEdit.setVisible(false);
			btnSubmitEdit.setEnabled(false);
			btnAttachButton.setEnabled(false);
			btnAttachButton.setVisible(false);
			btnBack.setEnabled(true);
			btnBack.setVisible(true);
			lblimgSrc.setText("");
		}
		System.out.println("CurrentPAge:"+currentPage);
	}
	//======================================Profile===================================
	
	

	
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
		
	//--------------------------------------Phoebe-------------------------------
	 
	// Edit Group Name
	public void editGroupName(String newGroupName) {
		try {
			
			// To loop through the hash map 
			for (Entry<String, ArrayList> m : userHashMap.entrySet()) {
				String key = m.getKey();
				ArrayList<String> value = m.getValue();
				
				// Check if group name is the same
				if (!oldGroupName.matches(newGroupName)) {
					// Changes made for group name
					if (!m.getKey().matches(newGroupName)) {
						userHashMap.put(newGroupName, (ArrayList<String>) m.getValue());
						userHashMap.remove(oldGroupName);

						String msg = "Group name changed to " + newGroupName;
						byte[] buf = msg.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
						multicastSocket.send(dgpSend);
						taConverstaion.append(msg + "\n");
					
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	
	
	// Add Member
    public void addMember(List list) {
    	
    		String groupName = txtTitle.getText().toString();
    		ArrayList<String> localArrayList = userHashMap.get(groupName);
    		
    		
    		for (int p = 0; p < list.size(); p++) {
    			//Send JOIN command to all selected users
    			String msg = "JOIN:"+list.get(p).toString()+":"+groupName;
				sendBroadcast(msg, defaultGroup, defaultSocket, 6789);
				
				
				
				//Add to arrayList
				localArrayList.add(list.get(p).toString());
				
				//Add to uilist
				groupMember.addElement(list.get(p).toString());
            }
			userHashMap.put(groupName, localArrayList);
			
			
			String serial = serializeArray(localArrayList.toArray(new String[localArrayList.size()]));//serialize the arraylist
			
			//Send UPDATEMEMBER command to give everyone the latest userHashMap
			for (String item : localArrayList) {
    			String msg = "UPDATEMEMBER:"+username+":"+groupName+":"+serial;
				sendBroadcast(msg, defaultGroup, defaultSocket, 6789);
			}
			
			
    		
    }
	
    // Delete Member
    public void deleteMember(List list) {
 
        try {
 
            System.out.println("DELETE");
 
            userHashMap.put("ICT2107", new ArrayList<String>());
            userHashMap.get("ICT2107").add("a");
 
            for (int p = 0; p < list.size(); p++) {
 
                userHashMap.put(activeGroupName, new ArrayList<String>());
                userHashMap.get(activeGroupName).add(list.get(p));
 
                String msg = list.get(p) + " is removed ";
                byte[] buf = msg.getBytes();
                DatagramPacket dgpConnected = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
                multicastSocket.send(dgpConnected);
                groupMember.removeElement((String) list.get(p));
 
            }
 
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

	// Leave Group
	public void leaveGroup(String userName, String group) {
		//Update the userHashMap to remove self
		ArrayList<String> localArrayList = userHashMap.get(group);
		localArrayList.remove(userName);
		userHashMap.put(group, localArrayList);
		
		String serial = serializeArray(localArrayList.toArray(new String[localArrayList.size()]));//serialize the arraylist
		
		//Send UPDATEMEMBER command to give everyone the latest userHashMap
		for (String item : localArrayList) {
			String msg = "UPDATEMEMBER:"+username+":"+group+":"+serial;
			sendBroadcast(msg, defaultGroup, defaultSocket, 6789);
		}
		
		//leave group ip
		try {
			InetAddress groupAddress = InetAddress.getByName(groupHashMap.get(group));
			String msg = username + ": is leaving";
			sendBroadcast(msg,groupAddress,multicastSocket,6789);
			multicastSocket.leaveGroup(groupAddress);}
		
		catch(IOException ex){
			
		}
		
		//Update local UI
		groupMember.clear();
		listModelGroups.removeElement(group);
		txtTitle.setText("");
		if(!listModelGroups.isEmpty()){
			groupsList.setSelectedIndex(0);
		}
	}

	
	//====================================daniel======================================
	private static void sendBroadcast(String message, InetAddress address, MulticastSocket socket,int port) {
		try {
			byte[] buffer = message.getBytes();
			DatagramPacket dgpSend = new DatagramPacket(buffer, buffer.length, address, port);
			socket.send(dgpSend);
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void joinChatGroup(String groupName) {
		
		try {
			System.out.println(username + " JOIN FUNCTION " + groupName);
			if (multicastSocket == null) {
				multicastSocket = new MulticastSocket(6789);
			}
			
			if (groupHashMap.get(groupName) == null) {
				System.out.println(username + " trying to join " + groupName);
				String check = "GET:" + groupName;
				sendBroadcast(check, defaultGroup, defaultSocket, 6789);
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			if (groupHashMap.get(groupName) == null) {
				System.out.println("Room does not exist");
			} else {
				multicastGroup = InetAddress.getByName(groupHashMap.get(groupName) );
				System.out.println("name:"+groupName);
				System.out.println(groupHashMap.get("group1"));
				System.out.println(groupHashMap.get("group2"));
				
				multicastSocket.joinGroup(multicastGroup);
				activeGroupName = groupName; //Set active group
				
				//Update GROUPS to show ACTIVE
				listModelGroups.insertElementAt(groupName, listModelGroups.getSize());
				groupsList.setSelectedIndex(listModelGroups.getSize()-1);
				
				//Send a joined message
				//String message = activeGroupName+":"+username + " joined";
				//sendBroadcast(message,multicastGroup,multicastSocket,6789);
				String msg1 = "GETMESSAGE:"+activeGroupName+":"+username + ":joined";		
				sendBroadcast(msg1, multicastGroup, multicastSocket, 6789);
				
				
				//Create a new thread to for messages within a group
				new Thread(new Runnable() {
					@Override
					public void run() {
						byte buf1[] = new byte[1000];
						DatagramPacket dgpReceived = new DatagramPacket(buf1, buf1.length, multicastGroup, 6789);
						while(true) {
							try {
								multicastSocket.receive(dgpReceived);
								byte[] receivedData = dgpReceived.getData();
								int length = dgpReceived.getLength();
								//Assuming we receive string
								String msg = new String(receivedData, 0, length);
								if (msg != null) {
									System.out.println("Message received, active group \n"+msg);
									String[] data = msg.split(":");
									//===================================== GET/SET PAST MESSAGES =====================================
									if (data[0].equals("SETMESSAGE") && messageHashMap.get(data[1]) == null){
										System.out.println(username +" receive SETMESSAGE");
										if (username.equalsIgnoreCase(data[2])) { //Is self post
											System.out.println("receive array");	
											int start =data[0].length()+data[1].length()+data[2].length()+3;
											System.out.println(msg.substring(start));	
											messageHashMap.put(data[1], msg.substring(start));
											
											if(activeGroupName.equalsIgnoreCase(data[1])) {
												taConverstaion.setText(messageHashMap.get(data[1]));
											}
										}
									}
									else if (data[0].equals("GETMESSAGE") && data.length!=1){
										System.out.println(username +" receive GETMESSAGE");
										if (!username.equalsIgnoreCase(data[2])) { //Is not self post
											System.out.println("send array");
											
											
											
											String temp = messageHashMap.get(data[1]);
											System.out.println(temp);
											String message = "SETMESSAGE:"+data[1]+":"+data[2]+":"+temp;
											sendBroadcast(message, multicastGroup, multicastSocket, 6789);
										}
									}											
									
									//===================================== NORMAL CHAT =====================================
									
									else if (messageHashMap.get(data[0])==null) {
										//Add to the message hashmap
										messageHashMap.put(data[0],msg + "\n");
									}
									else{
										String temp = messageHashMap.get(data[0]);
										messageHashMap.put(data[0],temp+msg + "\n");
									}
									if(activeGroupName.equalsIgnoreCase(data[0])) {
										taConverstaion.setText(messageHashMap.get(data[0]));
									}
								}
							} catch (IOException ex) {
								ex.printStackTrace();
							}
						}
					}
				}).start();
			}
			
		}catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void backgroundThread() {
		//Background thread for groups 
				try {
					defaultGroup = InetAddress.getByName(defaultIP);
					if (defaultSocket == null)
						defaultSocket = new MulticastSocket(6789);
					defaultSocket.joinGroup(defaultGroup);
						
					new Thread(new Runnable() {
						@Override
						public void run() {
							byte buf[] = new byte[1000];
							DatagramPacket dgpReceived = new DatagramPacket(buf, buf.length, defaultGroup, 6789);
							while(true) {
								try {
									defaultSocket.receive(dgpReceived);
									byte[] receivedData = dgpReceived.getData();
									int length = dgpReceived.getLength();
									//Assuming we receive string
									String msg = new String(receivedData, 0, length);
									System.out.println("default," +msg);
									username = myID;
									if (msg != null) {
										String[] data = msg.split(":");
										
										//===================================== GET/SET Group IP =====================================
										if (data[0].equals("SET") && groupHashMap.get(data[1]) == null) {//If data is a GROUP update
											System.out.println(username +" receive SET");
											//Add to the hashmap
											groupHashMap.put(data[1],data[2]);
										}
										else if (data[0].equals("GET") && data.length!=1) {//If data is a GET of the group list, reply with IP from hashmap
											System.out.println(username +" receive GET");
											if (groupHashMap.get(data[1]) != null) {
												System.out.println("send SET");
												String groupIP = "SET:"+data[1]+":"+groupHashMap.get(data[1]);
												sendBroadcast(groupIP, defaultGroup, defaultSocket, 6789);
											}
										}
										//=====================================NEW USERHASHMAP GET/SET ===============================
										else if (data[0].equals("UPDATEMEMBER")){
											System.out.println("UPDATEMEMBER");
											if(!data[1].equals(username)) {
												String serial = data[3];
												String[] deserialized = deserializeArray(serial);
												//Update UI
												groupMember.clear();
												for (String item : deserialized) {
												    System.out.println(item);
												    groupMember.addElement(item);
												}
											}
										}
										else if (data[0].equals("LEAVE")) {
											
										}
										else if (data[0].equals("JOIN")) {
											if(data[1].equals(username)) {
												System.out.println(username +"join "+data[2]);
												//Add delay
												try {
													Thread.sleep(500);
												} catch (InterruptedException e1) {
													e1.printStackTrace();
												}
												
												joinChatGroup(data[2]);
												
												//TEST!	
												
											}
										}
										
									}
								} catch(IOException ex) {
									ex.printStackTrace();
								}
							}
						}
					}).start();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
	}
	
	public String serializeArray(final String[] data) {
	    try (final ByteArrayOutputStream boas = new ByteArrayOutputStream();
	         final ObjectOutputStream oos = new ObjectOutputStream(boas)) {
	        oos.writeObject(data);
	        return Base64.getEncoder().encodeToString(boas.toByteArray());
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	public String[] deserializeArray(final String data) {
	    try (final ByteArrayInputStream bias = new ByteArrayInputStream(Base64.getDecoder().decode(data));
	         final ObjectInputStream ois = new ObjectInputStream(bias)) {
	        return (String[]) ois.readObject();
	    } catch (IOException | ClassNotFoundException e) {
	        throw new RuntimeException(e);
	    }
	}
	//====================================daniel======================================
}
