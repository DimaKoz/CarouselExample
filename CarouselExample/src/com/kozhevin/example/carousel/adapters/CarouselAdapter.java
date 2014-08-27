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

	private final static String			TAG						= "CarouselAdapter";

	private List<ViewModel>				mItems;
	private LayoutInflater				mLayoutInflater;
	private OnCarouselItemClickListener	mOnCarouselItemClickListener;

	private final static int			OFFSET_WITH_START_ITEM	= 1;

	private final static int			TYPE_WRAPPER_START		= 0;
	private final static int			TYPE_WRAPPER_FINISH		= 2;
	private final static int			TYPE_WRAPPED			= 1;

	private final static boolean		IS_DEBUG				= false;


	public CarouselAdapter(Context context) {
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		registerAdapterDataObserver(new AdapterDataObserver() {

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				if (IS_DEBUG) {
					Log.v(TAG, "CAdapter removed: pos = " + positionStart + ", itemCount = " + itemCount);
				}
			}
		});
	}


	@Override
	public void onBindViewHolder(CarouselViewHolder pHolder, int pPosition) {
		if (IS_DEBUG) {
			Log.v(TAG, "onBindViewHolder pos = " + pPosition);
		}
		if (getItemViewType(pPosition) == TYPE_WRAPPED) {
			ViewModel item = mItems.get(pPosition);

			pHolder.text.setText(item.getText());

			switch (pPosition - OFFSET_WITH_START_ITEM) {
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
		}

		if (IS_DEBUG) {
			Log.v(TAG, "After binding");
		}
	}


	@Override
	public CarouselViewHolder onCreateViewHolder(ViewGroup pParent, int pViewType) {

		if (IS_DEBUG) {
			Log.d(TAG, "CreateViewHolder with type =" + pViewType);
		}

		if (pViewType == TYPE_WRAPPER_START || pViewType == TYPE_WRAPPER_FINISH) {
			View v = mLayoutInflater.inflate(R.layout.wrapper_item, pParent, false);
			return new CarouselViewHolder(v);
		}

		View v = mLayoutInflater.inflate(R.layout.item, pParent, false);
		CarouselViewHolder viewHolder = new CarouselViewHolder(v);

		viewHolder.setPositionOffset(OFFSET_WITH_START_ITEM);
		viewHolder.setOnCarouselItemClickListener(mOnCarouselItemClickListener);

		return viewHolder;
	}


	public void setData(List<ViewModel> pListViewModel) {

		if (pListViewModel == null) {
			return;
		}
		mItems = pListViewModel;
		appendOffsetItems();
		notifyDataSetChanged();

	}


	private void appendOffsetItems() {

		if (mItems == null) {
			return;
		}

		mItems.add(0, new ViewModel(0, "", ""));
		mItems.add(new ViewModel(0, "", ""));

	}


	public void showRemoveItemAnimation(int position) {
		if (IS_DEBUG) {
			Log.v(TAG, "CAdapter showAnim: pos = " + position);
		}
		notifyItemRemoved(position);
	}


	public void setOnItemClickListener(OnCarouselItemClickListener listener) {
		mOnCarouselItemClickListener = listener;
	}


	@Override
	public int getItemCount() {
		if (mItems == null) {
			return 0;
		}
		return mItems.size();
	}


	public int getOffsetPositionForAnimations() {
		return OFFSET_WITH_START_ITEM;
	}


	@Override
	public int getItemViewType(int pPosition) {

		if (pPosition == getItemCount() - OFFSET_WITH_START_ITEM) {
			return TYPE_WRAPPER_FINISH;
		}

		if (pPosition == 0) {
			return TYPE_WRAPPER_START;
		}

		return TYPE_WRAPPED;

	}

	public interface OnCarouselItemClickListener {

		public void onImageClick(int position);


		public void onDeleteButtonClick(int position);
	}

}
