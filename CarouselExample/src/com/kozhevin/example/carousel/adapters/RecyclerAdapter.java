package com.kozhevin.example.carousel.adapters;


import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kozhevin.example.carousel.R;
import com.kozhevin.example.carousel.ViewModel;
import com.kozhevin.example.carousel.listeners.OnAddedItemListener;
import com.kozhevin.example.carousel.listeners.OnDeletedItemListener;
import com.kozhevin.example.carousel.listeners.OnRecyclerViewItemClickListener;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements View.OnClickListener {

	private List<ViewModel>								mItems;
	private OnRecyclerViewItemClickListener<ViewModel>	mItemClickListener;
	private int											mItemLayout;

	private OnAddedItemListener mOnAddedItemListener;
	private OnDeletedItemListener mOnDeletedItemListener;

	public RecyclerAdapter(List<ViewModel> items, int itemLayout) {
		mItems = items;
		mItemLayout = itemLayout;
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(mItemLayout, parent, false);
		v.setOnClickListener(this);
		return new ViewHolder(v);
	}


	@Override
	public void onBindViewHolder(ViewHolder pHolder, int pPosition) {
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
		pHolder.itemView.setTag(R.string.app_name,pPosition);
		
	}


	@Override
	public int getItemCount() {
		return mItems.size();
	}


	@Override
	public void onClick(View view) {
		if (mItemClickListener != null) {
			ViewModel model = (ViewModel)view.getTag();
			mItemClickListener.onItemClick(view, model);
		}
	}


	public void add(ViewModel pItem, int pPosition) {
		mItems.add(pPosition, pItem);
		//FIXME move to listener
		//notifyItemInserted(pPosition + 1);

		if (mOnAddedItemListener != null) {
			mOnAddedItemListener.onItemAdded(pPosition);
		}
	}


	public void remove(ViewModel pItem) {
		int lPosition = mItems.indexOf(pItem);
		mItems.remove(lPosition);
		//FIXME move to listener
		// notifyItemRemoved(lPosition+1);
		if (mOnDeletedItemListener != null) {
			mOnDeletedItemListener.onItemDeleted(lPosition);
		}
	}


	public void setOnItemClickListener(OnRecyclerViewItemClickListener<ViewModel> listener) {
		mItemClickListener = listener;
	}


	public OnDeletedItemListener getOnDeletedItemListener() {
		return mOnDeletedItemListener;
	}


	public void setOnDeletedItemListener(OnDeletedItemListener pOnDeletedItemListener) {
		mOnDeletedItemListener = pOnDeletedItemListener;
	}


	public OnAddedItemListener getOnAddedItemListener() {
		return mOnAddedItemListener;
	}


	public void setOnAddedItemListener(OnAddedItemListener pOnAddedItemListener) {
		mOnAddedItemListener = pOnAddedItemListener;
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {

		public ImageView	image;
		public TextView		text;


		public ViewHolder(View itemView) {
			super(itemView);
			image = (ImageView)itemView.findViewById(R.id.image);
			text = (TextView)itemView.findViewById(R.id.text);
		}
	}


	
}
