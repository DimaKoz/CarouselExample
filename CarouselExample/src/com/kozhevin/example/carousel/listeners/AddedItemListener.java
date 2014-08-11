package com.kozhevin.example.carousel.listeners;

public class AddedItemListener extends BaseItemChangedListener implements OnAddedItemListener {

	@Override
	public void onItemAdded(int pPositionItem) {

		if (getOffset() == DEFAULT_OFFSET) {
			throw new IllegalArgumentException("Offset value has not been set.");
		}

		if (getWrapperRecyclerViewAdapter() == null) {
			throw new NullPointerException("Adapter has not been set.");
		}

		if (getWrapperRecyclerViewAdapter().getAdaper() == null) {
			throw new NullPointerException("Adapter has not been set.");
		}

		getWrapperRecyclerViewAdapter().getAdaper().notifyItemInserted(pPositionItem + getOffset());

	}

}
