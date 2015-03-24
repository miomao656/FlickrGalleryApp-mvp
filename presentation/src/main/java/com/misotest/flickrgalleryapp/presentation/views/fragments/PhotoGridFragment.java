package com.misotest.flickrgalleryapp.presentation.views.fragments;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.misotest.flickrgalleryapp.R;
import com.misotest.flickrgalleryapp.data.repository.PhotoDataRepository;
import com.misotest.flickrgalleryapp.data.repository.datasource.PhotosDbStore;
import com.misotest.flickrgalleryapp.domain.interactor.GetPhotosUseCaseImpl;
import com.misotest.flickrgalleryapp.domain.interactor.IGetPhotosUseCase;
import com.misotest.flickrgalleryapp.presentation.animation.RecyclerInsetsDecoration;
import com.misotest.flickrgalleryapp.presentation.entity.PhotoPresentationModel;
import com.misotest.flickrgalleryapp.presentation.mvp.presenters.PhotosListPresenter;
import com.misotest.flickrgalleryapp.presentation.utils.CommonUtils;
import com.misotest.flickrgalleryapp.presentation.utils.DeviceDimensionsHelper;
import com.misotest.flickrgalleryapp.presentation.utils.FragmentHelper;
import com.misotest.flickrgalleryapp.presentation.mvp.viewinterfaces.PhotoGridView;
import com.misotest.flickrgalleryapp.presentation.views.adapters.PhotosGridAdapter;
import com.misotest.flickrgalleryapp.presentation.views.customviews.CircleView;
import com.misotest.flickrgalleryapp.presentation.views.customviews.CustomRecyclerView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * A Fragment containing a grid view.
 */
public class PhotoGridFragment extends Fragment implements PhotoGridView, View.OnTouchListener {

    public static final String TAG = PhotoGridFragment.class.getSimpleName();

    @InjectView(R.id.my_recycler_view)
    CustomRecyclerView mRecyclerView;
    @InjectView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @InjectView(R.id.long_press_container)
    RelativeLayout mRelativeLayout;

    private PhotosGridAdapter mItemListAdapter;
    private PhotosListPresenter mPhotoListPresenter;
    private boolean isPaging;
    private GestureDetector gestureDetector;

    private RelativeLayout menu_delete;
    private RelativeLayout menu_share;

    private int screenWidth;
    private int screenHeight;
    private Rect outRect = new Rect();
    private int[] location = new int[2];
    private boolean scrollable;
    private int positionWhenPressed;

    public PhotoGridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecyclerInsetsDecoration(this.getContext()));
        mItemListAdapter = new PhotosGridAdapter();
        mRecyclerView.setAdapter(mItemListAdapter);
        gestureDetector = new GestureDetector(getContext(), new SingleTapConfirm());
        mRecyclerView.setOnTouchListener(this);
        screenWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());
        screenHeight = DeviceDimensionsHelper.getDisplayHeight(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        startPresenter();
    }

    //todo release resources when paused
//    @Override
//    public void onPause() {
//        mPhotoListPresenter.stop();
//        super.onPause();
//    }

    /**
     * Checks if view is in bounds and returns true if it is false otherwise
     *
     * @param view
     * @param x
     * @param y
     * @return
     */
    private boolean inViewInBounds(View view, int x, int y) {
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    /**
     * Displays the circle with animated buttons for share and delete
     *
     * @param motionEvent
     */
    private void displayMenu(MotionEvent motionEvent) {
        // set position for photo to be handled
        positionWhenPressed = getPositionTouched(motionEvent);

        menu_delete = (RelativeLayout) getActivity().getLayoutInflater()
                .inflate(R.layout.menu_button_delete, null);
        menu_share = (RelativeLayout) getActivity().getLayoutInflater()
                .inflate(R.layout.menu_button_share, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int offset = DeviceDimensionsHelper.convertDpToPixel(20, getContext());
        params.leftMargin = (int) motionEvent.getX() - offset;
        params.topMargin = (int) motionEvent.getY() - offset;
        mRelativeLayout.addView(new CircleView(getContext(), motionEvent.getX(), motionEvent.getY(), 25));
        menu_delete.setAlpha(0f);
        menu_share.setAlpha(0f);
        mRelativeLayout.addView(menu_delete, params);
        mRelativeLayout.addView(menu_share, params);

        if ((screenHeight / 2) > motionEvent.getY() && (screenWidth / 2) > motionEvent.getX()) {
            //upper left
            menu_share.animate().translationX(-20).translationY(-170).alpha(1f).setDuration(300);
            menu_delete.animate().translationX(100).translationY(-150).alpha(1f).setDuration(300);
        } else if ((screenHeight / 2) > motionEvent.getY() && (screenWidth / 2) < motionEvent.getX()) {
            //upper right
            menu_share.animate().translationX(50).translationY(-170).alpha(1f).setDuration(300);
            menu_delete.animate().translationX(-100).translationY(-150).alpha(1f).setDuration(300);
        } else if ((screenHeight / 2) < motionEvent.getY() && (screenWidth / 2) > motionEvent.getX()) {
            //lower left
            menu_share.animate().translationX(-20).translationY(-170).alpha(1f).setDuration(300);
            menu_delete.animate().translationX(100).translationY(-150).alpha(1f).setDuration(300);
        } else if ((screenHeight / 2) < motionEvent.getY() && (screenWidth / 2) < motionEvent.getX()) {
            //lower right
            menu_share.animate().translationX(50).translationY(-170).alpha(1f).setDuration(300);
            menu_delete.animate().translationX(-100).translationY(-150).alpha(1f).setDuration(300);
        }
        mRecyclerView.animate().alpha(0.7f).setDuration(300);
    }

    /**
     * Initializes the presenter
     */
    private void startPresenter() {
        PhotosDbStore photosDbStore = new PhotosDbStore(getActivity().getApplicationContext());
        PhotoDataRepository photoDataRepository = PhotoDataRepository.getInstance(photosDbStore);
        IGetPhotosUseCase iGetPhotosUseCase = new GetPhotosUseCaseImpl(photoDataRepository);
        mPhotoListPresenter = new PhotosListPresenter(this, iGetPhotosUseCase);
        mPhotoListPresenter.setQuery("akita");
        mPhotoListPresenter.startPresenting();
        isPaging = false;
    }

    /**
     * Opens a details fragment on photo click
     *
     * @param position
     */
    private void onPhotoClick(int position) {
        FragmentHelper.prepareAndShowFragment(getActivity(), R.id.fragment_container,
                PhotoPagerFragment.newInstance(position), true, PhotoPagerFragment.TAG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void presentPhotos(List<PhotoPresentationModel> photoPresentationModels) {
        if (photoPresentationModels != null && !photoPresentationModels.isEmpty()) {
            mItemListAdapter.addPhotos(photoPresentationModels, isPaging);
        }
    }

    @Override
    public void presentPhotosUpdated(List<PhotoPresentationModel> itemDomainEntityList) {
        if (itemDomainEntityList != null && !itemDomainEntityList.isEmpty()) {
            mItemListAdapter.updatePhotos(itemDomainEntityList);
        }
    }

    @Override
    public void showLoading() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void hideLoading() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPhotoDeleted(String photoID) {
        Timber.d("deleted photo " + photoID);
    }

    @Override
    public void showError(String error) {
        Timber.e(error);
    }

    @Override
    public void updatePhotoInList(PhotoPresentationModel presentationModel) {
        if (presentationModel != null) {
            mItemListAdapter.updatePhoto(presentationModel);
        }
    }

    @Override
    public Context getContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (gestureDetector.onTouchEvent(motionEvent)) {
            // single click
            onPhotoClick(getPositionTouched(motionEvent));
            return true;
        } else {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                // dispatch event to gesture listener for long click
                gestureDetector.onTouchEvent(motionEvent);
                scrollable = true;
                return true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                int x = (int) motionEvent.getRawX();
                int y = (int) motionEvent.getRawY();
                if (menu_share != null && menu_delete != null) {
                    if (inViewInBounds(menu_share, x, y)) {
                        String filePath = mItemListAdapter.getPhoto(positionWhenPressed).photo_file_path;
                        if (filePath != null) {
                            CommonUtils.shareImage(getActivity(), mItemListAdapter.getPhoto(positionWhenPressed).photo_file_path);
                        }
                        Timber.e("share " + positionWhenPressed);
                    }

                    if (inViewInBounds(menu_delete, x, y)) {
                        mPhotoListPresenter.deletePhoto(mItemListAdapter.getPhoto(positionWhenPressed).photo_id);
                        mItemListAdapter.removeItem(positionWhenPressed);
                        Timber.e("remove " + positionWhenPressed);
                    }
                    mRelativeLayout.removeAllViews();
                    mRecyclerView.setAlpha(1);
                }
                return false;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                if (!scrollable) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get position of object in the adapter list
     *
     * @param motionEvent
     * @return
     */
    private int getPositionTouched(MotionEvent motionEvent) {
        View v = mRecyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        return mRecyclerView.getChildAdapterPosition(v);
    }

    /**
     * Helper gesture detector for detecting on photo click
     */
    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            //init menu on long press
            displayMenu(e);
            scrollable = false;
        }
    }
}
