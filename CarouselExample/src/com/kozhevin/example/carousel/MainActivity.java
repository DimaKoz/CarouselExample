package com.kozhevin.example.carousel;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.State;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kozhevin.example.carousel.adapters.CarouselAdapter;
import com.kozhevin.example.carousel.adapters.WrapperRecyclerViewAdapter;
import com.kozhevin.example.carousel.listeners.OnLappingItemListener;
import com.kozhevin.example.carousel.swipedismiss.SwipeDismissRecyclerViewTouchListener;

public class MainActivity extends Activity {

	private static final String								LOG_TAG				= "MainActivity";

	private static final int								ITEM_COUNT			= 5;

	private final float										TRANSLATION_VALUE_X	= -1400f;

	private CarouselAdapter									mAdapter;
	private List<ViewModel>									mListModel;
	private CarouselRecyclerView							mRecyclerView;
	private WrapperRecyclerViewAdapter<CarouselViewHolder>	mWrapperRecyclerViewAdapter;

	private OnLappingItemListener							mOnLappingItemListener;

	private CarouselLayoutManager							mLinearLayoutManager;

	private CarouselItemAnimator							mDefaultItemAnimator;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mRecyclerView = (CarouselRecyclerView)findViewById(R.id.list);

		mRecyclerView.setHasFixedSize(false);
		mListModel = createMockList();
		mAdapter = new CarouselAdapter(this);
		mAdapter.setData(mListModel);
		mWrapperRecyclerViewAdapter = new WrapperRecyclerViewAdapter<CarouselViewHolder>();
		mWrapperRecyclerViewAdapter.setAdaper(mAdapter);
		mAdapter.setPositionOffset(-mWrapperRecyclerViewAdapter.getOffsetPositionForAnimations());
		mRecyclerView.setAdapter(mWrapperRecyclerViewAdapter);

		mLinearLayoutManager = new CarouselLayoutManager(LinearLayout.HORIZONTAL, false);
		mLinearLayoutManager.supportsPredictiveItemAnimations();
		mRecyclerView.setLayoutManager(mLinearLayoutManager);
		mDefaultItemAnimator = new CarouselItemAnimator();
		mDefaultItemAnimator.setTranslationValueY(TRANSLATION_VALUE_X);
		mRecyclerView.setItemAnimator(mDefaultItemAnimator);

		mAdapter.setOnItemClickListener(new CarouselAdapter.OnCarouselItemClickListener() {

			@Override
			public void onImageClick(int position) {
				mAdapter.showRemoveItemAnimation(position);
				mListModel.remove(position);

			}


			@Override
			public void onDeleteButtonClick(int position) {
				mAdapter.showRemoveItemAnimation(position);
				mListModel.remove(position);
			}
		});

		// TODO need determine a translation of X value
		mDefaultItemAnimator.setTranslationValueY(TRANSLATION_VALUE_X);
		mRecyclerView.setItemAnimator(mDefaultItemAnimator);

		Button lMagicButton = (Button)findViewById(R.id.button1);
		lMagicButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				int lcount = mLinearLayoutManager.getPositionForInsert();
				Log.d(getPackageName(), "a new item inserted into " + lcount);
				mListModel.add(new ViewModel(lcount, "Item " + (lcount + 1), null));
				mAdapter.notifyDataSetChanged();
				mWrapperRecyclerViewAdapter.notifyDataSetChanged();

			}
		});

		mOnLappingItemListener = new OnLappingItemListener(mRecyclerView, mLinearLayoutManager);

		mLinearLayoutManager.setOnLappingItemListener(mOnLappingItemListener);
		if (mWrapperRecyclerViewAdapter.getItemCount() > 1) {
			mOnLappingItemListener.onItemLapping(1);
		}

		mLinearLayoutManager.smoothScrollToPosition(mRecyclerView, new State(), 1);
		initSwipe();
	}


	private List<ViewModel> createMockList() {
		List<ViewModel> items = new ArrayList<ViewModel>();
		for (int i = 0; i < ITEM_COUNT; i++) {
			items.add(new ViewModel(i, "Item " + (i + 1), null));
		}
		return items;
	}


	private void initSwipe() {
		SwipeDismissRecyclerViewTouchListener touchListener = new SwipeDismissRecyclerViewTouchListener(
				mRecyclerView,
				new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {

					@Override
					public boolean canDismiss(int position) {
						if (mWrapperRecyclerViewAdapter != null) {
							if (position == 0 || ((position + 1) == mWrapperRecyclerViewAdapter.getItemCount())) {
								return false;
							}
						}

						return true;
					}


					@Override
					public void onDismiss(RecyclerView pListView, int[] pReverseSortedPositions) {
						for (int position : pReverseSortedPositions) {

							Log.v(LOG_TAG, "position removed =" + (position - 1));
							if (position == 0) {
								return;
							}
							mListModel.remove(position - 1);
							mDefaultItemAnimator.setIsUseTranslationAnimation(false);
							mAdapter.notifyItemRemoved(position - 1);

						}
					}
				});

		mRecyclerView.setOnTouchListener(touchListener);
		mRecyclerView.setOnHoverListener(touchListener);
		mRecyclerView.addOnItemTouchListener(touchListener);

		// Setting this scroll listener is required to ensure that during
		// ReciclerView scrolling,
		// we don't look for swipes.
		mRecyclerView.setOnScrollListener(touchListener.makeScrollListener());

	}
}
