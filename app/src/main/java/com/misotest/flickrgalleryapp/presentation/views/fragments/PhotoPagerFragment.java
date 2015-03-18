package com.misotest.flickrgalleryapp.presentation.views.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.misotest.flickrgalleryapp.R;
import com.misotest.flickrgalleryapp.presentation.PhotoPresentationModel;
import com.misotest.flickrgalleryapp.presentation.animation.DepthPageTransformer;
import com.misotest.flickrgalleryapp.presentation.presenters.PhotosListPresenter;
import com.misotest.flickrgalleryapp.presentation.viewinterfaces.PhotoGridView;
import com.misotest.flickrgalleryapp.presentation.views.adapters.PhotosSlideAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhotoPagerFragment extends Fragment implements View.OnClickListener, PhotoGridView{

    private static final String POSITION = "position";
    public static final String TAG = PhotoPagerFragment.class.getSimpleName();

    @InjectView(R.id.pager)
    ViewPager mPager;
    @InjectView(R.id.toolbar)
    RelativeLayout mToolbar;

    private int position;

    private PhotosSlideAdapter mPhotosSlideAdapter;
    private PhotosListPresenter mPhotoListPresenter;

    public static PhotoPagerFragment newInstance(int position) {
        PhotoPagerFragment fragment = new PhotoPagerFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public PhotoPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPhotoListPresenter = new PhotosListPresenter(this);
        mPhotosSlideAdapter = new PhotosSlideAdapter();
        mPager.setAdapter(mPhotosSlideAdapter);
        mPager.setPageTransformer(false, new DepthPageTransformer());
        mPhotoListPresenter.startPresenting();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        rootView.findViewById(R.id.img_back).setOnClickListener(this);
        rootView.findViewById(R.id.back_text).setOnClickListener(this);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
            case R.id.back_text:
                getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void presentPhotoItems(List<PhotoPresentationModel> itemDomainEntityList) {
        mPhotosSlideAdapter.addUrls(itemDomainEntityList);
        mPager.setCurrentItem(position);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void hideError() {

    }

    @Override
    public Context getContext() {
        return getActivity().getApplicationContext();
    }
}
