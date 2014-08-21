package com.kozhevin.example.carousel.listeners;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.State;
import android.util.Log;

import com.kozhevin.example.carousel.CarouselLayoutManager;

public class OnLappingItemListener implements IOnLappingItemListener {

	private static final String		TAG	= "OnLappingItemListener";

	private CarouselLayoutManager	mCarouselLayoutManager;
	private RecyclerView			mRecyclerView;


	@SuppressWarnings("unused")
	private OnLappingItemListener() {};


	public OnLappingItemListener(RecyclerView pRecyclerView, CarouselLayoutManager pCarouselLayoutManager) {
		mRecyclerView = pRecyclerView;
		mCarouselLayoutManager = pCarouselLayoutManager;
	}


	@Override
	public void onItemLapping(final int pPositionForLapping) {

		if (mCarouselLayoutManager == null) {
			throw new NullPointerException("CarouselLayoutManager is not setted.");
		}

		if (mRecyclerView == null) {
			throw new NullPointerException("RecyclerView is not setted.");
		}

		// FIXME Will need to enable our animation when scrolling list
		// we'll remove this bugfix later when our animation will enabled
		// A bugfix's temporary
		ThreadHolder.finishTread();
		ThreadHolder.setThread(new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					for (int i = 0; i < 10; ++i) {
						if (ThreadHolder.getThread().isInterrupted()) {
							return;
						}
						Thread.sleep(50);
					}
				}catch(InterruptedException e) {

					e.printStackTrace();
				}
				if (ThreadHolder.getThread().isInterrupted()) {
					return;
				}

				Log.d(TAG, this.toString() + " trying scroll to: " + pPositionForLapping);
				if (mCarouselLayoutManager != null) {
					mCarouselLayoutManager.smoothScrollToPosition(mRecyclerView, new State(), pPositionForLapping);
				}
			}
		}));
		ThreadHolder.getThread().start();

	}

	private static class ThreadHolder {

		private static Thread	mThread;


		public static Thread getThread() {
			return mThread;
		}


		public static void setThread(Thread pThread) {
			mThread = pThread;
		}


		public static void finishTread() {
			if (mThread != null && mThread.getState() != Thread.State.TERMINATED) {
				Log.w(TAG, "Thread interrupt()");
				mThread.interrupt();
				while (mThread.getState() != Thread.State.TERMINATED) {

				}
			}

		}

	}

}
