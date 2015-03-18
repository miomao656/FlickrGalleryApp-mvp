package com.misotest.flickrgalleryapp.presentation.views.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.misotest.flickrgalleryapp.R;
import com.misotest.flickrgalleryapp.presentation.PhotoPresentationModel;
import com.misotest.flickrgalleryapp.presentation.views.adapters.PhotosGridAdapter;
import com.misotest.flickrgalleryapp.presentation.animation.RecyclerInsetsDecoration;
import com.misotest.flickrgalleryapp.presentation.presenters.PhotosListPresenter;
import com.misotest.flickrgalleryapp.presentation.viewinterfaces.PhotoGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */
public class PhotoGridFragment extends Fragment implements PhotoGridView, PhotosGridAdapter.GridActions {

    public static final String TAG = PhotoGridFragment.class.getSimpleName();

    @InjectView(R.id.my_recycler_view)
    RecyclerView mMyRecyclerView;
    @InjectView(R.id.progress_bar)
    ProgressBar mProgressBar;
//    @InjectView(R.id.long_press_container)
//    RelativeLayout mRelativeLayout;
//    @InjectView(R.id.btn_delete)
//    ImageButton mImageButtonDelete;
//    @InjectView(R.id.btn_share)
//    ImageButton mImageButtonShare;

    private GridLayoutManager mGridLayoutManager;
    private PhotosGridAdapter mItemListAdapter;
    private PhotosListPresenter mPhotoListPresenter;

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
        mItemListAdapter = new PhotosGridAdapter(this);
        mGridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 4);
        mMyRecyclerView.setLayoutManager(mGridLayoutManager);
        mMyRecyclerView.addItemDecoration(new RecyclerInsetsDecoration(this.getContext()));
        mMyRecyclerView.setAdapter(mItemListAdapter);
        mPhotoListPresenter = new PhotosListPresenter(this);
        mPhotoListPresenter.setQuery("akita");
        mPhotoListPresenter.startPresenting();
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
    public void onLongPhotoClick(int position, String url, View view) {
        Toast.makeText(getContext(), "position: " + position, Toast.LENGTH_SHORT).show();
    }

    private int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    @Override
    public void presentPhotoItems(List<PhotoPresentationModel> photoPresentationModels) {
        if (photoPresentationModels != null && !photoPresentationModels.isEmpty()) {
            mItemListAdapter.addPhotos(photoPresentationModels);
        }
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideError() {

    }

    @Override
    public Context getContext() {
        return getActivity().getApplicationContext();
    }
}
