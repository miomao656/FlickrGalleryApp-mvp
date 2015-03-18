package com.misotest.flickrgalleryapp.presentation.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.misotest.flickrgalleryapp.R;
import com.misotest.flickrgalleryapp.presentation.PhotoPresentationModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

/**
 * PagerAdapter for displaying a list of photos from PhotoGridFragment
 *
 */
public class PhotosSlideAdapter extends PagerAdapter {

    private List<PhotoPresentationModel> urls = Collections.emptyList();

    public void addUrls(List<PhotoPresentationModel> urls) {
        this.urls = urls;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.viewpager_item, container, false);

        final ImageView photoView = (ImageView) itemView.findViewById(R.id.photo_view);

        final View view1 = (View) itemView.findViewById(R.id.one);
        final View view2 = (View) itemView.findViewById(R.id.two);
        final View view3 = (View) itemView.findViewById(R.id.three);
        final View view4 = (View) itemView.findViewById(R.id.four);
        final View view5 = (View) itemView.findViewById(R.id.five);
        final View view6 = (View) itemView.findViewById(R.id.six);

        if (urls.get(position).file_path!=null) {
            //loading images using picasso with simple callback to get colors with Palette api
            Picasso.with(container.getContext()).load(new File(urls.get(position).file_path)).into(photoView, new Callback() {
                @Override
                public void onSuccess() {
                    //get bitmap from view and generate colors for six views
                    Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();

                    Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            if (palette != null) {
                                //get colors or default color if palete getter null
                                int vibrant = palette.getVibrantColor(0x000000);
                                int vibrantLight = palette.getLightVibrantColor(0x000000);
                                int vibrantDark = palette.getDarkVibrantColor(0x000000);
                                int muted = palette.getMutedColor(0x000000);
                                int mutedLight = palette.getLightMutedColor(0x000000);
                                int mutedDark = palette.getDarkMutedColor(0x000000);
                                //set colors to views
                                view1.setBackgroundColor(vibrant);
                                view2.setBackgroundColor(vibrantLight);
                                view3.setBackgroundColor(vibrantDark);
                                view4.setBackgroundColor(muted);
                                view5.setBackgroundColor(mutedLight);
                                view6.setBackgroundColor(mutedDark);
                            }
                        }
                    });
                }

                @Override
                public void onError() {
                    Timber.e("Error loading image: " + urls.get(position));
                }
            });
        } else {
            //loading images using picasso with simple callback to get colors with Palette api
            Picasso.with(container.getContext()).load(urls.get(position).photo_url).into(photoView, new Callback() {
                @Override
                public void onSuccess() {
                    //get bitmap from view and generate colors for six views
                    Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();

                    Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            if (palette != null) {
                                //get colors or default color if palete getter null
                                int vibrant = palette.getVibrantColor(0x000000);
                                int vibrantLight = palette.getLightVibrantColor(0x000000);
                                int vibrantDark = palette.getDarkVibrantColor(0x000000);
                                int muted = palette.getMutedColor(0x000000);
                                int mutedLight = palette.getLightMutedColor(0x000000);
                                int mutedDark = palette.getDarkMutedColor(0x000000);
                                //set colors to views
                                view1.setBackgroundColor(vibrant);
                                view2.setBackgroundColor(vibrantLight);
                                view3.setBackgroundColor(vibrantDark);
                                view4.setBackgroundColor(muted);
                                view5.setBackgroundColor(mutedLight);
                                view6.setBackgroundColor(mutedDark);
                            }
                        }
                    });
                }

                @Override
                public void onError() {
                    Timber.e("Error loading image: " + urls.get(position));
                }
            });
        }
        container.addView(itemView);

        return itemView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        container.removeView((RelativeLayout) object);
    }
}
