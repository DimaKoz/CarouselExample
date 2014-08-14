package com.kozhevin.example.carousel.adapters;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kozhevin.example.carousel.R;


public class WrapperRecyclerViewAdapter<T extends ViewHolder> extends RecyclerView.Adapter<ViewHolder> {

	private final static String TAG = "WrapperRecyclerViewAdapter";

	private static final boolean DEBUG = false;

	private final static int TYPE_WRAPPER_START = 0;
	private final static int TYPE_WRAPPER_FINISH = 2;
	private final static int TYPE_WRAPPERED = 1;
	
	private final static int OFFSET_WITH_START_ITEM = 1;
	private final static int OFFSET_FULL = 2;

	RecyclerView.Adapter<T> mAdaper;

	public WrapperRecyclerViewAdapter() {

	}

	@SuppressWarnings("unchecked")
	public void setAdaper(Adapter<? extends ViewHolder> pAdapter) {
		mAdaper = (Adapter<T>) pAdapter;
	}

	@Override
	public void registerAdapterDataObserver(AdapterDataObserver pObserver) {
		
		if (mAdaper != null) {
			mAdaper.registerAdapterDataObserver(pObserver);
		}
		
	}

	@Override
	public void unregisterAdapterDataObserver(AdapterDataObserver pObserver) {
		
		if (mAdaper != null) {
			mAdaper.unregisterAdapterDataObserver(pObserver);
		}
		
	}

	@Override
	public int getItemCount() {
		
		return mAdaper.getItemCount() + OFFSET_FULL;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onBindViewHolder(ViewHolder pHolder, int pPosition) {
		
		if (getItemViewType(pPosition) == TYPE_WRAPPERED) {
			mAdaper.onBindViewHolder((T) pHolder, pPosition - OFFSET_WITH_START_ITEM);
		}
		
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup pHolder, int pType) {

		if (DEBUG) {
			Log.d(TAG, "CreateViewHolder with type =" + pType);
		}

		if (pType == TYPE_WRAPPER_START) {
			View v = LayoutInflater.from(pHolder.getContext()).inflate(R.layout.wrapper_item, pHolder, false);
			WrapperViewHolderStart lWrapperViewHolderStart = new WrapperViewHolderStart(v);
			return lWrapperViewHolderStart;
		}

		if (pType == TYPE_WRAPPER_FINISH) {
			View v = LayoutInflater.from(pHolder.getContext()).inflate(R.layout.wrapper_item, pHolder, false);
			return new WrapperViewHolderFinish(v);
		}

		return mAdaper.onCreateViewHolder(pHolder, pType);

	}

	@Override
	public int getItemViewType(int pPosition) {

		if (pPosition == (mAdaper.getItemCount() + OFFSET_WITH_START_ITEM)) {
			return TYPE_WRAPPER_FINISH;
		}

		if (pPosition == 0) {
			return TYPE_WRAPPER_START;
		}

		return TYPE_WRAPPERED;

	}

	public static class WrapperViewHolderStart extends RecyclerView.ViewHolder {

		public WrapperViewHolderStart(View itemView) {
			super(itemView);
		}
		
	}

	public static class WrapperViewHolderFinish extends RecyclerView.ViewHolder {

		public WrapperViewHolderFinish(View itemView) {
			super(itemView);
		}

	}

	@Override
	public long getItemId(int pPosition) {
		return super.getItemId(pPosition);
	}

	@Override
	public void setHasStableIds(boolean pHasStableIds) {
		super.setHasStableIds(pHasStableIds);
	}


	public int getOffsetPositionForAnimations() {
		return OFFSET_WITH_START_ITEM;
	}
	
	public RecyclerView.Adapter<T> getAdaper() {
		return mAdaper;
	}
}
