package com.misotest.flickrgalleryapp.presentation.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misotest.flickrgalleryapp.R;
import com.misotest.flickrgalleryapp.presentation.adapters.PhotosSlideAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhotoPagerFragment extends Fragment {

    private static final String POSITION = "position";
    private static final String URL_LIST = "param2";
    public static final String TAG = PhotoPagerFragment.class.getSimpleName();

    @InjectView(R.id.pager)
    ViewPager mPager;

    private int position;
    private ArrayList<String> urlList;

    private PhotosSlideAdapter mPhotosSlideAdapter;

    public static PhotoPagerFragment newInstance(int position, ArrayList<String> urls) {
        PhotoPagerFragment fragment = new PhotoPagerFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putStringArrayList(URL_LIST, urls);
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
            urlList = getArguments().getStringArrayList(URL_LIST);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (urlList!=null && !urlList.isEmpty()) {
            mPhotosSlideAdapter = new PhotosSlideAdapter();
            mPhotosSlideAdapter.addUrls(urlList);
            mPager.setAdapter(mPhotosSlideAdapter);
            mPager.setCurrentItem(position);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
