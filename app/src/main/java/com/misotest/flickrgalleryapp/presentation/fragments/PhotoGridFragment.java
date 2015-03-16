package com.misotest.flickrgalleryapp.presentation.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.misotest.flickrgalleryapp.R;
import com.misotest.flickrgalleryapp.presentation.adapters.PhotosGridAdapter;
import com.misotest.flickrgalleryapp.presentation.presenters.PhotosListPresenter;
import com.misotest.flickrgalleryapp.presentation.viewinterfaces.PhotoGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */
public class PhotoGridFragment extends Fragment implements PhotoGridView,
        PhotosGridAdapter.GridActions {

    public static final String TAG = PhotoGridFragment.class.getSimpleName();

    @InjectView(R.id.my_recycler_view)
    RecyclerView mMyRecyclerView;
    @InjectView(R.id.progress_bar)
    ProgressBar mProgressBar;

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
        mMyRecyclerView.setAdapter(mItemListAdapter);
        mMyRecyclerView.setLayoutManager(mGridLayoutManager);
        mMyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mPhotoListPresenter = new PhotosListPresenter(this);
        mPhotoListPresenter.setQuery("pussy");
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
    public void onPhotoClick(int position, ArrayList<String> urls) {
        FragmentHelper.prepareAndShowFragment(getActivity(), R.id.fragment_container,
                PhotoPagerFragment.newInstance(position, urls), true, PhotoPagerFragment.TAG);
    }

    @Override
    public void onLongPhotoClick(int position, String url) {
        Toast.makeText(getContext(), "position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showItemsFromDiskUrl(List<String> itemDomainEntityList) {
        if (itemDomainEntityList != null && !itemDomainEntityList.isEmpty()) {
            mItemListAdapter.addPhotos(itemDomainEntityList);
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
