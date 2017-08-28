package domain;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class GuestSession{

	@SerializedName("expires_at")
	private String expiresAt;

	@SerializedName("guest_session_id")
	private String guestSessionId;

	@SerializedName("success")
	private boolean success;

	public void setExpiresAt(String expiresAt){
		this.expiresAt = expiresAt;
	}

	public String getExpiresAt(){
		return expiresAt;
	}

	public void setGuestSessionId(String guestSessionId){
		this.guestSessionId = guestSessionId;
	}

	public String getGuestSessionId(){
		return guestSessionId;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}

	@Override
 	public String toString(){
		return 
			"GuestSession{" + 
			"expires_at = '" + expiresAt + '\'' + 
			",guest_session_id = '" + guestSessionId + '\'' + 
			",success = '" + success + '\'' + 
			"}";
		}
}