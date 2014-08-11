package com.kozhevin.example.carousel.listeners;

public class DeletedItemListener extends BaseItemChangedListener implements OnDeletedItemListener {

	@Override
	public void onItemDeleted(int pPositionItem) {

		if (getOffset() == DEFAULT_OFFSET) {
			throw new IllegalArgumentException("Offset value has not been set.");
		}

		if (getWrapperRecyclerViewAdapter() == null) {
			throw new NullPointerException("Wrapper of adapter has not been set.");
		}

		if (getWrapperRecyclerViewAdapter().getAdaper() == null) {
			throw new NullPointerException("Adapter has not been set.");
		}

		getWrapperRecyclerViewAdapter().getAdaper().notifyItemRemoved(pPositionItem + getOffset());
	}

}
