package com.kozhevin.example.carousel;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kozhevin.example.carousel.adapters.CarouselAdapter;
import com.kozhevin.example.carousel.listeners.OnLappingItemListener;
import com.kozhevin.example.carousel.swipedismiss.SwipeDismissRecyclerViewTouchListener;

public class MainActivity extends Activity {

	private static final String								LOG_TAG				= "MainActivity";

	private static final int								ITEM_COUNT			= 5;

	private final float										TRANSLATION_VALUE_X	= -1400f;

	private CarouselAdapter									mAdapter;
	private List<ViewModel>									mListModel;
	private CarouselRecyclerView							mRecyclerView;

	private OnLappingItemListener							mOnLappingItemListener;

//	private CarouselLayoutManager							mLinearLayoutManager;

	private CarouselItemAnimator							mDefaultItemAnimator;

	private CarouselLayoutManager	mLinearLayoutManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mRecyclerView = (CarouselRecyclerView)findViewById(R.id.list);

		mRecyclerView.setHasFixedSize(false);
		mListModel = createMockList();
		mAdapter = new CarouselAdapter(this);
		mAdapter.setData(mListModel);
		mRecyclerView.setAdapter(mAdapter);

		mLinearLayoutManager = new CarouselLayoutManager(this, LinearLayout.HORIZONTAL, false);
		mLinearLayoutManager.supportsPredictiveItemAnimations();
		mRecyclerView.setLayoutManager(mLinearLayoutManager);
		mDefaultItemAnimator = new CarouselItemAnimator();
		mDefaultItemAnimator.setTranslationValueY(TRANSLATION_VALUE_X);
		mRecyclerView.setItemAnimator(mDefaultItemAnimator);

		mAdapter.setOnItemClickListener(new CarouselAdapter.OnCarouselItemClickListener() {

			@Override
			public void onImageClick(int position) {
				if (position == 0) {
					return;
				}
				Log.v(LOG_TAG, " onImageClick position removed =" + (position));
				
				mListModel.remove(position - mAdapter.getOffsetPositionForAnimations());
				mAdapter.showRemoveItemAnimation(position - mAdapter.getOffsetPositionForAnimations());

			}


			@Override
			public void onDeleteButtonClick(int position) {
				if (position == 0) {
					return;
				}
				Log.v(LOG_TAG, " onDeleteButtonClick position removed =" + (position));
				mListModel.remove(position - mAdapter.getOffsetPositionForAnimations());
				mAdapter.showRemoveItemAnimation(position - mAdapter.getOffsetPositionForAnimations());
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

			}
		});

		mOnLappingItemListener = new OnLappingItemListener(mRecyclerView, mLinearLayoutManager);

		mLinearLayoutManager.setOnLappingItemListener(mOnLappingItemListener);
		if (mAdapter.getItemCount() > 1) {
			mOnLappingItemListener.onItemLapping(1);
		}

	

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
						if (mAdapter != null) {
							if (position == 0 || ((position + mAdapter.getOffsetPositionForAnimations()) == mAdapter.getItemCount())) {
								return false;
							}
						}

						return true;
					}


					@Override
					public void onDismiss(RecyclerView pListView, int[] pReverseSortedPositions) {
						for (int position : pReverseSortedPositions) {

							Log.v(LOG_TAG, " onDismiss position removed =" + (position));
							if (position == 0) {
								return;
							}
							mListModel.remove(position);
							mDefaultItemAnimator.setIsUseTranslationAnimation(false);
							mAdapter.notifyItemRemoved(position);

						}
					}
				});

		mRecyclerView.setOnTouchListener(touchListener);
		mRecyclerView.addOnItemTouchListener(touchListener);

		// Setting this scroll listener is required to ensure that during
		// ReciclerView scrolling,
		// we don't look for swipes.
		mRecyclerView.setOnScrollListener(touchListener.makeScrollListener());

	}
}
