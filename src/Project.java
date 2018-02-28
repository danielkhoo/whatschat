import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class Project extends JFrame {

	MulticastSocket multicastSocket = null;
	InetAddress multicastGroup = null;
	
	MulticastSocket defaultSocket = null;
	InetAddress defaultGroup = null;
	String defaultIP = "228.1.1.1";
	
	volatile Map<String, String> groupHashMap = new HashMap<String,String>(); //GroupName:IP
	volatile Map<String, String> messageHashMap = new HashMap<String,String>(); // GroupName:Messages
	volatile Map<String, ArrayList> userHashMap = new HashMap<String,ArrayList>(); // GroupName:Users
	volatile Map<String, Integer> onlineHashMap = new HashMap<String,Integer>(); // GroupName:Users
	private String username;
	private String activeGroupName;
	
	private JPanel contentPane;
	private JTextField textField;
	private JTextField txtJoinGroup;
	private JTextField txtCreateGroup;
	private JTextField txtUserName;

	private JList listGroups;
	private DefaultListModel<String> listModelGroups;
	
	private JList listUsers;
	private DefaultListModel<String> listModelUsers;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Project frame = new Project();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Project() {
		//For OSX, stick to IPv4
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 395);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblProcessID = new JLabel("ProcessID:");
		lblProcessID.setBounds(6, 0, 73, 16);
		contentPane.add(lblProcessID);
		
		JLabel lblUserName = new JLabel("User Name");
		lblUserName.setBounds(6, 21, 82, 16);
		contentPane.add(lblUserName);
		
		
		JLabel lblCreateGroup = new JLabel("Create Group");
		lblCreateGroup.setBounds(6, 85, 82, 16);
		contentPane.add(lblCreateGroup);
		
		JLabel lblJoinGroup = new JLabel("Join Group");
		lblJoinGroup.setBounds(6, 113, 73, 16);
		contentPane.add(lblJoinGroup);
		
		JLabel lblMessage = new JLabel("Message:");
		lblMessage.setBounds(6, 335, 61, 16);
		contentPane.add(lblMessage);
		
		JLabel lblProcessIdValue = new JLabel();
		lblProcessIdValue.setBounds(91, 0, 252, 16);
		lblProcessIdValue.setText(ManagementFactory.getRuntimeMXBean().getName());
		contentPane.add(lblProcessIdValue);
		
		JButton btnSend = new JButton("Send");
		btnSend.setEnabled(false);
		btnSend.setBounds(343, 330, 101, 29);
		
		JButton btnLeave = new JButton("Leave");
		btnLeave.setEnabled(false);
		btnLeave.setBounds(349, 108, 101, 29);
		
		txtUserName = new JTextField();
		txtUserName.setText("");
		txtUserName.setColumns(10);
		txtUserName.setBounds(79, 16, 154, 26);
		contentPane.add(txtUserName);
		
		txtCreateGroup = new JTextField();
		txtCreateGroup.setColumns(10);
		txtCreateGroup.setBounds(110, 80, 154, 26);
		contentPane.add(txtCreateGroup);
		
		txtJoinGroup = new JTextField();
		//txtGroupIp.setText("228.5.6.7");
		txtJoinGroup.setBounds(110, 108, 154, 26);
		contentPane.add(txtJoinGroup);
		txtJoinGroup.setColumns(10);
		
		//Chat View
		JTextArea textArea = new JTextArea();
		JScrollPane scrollPaneChat = new JScrollPane(textArea);
		scrollPaneChat.setBounds(156, 149, 438, 160);
		contentPane.add(scrollPaneChat);
		
		//NewMessage
		textField = new JTextField();
		textField.setBounds(79, 330, 252, 26);
		contentPane.add(textField);
		textField.setColumns(10);
		
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.setBounds(234, 16, 101, 29);
		
		
		JButton btnCreate = new JButton("Create");
		btnCreate.setBounds(257, 80, 101, 29);
		
		
		JButton btnJoin = new JButton("Join");
		btnJoin.setBounds(257, 108, 101, 29);
		
		
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
							//System.out.println(username + ":" +msg);
							if (msg != null) {
								String[] data = msg.split(":");
								
								//===================================== GET/SET Group IP =====================================
								if (data[0].equals("SET") && groupHashMap.get(data[1]) == null) {//If data is a GROUP update
									//System.out.println(username +" receive SET");
									//Add to the hashmap
									groupHashMap.put(data[1],data[2]);
									//System.out.println(username + ": 1Add new group:"+data[1]+"@"+data[2]);
								}
								else if (data[0].equals("GET") && data.length!=1) {//If data is a GET of the group list, reply with IP from hashmap
									//System.out.println(username +" receive GET");
									if (groupHashMap.get(data[1]) != null) {
										String groupIP = "SET:"+data[1]+":"+groupHashMap.get(data[1]);
										sendBroadcast(groupIP, defaultGroup, defaultSocket, 6789);
									}
								}
								
								//===================================== GET/SET Log-On =====================================
								else if(data[0].equals("SETLOGON")) {
									//System.out.println(username +" receive SETLOGON");
									//Receive array of users
									String[] array = deserializeArray(data[1]);
									listModelUsers.clear();
									for (String item : array) {
										System.out.println(item);
										onlineHashMap.put(item,1);
										listModelUsers.addElement(item);
									}
								}
								else if (data[0].equals("GETLOGON") && data.length!=1){
									//System.out.println(username +" receive GETLOGON");
									if (onlineHashMap.get(data[1]) == null) { //Not a self request
										onlineHashMap.put(data[1],1); //Put new item into onlineHashMap
										//Serial array and send over broadcast to all users
										Set<String> keys = onlineHashMap.keySet();
										String[] array = keys.toArray(new String[keys.size()]);
										System.out.println(keys.toString());
										
										String message = "SETLOGON:"+serializeArray(array);
										sendBroadcast(message, defaultGroup, defaultSocket, 6789);
									}
									else {
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
		
		
		
		
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(txtUserName.getText().equalsIgnoreCase("")) {
					username = ManagementFactory.getRuntimeMXBean().getName();
				}
				else {
					username = txtUserName.getText();
				}
				txtUserName.setText(username);
				
				onlineHashMap.put(username,1);
				String check = "GETLOGON:"+txtUserName.getText();
				sendBroadcast(check, defaultGroup, defaultSocket, 6789);
				
				
			}
		});
		contentPane.add(btnUpdate);
		
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {


					//Check if group exists
					String groupName = txtCreateGroup.getText();
					String check = "GET:"+groupName;
					sendBroadcast(check, defaultGroup, defaultSocket, 6789);
					
					//If group name is unique create the group
					if(!groupHashMap.containsKey(groupName)) {
						Random num = new Random();
						String ip = "228.0.0."+num.nextInt(256);
						String groupIP = "SET:"+groupName +":"+ip;
						sendBroadcast(groupIP, defaultGroup, defaultSocket, 6789);
						
						int selected[]= listUsers.getSelectedIndices();
						for(int i=0; i<selected.length;i++) {
							System.out.println(listModelUsers.getElementAt(selected[i]));
							
						}
						
						txtJoinGroup.setText(groupName);
						groupHashMap.put(groupName, ip);
						
						btnJoin.doClick();
						
					}
				
				
			}
		});
		contentPane.add(btnCreate);
		
		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (multicastSocket == null) {
						multicastSocket = new MulticastSocket(6789);
					}
					final String groupName = txtJoinGroup.getText();
					
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
					btnUpdate.doClick();
					
					if (groupHashMap.get(groupName) == null) {
						System.out.println("Room does not exist bro!");
						textArea.append("Room does not exist bro!\n");
					} else {
						System.out.println(groupHashMap.get(groupName));
						multicastGroup = InetAddress.getByName(groupHashMap.get(groupName) );
						multicastSocket.joinGroup(multicastGroup);
						activeGroupName = groupName; //Set active group
						
						//Update list
						listModelGroups.insertElementAt(groupName, listModelGroups.getSize());
						listGroups.setSelectedIndex(listModelGroups.getSize()-1);
						
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
														textArea.setText(messageHashMap.get(data[1]));
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
												textArea.setText(messageHashMap.get(data[0]));
											}
										}
									} catch (IOException ex) {
										ex.printStackTrace();
									}
								}
							}
						}).start();
						//Disable this button
						//btnJoin.setEnabled(false);
						//btnUpdate.setEnabled(false);
						//btnCreate.setEnabled(false);
						//Enable the button leave and to send message
						btnLeave.setEnabled(true);
						btnSend.setEnabled(true);
					}


					
					
				}catch (IOException ex) {
					ex.printStackTrace();
				}
				
			}
		});
		contentPane.add(btnJoin);
		
		
		btnLeave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String msg = username + ": is leaving";
					sendBroadcast(msg,multicastGroup,multicastSocket,6789);
					multicastSocket.leaveGroup(multicastGroup);
					btnJoin.setEnabled(true);
					btnSend.setEnabled(false);
					btnLeave.setEnabled(false);
				}catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		contentPane.add(btnLeave);
		
		
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
						
					
						String msg = textField.getText();					
						msg = activeGroupName+":"+username+ ": " + msg;
						sendBroadcast(msg, multicastGroup, multicastSocket, 6789);
			
			}
		});
		contentPane.add(btnSend);
		
		listModelGroups = new DefaultListModel();

		listGroups = new JList(listModelGroups);
		listGroups.setBounds(6, 151, 138, 158);
		listGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listGroups.addListSelectionListener(new ListSelectionListener() {
		    @Override
		    public void valueChanged(ListSelectionEvent e)
		    {
		        if(!e.getValueIsAdjusting()) {
		        		activeGroupName = listGroups.getSelectedValue().toString();
		        		txtJoinGroup.setText(activeGroupName); 
		        		textArea.setText(messageHashMap.get(activeGroupName));//Update textArea
		        }
		        
		        
		    }
		}); 
		contentPane.add(listGroups);
		
		listModelUsers = new DefaultListModel();
		listModelUsers.addElement("user1");
		listModelUsers.addElement("user1");
		listUsers = new JList(listModelUsers);
		listUsers.setBounds(456, 6, 138, 131);
		listUsers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listUsers.addListSelectionListener(new ListSelectionListener() {
		    @Override
		    public void valueChanged(ListSelectionEvent e)
		    {
		        if(!e.getValueIsAdjusting()) {
		        }
		    }
		}); 
		contentPane.add(listUsers);
		
		
	}
	
	private static void sendBroadcast(String message, InetAddress address, MulticastSocket socket,int port) {
		try {
			byte[] buffer = message.getBytes();
			DatagramPacket dgpSend = new DatagramPacket(buffer, buffer.length, address, port);
			socket.send(dgpSend);
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private static void displayCreateWindow() {
        
		JPanel panel = new JPanel(new GridLayout(0, 1));
        /*
        DefaultListModel listModelOnlineUsers = new DefaultListModel();
        for (String item : userList) {
        		listModelOnlineUsers.addElement(item);
        }
        JList list = new JList(listModelOnlineUsers);;
        */
        JTextField txtName = new JTextField();
        panel.add(new JLabel("New Group Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Add Members:"));
        //panel.add(list);
        int result = JOptionPane.showConfirmDialog(null, panel, "Test",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            //System.out.println(list.getSelectedValuesList().toString() + " " + txtName.getText());
        } else {
            System.out.println("Cancelled");
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
	
}
