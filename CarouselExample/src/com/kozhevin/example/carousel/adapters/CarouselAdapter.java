package com.kozhevin.example.carousel.adapters;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kozhevin.example.carousel.CarouselViewHolder;
import com.kozhevin.example.carousel.R;
import com.kozhevin.example.carousel.ViewModel;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselViewHolder> {
	
	private final static String		TAG						= "CarouselAdapter";

	private List<ViewModel>								mItems;
	private LayoutInflater				mLayoutInflater;
	private OnCarouselItemClickListener	mOnCarouselItemClickListener;
	private int							mPositionOffset;


	public CarouselAdapter(Context context) {
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		registerAdapterDataObserver(new AdapterDataObserver() {

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				Log.v(TAG,"CAdapter removed: pos = " + positionStart + ", itemCount = " + itemCount);
			}
		});
	}


	@Override
	public int getItemCount() {
		if (mItems == null) {
			return 0;
		}
		return mItems.size();
	}


	@Override
	public void onBindViewHolder(CarouselViewHolder pHolder, int pPosition) {
		Log.v(TAG, "onBindViewHolder pos = "+ pPosition);
		ViewModel item = mItems.get(pPosition);
		pHolder.text.setText(item.getText());
		switch (pPosition) {
		case 0:
			pHolder.image.setImageResource(R.drawable.item1);
			break;
		case 1:
			pHolder.image.setImageResource(R.drawable.item2);
			break;
		case 2:
			pHolder.image.setImageResource(R.drawable.item3);
			break;
		default:
			pHolder.image.setImageResource(R.drawable.item4);
		}
		pHolder.itemView.setTag(item);
		pHolder.itemView.setTag(R.string.app_name, pPosition);
		Log.v(TAG, "After binding");
	}


	@Override
	public CarouselViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = mLayoutInflater.inflate(R.layout.item, parent, false);
		CarouselViewHolder viewHolder = new CarouselViewHolder(v);
		
		viewHolder.setPositionOffset(mPositionOffset);
		viewHolder.setOnCarouselItemClickListener(mOnCarouselItemClickListener);
		return viewHolder;
	}


	public void setData(List<ViewModel> pListViewModel) {
		if (pListViewModel == null) {
			return;
		}
		mItems = pListViewModel;
		notifyDataSetChanged();
	}
	


	public void showRemoveItemAnimation(int position) {
		Log.v(TAG, "CAdapter showAnim: pos = " + position);
		notifyItemRemoved(position);
	}


	public void setOnItemClickListener(OnCarouselItemClickListener listener) {
		mOnCarouselItemClickListener = listener;
	}


	public void setPositionOffset(int offset) {
		mPositionOffset = offset;
	}



	public interface OnCarouselItemClickListener {

		public void onImageClick(int position);


		public void onDeleteButtonClick(int position);
	}
}
