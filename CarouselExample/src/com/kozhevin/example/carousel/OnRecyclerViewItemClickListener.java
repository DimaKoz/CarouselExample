package com.kozhevin.example.carousel;

import android.view.View;

public interface OnRecyclerViewItemClickListener<Model> {

	public void onItemClick(View view, Model model);
	
}