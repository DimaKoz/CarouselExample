package com.kozhevin.example.carousel;

public class ViewModel {

	private long	mId;
	private String	mText;
	private String	mImage;


	public ViewModel(long id, String text, String image) {
		mId = id;
		mText = text;
		mImage = image;
	}


	public long getId() {
		return mId;
	}


	public void setId(long id) {
		mId = id;
	}


	public String getText() {
		return mText;
	}


	public void setText(String text) {
		mText = text;
	}


	public String getImage() {
		return mImage;
	}


	public void setImage(String image) {
		mImage = image;
	}

}
