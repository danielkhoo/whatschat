import java.io.Serializable;
import java.util.HashMap;


public class User implements Serializable {
	String username;
	String profilePicture;
	String description;
	HashMap<String, String> groupName;
	
	



	public User(){
		username= "";
		profilePicture ="";
		description="";
		groupName=new HashMap<String, String>();
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
	
	public HashMap<String, String> getGroupName() {
		return groupName;
	}

	public void setGroupName(HashMap<String, String> groupName) {
		this.groupName = groupName;
	}
	

}
