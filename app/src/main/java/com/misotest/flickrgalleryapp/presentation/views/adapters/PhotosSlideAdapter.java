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
import android.widget.TextView;

import com.misotest.flickrgalleryapp.R;
import com.misotest.flickrgalleryapp.presentation.entity.PhotoPresentationModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

/**
 * PagerAdapter for displaying a list of photos from PhotoGridFragment
 */
public class PhotosSlideAdapter extends PagerAdapter {

    private List<PhotoPresentationModel> presentationModelList = Collections.emptyList();

    /**
     * Add items to viewpager list
     *
     * @param presentationModelList
     */
    public void addPhotos(List<PhotoPresentationModel> presentationModelList) {
        this.presentationModelList = presentationModelList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return presentationModelList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.viewpager_item, container, false);

        final ImageView photoView = (ImageView) itemView.findViewById(R.id.photo_view);

        TextView title = (TextView) itemView.findViewById(R.id.photo_title);

        final View view1 = (View) itemView.findViewById(R.id.one);
        final View view2 = (View) itemView.findViewById(R.id.two);
        final View view3 = (View) itemView.findViewById(R.id.three);
        final View view4 = (View) itemView.findViewById(R.id.four);
        final View view5 = (View) itemView.findViewById(R.id.five);
        final View view6 = (View) itemView.findViewById(R.id.six);

        final PhotoPresentationModel presentationModel = presentationModelList.get(position);

        title.setText(presentationModel.photo_title);

        if (presentationModel.photo_file_path != null && !presentationModel.photo_file_path.isEmpty()) {
            //loading images using picasso with simple callback to get colors with Palette api
            Picasso.with(container.getContext()).load(new File(presentationModel.photo_file_path)).into(photoView, new Callback() {
                @Override
                public void onSuccess() {
                    //get bitmap from view and generate colors for six views
                    onPhotoLoaded(photoView, view1, view2, view3, view4, view5, view6);
                }

                @Override
                public void onError() {
                    Timber.e("Error loading image: " + presentationModel);
                }
            });
        } else {
            //loading images using picasso with simple callback to get colors with Palette api
            Picasso.with(container.getContext()).load(presentationModel.photo_url).into(photoView, new Callback() {
                @Override
                public void onSuccess() {
                    //get bitmap from view and generate colors for six views
                    onPhotoLoaded(photoView, view1, view2, view3, view4, view5, view6);
                }

                @Override
                public void onError() {
                    Timber.e("Error loading image: " + presentationModel);
                }
            });
        }
        container.addView(itemView);

        return itemView;
    }

    /**
     * Generate colours from image and set to view's
     *
     * @param photoView
     * @param view1
     * @param view2
     * @param view3
     * @param view4
     * @param view5
     * @param view6
     */
    private void onPhotoLoaded(ImageView photoView, final View view1, final View view2,
                               final View view3, final View view4, final View view5, final View view6) {
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
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        container.removeView((RelativeLayout) object);
    }
}
