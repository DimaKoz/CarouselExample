package com.kozhevin.example.carousel;


//import android.support.v7.widget.RecyclerView;
import com.kozhevin.example.carousel.adapters.CarouselAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CarouselViewHolder extends RecyclerView.ViewHolder {

	public TextView										text;
	public ImageView									image;
	public View											deleteButton;
	public int											mMyPositionOffset;
	private CarouselAdapter.OnCarouselItemClickListener	mOnItemClickListener;


	public CarouselViewHolder(View itemView) {
		super(itemView);
		image = (ImageView)itemView.findViewById(R.id.image);
		text = (TextView)itemView.findViewById(R.id.text);
		
		deleteButton = image;
/*
		image.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onImageClick(getPosition() + mMyPositionOffset);
				}
			}
		});
*/
		deleteButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onDeleteButtonClick(getPosition() + mMyPositionOffset);
				}
			}
		});
	}


	public void setOnCarouselItemClickListener(CarouselAdapter.OnCarouselItemClickListener listener) {
		mOnItemClickListener = listener;
	}


	public void setPositionOffset(int offset) {
		mMyPositionOffset = offset;
	}
}