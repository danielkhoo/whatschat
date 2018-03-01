import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;


public class User implements Serializable {
	String username;
	String profilePicture;
	String description;
	//https://stackoverflow.com/questions/26571640/how-to-transfer-jpg-image-using-udp-socket

	public User(){
		this.username= "";
		profilePicture ="profilepage_icon.png";
		description="";
	}
	
	public User(String username){
		this.username= username;
		profilePicture ="profilepage_icon.png";
		description="";
	}
	
	
	public User(String profilePicture, String description) {
		super();
		this.profilePicture = profilePicture;
		this.description = description;
	}


	public User(String username, String profilePicture, String description) {
		super();
		this.username = username;
		this.profilePicture = profilePicture;
		this.description = description;
	}


	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
