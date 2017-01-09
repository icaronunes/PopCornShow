package domain;

import com.google.gson.annotations.Expose;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class MessageItem{

	@SerializedName("label")
	@Expose
	private String label;

	@SerializedName("attr")
	@Expose
	private String attr;

	@SerializedName("url")
	@Expose
	private String url;

	public void setLabel(String label){
		this.label = label;
	}

	public String getLabel(){
		return label;
	}

	public void setAttr(String attr){
		this.attr = attr;
	}

	public String getAttr(){
		return attr;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getUrl(){
		return url;
	}
}