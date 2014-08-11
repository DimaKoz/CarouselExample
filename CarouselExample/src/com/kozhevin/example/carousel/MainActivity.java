package com.kozhevin.example.carousel;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;

import com.kozhevin.example.carousel.adapters.RecyclerAdapter;
import com.kozhevin.example.carousel.adapters.WrapperRecyclerViewAdapter;
import com.kozhevin.example.carousel.listeners.AddedItemListener;
import com.kozhevin.example.carousel.listeners.DeletedItemListener;
import com.kozhevin.example.carousel.listeners.OnRecyclerViewItemClickListener;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	private static final int ITEM_COUNT = 3;


	private final float TRANSLATION_VALUE_X = -1400f;
	
	
	private RecyclerAdapter mAdapter;
	private List<ViewModel> mListModel;
	private CarouselLayoutManager mLinearLayoutManager;
	private AddedItemListener mAddedItemListener;

	private WrapperRecyclerViewAdapter<ViewHolder> mWrapperRecyclerViewAdapter;


	private DeletedItemListener mDeletedItemListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);

		recyclerView.setHasFixedSize(false);
		mListModel = createMockList();
		mAdapter = new RecyclerAdapter(mListModel, R.layout.item);
		mWrapperRecyclerViewAdapter = new WrapperRecyclerViewAdapter<RecyclerView.ViewHolder>();
		mWrapperRecyclerViewAdapter.setAdaper(mAdapter);
		
		mAddedItemListener = new AddedItemListener();
		mAddedItemListener.setOffset(mWrapperRecyclerViewAdapter.getOffsetPositionForAnimations());
		mAddedItemListener.setWrapperRecyclerViewAdapter(mWrapperRecyclerViewAdapter);
		
		mDeletedItemListener = new DeletedItemListener();
		mDeletedItemListener.setOffset(mWrapperRecyclerViewAdapter.getOffsetPositionForAnimations());
		mDeletedItemListener.setWrapperRecyclerViewAdapter(mWrapperRecyclerViewAdapter);
		
		mAdapter.setOnAddedItemListener(mAddedItemListener);
		mAdapter.setOnDeletedItemListener(mDeletedItemListener);
		
		recyclerView.setAdapter(mWrapperRecyclerViewAdapter);
		mLinearLayoutManager = new CarouselLayoutManager(this, LinearLayout.HORIZONTAL, false);
		mLinearLayoutManager.supportsPredictiveItemAnimations();
		recyclerView.setLayoutManager(mLinearLayoutManager);
		CarouselItemAnimator lDefaultItemAnimator = new CarouselItemAnimator();
		//TODO need determine a translation of X value 
		lDefaultItemAnimator.setTranslationValueY(TRANSLATION_VALUE_X);
		recyclerView.setItemAnimator(lDefaultItemAnimator);
		
		mAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<ViewModel>() {

			@Override
			public void onItemClick(View view, ViewModel viewModel) {
				mAdapter.remove(viewModel);
				mWrapperRecyclerViewAdapter.notifyDataSetChanged();

			}
		});

		Button lMagicButton = (Button) findViewById(R.id.button1);
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
	}

	private List<ViewModel> createMockList() {
		List<ViewModel> items = new ArrayList<ViewModel>();
		for (int i = 0; i < ITEM_COUNT; i++) {
			items.add(new ViewModel(i, "Item " + (i + 1), null));
		}
		return items;
	}

}
