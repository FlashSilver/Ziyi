package com.tianren.bean;

import android.net.Uri;

public class PhotoBean {
private String createtime;
private String picturepath;
public String getCreatetime() {
	return createtime;
}
public void setCreatetime(String createtime) {
	this.createtime = createtime;
}
public String getPicturepath() {
	return picturepath;
}
public void setPicturepath(String picturepath) {
	this.picturepath = picturepath;
}


public PhotoBean(String createtime, String picturepath) {
	super();
	this.createtime = createtime;
	this.picturepath = picturepath;
}

public PhotoBean() {
	super();
}


}
