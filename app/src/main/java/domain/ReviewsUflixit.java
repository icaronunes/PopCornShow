package domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class ReviewsUflixit implements Serializable{

	@SerializedName("error")
	@Expose
	private boolean error;


	@SerializedName("result")
	@Expose
	private List<MessageItem> message;

	public void setError(boolean error){
		this.error = error;
	}

	public boolean isError(){
		return error;
	}

	public void setMessage(List<MessageItem> message){
		this.message = message;
	}

	public List<MessageItem> getMessage(){
		return message;
	}
}