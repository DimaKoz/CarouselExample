package com.kozhevin.example.carousel;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	private final float TRANSLATION_VALUE_X = -1400f;
	
	
	private RecyclerAdapter adapter;
	private List<ViewModel> mListModel;
	private CarouselLayoutManager mLinearLayoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
		recyclerView.setHasFixedSize(false);
		mListModel = createMockList();
		adapter = new RecyclerAdapter(mListModel, R.layout.item);
		recyclerView.setAdapter(adapter);
		mLinearLayoutManager = new CarouselLayoutManager(this, LinearLayout.HORIZONTAL, false);
		mLinearLayoutManager.supportsPredictiveItemAnimations();
		recyclerView.setLayoutManager(mLinearLayoutManager);
		CarouselItemAnimator lDefaultItemAnimator = new CarouselItemAnimator();
		//TODO need determine a translation of X value 
		lDefaultItemAnimator.setTranslationValueY(TRANSLATION_VALUE_X);
		recyclerView.setItemAnimator(lDefaultItemAnimator);
		adapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<ViewModel>() {

			@Override
			public void onItemClick(View view, ViewModel viewModel) {
				adapter.remove(viewModel);
			}
		});

		Button lMagicButton = (Button) findViewById(R.id.button1);
		lMagicButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				int lcount = mLinearLayoutManager.getPositionForInsert();
				Log.d(getPackageName(), "a new item inserted into " + lcount);
				mListModel.add(new ViewModel(lcount, "Item " + (lcount + 1), null));
				adapter.notifyDataSetChanged();
			}
		});
	}

	private List<ViewModel> createMockList() {
		List<ViewModel> items = new ArrayList<ViewModel>();
		for (int i = 0; i < 5; i++) {
			items.add(new ViewModel(i, "Item " + (i + 1), null));
		}
		return items;
	}

}
