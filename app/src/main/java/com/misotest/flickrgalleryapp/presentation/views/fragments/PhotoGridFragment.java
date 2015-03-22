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
import com.misotest.flickrgalleryapp.presentation.animation.RecyclerInsetsDecoration;
import com.misotest.flickrgalleryapp.presentation.entity.PhotoPresentationModel;
import com.misotest.flickrgalleryapp.presentation.presenters.PhotosListPresenter;
import com.misotest.flickrgalleryapp.presentation.utils.CommonUtils;
import com.misotest.flickrgalleryapp.presentation.utils.DeviceDimensionsHelper;
import com.misotest.flickrgalleryapp.presentation.utils.FragmentHelper;
import com.misotest.flickrgalleryapp.presentation.viewinterfaces.PhotoGridView;
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
public class PhotoGridFragment extends Fragment implements PhotoGridView,
        PhotosGridAdapter.ViewHolder.GridActions {

    public static final String TAG = PhotoGridFragment.class.getSimpleName();

    @InjectView(R.id.my_recycler_view)
    CustomRecyclerView mMyRecyclerView;
    @InjectView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @InjectView(R.id.long_press_container)
    RelativeLayout mRelativeLayout;

    private GridLayoutManager mGridLayoutManager;
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
        mGridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 4);
        mMyRecyclerView.setLayoutManager(mGridLayoutManager);
        mMyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMyRecyclerView.addItemDecoration(new RecyclerInsetsDecoration(this.getContext()));
        mItemListAdapter = new PhotosGridAdapter(this);
        mMyRecyclerView.setAdapter(mItemListAdapter);
        startPresenter();
        gestureDetector = new GestureDetector(getContext(), new SingleTapConfirm());
        mMyRecyclerView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, final MotionEvent motionEvent) {
                        View v = mMyRecyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                        int position = mMyRecyclerView.getChildAdapterPosition(v);
                        if (gestureDetector.onTouchEvent(motionEvent)) {
                            // single tap
                            onPhotoClick(position);
                            return true;
                        } else {
                            // your code for move and drag

                            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                displayMenu(motionEvent);
                                Timber.d("position" + mMyRecyclerView.getChildAdapterPosition(v));
                                return true;
                            }
                            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                int x = (int) motionEvent.getRawX();
                                int y = (int) motionEvent.getRawY();
                                if (menu_share != null && menu_delete != null) {
                                    if (inViewInBounds(menu_share, x, y)) {
                                        String filePath = mItemListAdapter.getPhoto(position).photo_file_path;
                                        if (filePath != null) {
                                            CommonUtils.shareImage(getActivity(), mItemListAdapter.getPhoto(position).photo_file_path);
                                        }
                                        Timber.e("share " + position);
                                    }

                                    if (inViewInBounds(menu_delete, x, y)) {
                                        mItemListAdapter.removeItem(position);
                                        mPhotoListPresenter.deletePhoto(mItemListAdapter.getPhoto(position).photo_id);
                                        Timber.e("remove " + position);
                                    }
                                    mRelativeLayout.removeAllViews();
                                }
                                return false;
                            }
                        }
                        return false;
                    }
                }
        );
        screenWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());
        screenHeight = DeviceDimensionsHelper.getDisplayHeight(getContext());
    }

    private boolean inViewInBounds(View view, int x, int y) {
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    private void displayMenu(MotionEvent motionEvent) {
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
            menu_share.animate().translationX(100).translationY(100).alpha(1f).setDuration(500);
            menu_delete.animate().translationX(100).translationY(-100).alpha(1f).setDuration(500);
        } else if ((screenHeight / 2) > motionEvent.getY() && (screenWidth / 2) < motionEvent.getX()) {
            //upper right
            menu_share.animate().translationX(-100).translationY(100).alpha(1f).setDuration(500);
            menu_delete.animate().translationX(-100).translationY(-100).alpha(1f).setDuration(500);
        } else if ((screenHeight / 2) < motionEvent.getY() && (screenWidth / 2) > motionEvent.getX()) {
            //lower left
            menu_share.animate().translationX(100).translationY(100).alpha(1f).setDuration(500);
            menu_delete.animate().translationX(100).translationY(-100).alpha(1f).setDuration(500);
        } else if ((screenHeight / 2) < motionEvent.getY() && (screenWidth / 2) < motionEvent.getX()) {
            //lower right
            menu_share.animate().translationX(-100).translationY(100).alpha(1f).setDuration(500);
            menu_delete.animate().translationX(-100).translationY(-100).alpha(1f).setDuration(500);
        }
    }

    private void startPresenter() {
        mPhotoListPresenter = new PhotosListPresenter(this);
        mPhotoListPresenter.setQuery("akita");
        mPhotoListPresenter.startPresenting();
        isPaging = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        mPhotoListPresenter.stop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onPhotoClick(int position) {
        FragmentHelper.prepareAndShowFragment(getActivity(), R.id.fragment_container,
                PhotoPagerFragment.newInstance(position), true, PhotoPagerFragment.TAG);
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
        }
    }

    @Override
    public void hideLoading() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
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

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

    }
}
