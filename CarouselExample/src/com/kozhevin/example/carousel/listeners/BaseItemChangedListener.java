package com.kozhevin.example.carousel.listeners;

import android.support.v7.widget.RecyclerView.ViewHolder;

import com.kozhevin.example.carousel.adapters.WrapperRecyclerViewAdapter;

public abstract class BaseItemChangedListener {

	public final static int										DEFAULT_OFFSET	= -1;

	private int													mOffset			= DEFAULT_OFFSET;

	private WrapperRecyclerViewAdapter<? extends ViewHolder>	mWrapperRecyclerViewAdapter;


	public int getOffset() {
		return mOffset;
	}


	public void setOffset(int pOffset) {
		mOffset = pOffset;
	}


	public WrapperRecyclerViewAdapter<? extends ViewHolder> getWrapperRecyclerViewAdapter() {
		return mWrapperRecyclerViewAdapter;
	}


	public void setWrapperRecyclerViewAdapter(
			WrapperRecyclerViewAdapter<? extends ViewHolder> pWrapperRecyclerViewAdapter) {
		mWrapperRecyclerViewAdapter = pWrapperRecyclerViewAdapter;
	}

}
