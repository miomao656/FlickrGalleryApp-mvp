package com.misotest.flickrgalleryapp.presentation.adapters;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.misotest.flickrgalleryapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhotosGridAdapter extends RecyclerView.Adapter<PhotosGridAdapter.ViewHolder> {

    private List<String> itemDomainEntityList = new LinkedList<>();

    private GridActions mGridActions;

    public void addPhotos(List<String> sizeElement) {
        int position = itemDomainEntityList.size();
        for (String element : sizeElement) {
            itemDomainEntityList.add(element);
        }
        notifyItemRangeChanged(position, sizeElement.size() - 1);
    }

    public PhotosGridAdapter(GridActions mGridActions) {
        this.mGridActions = mGridActions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final String itemDomainEntity = itemDomainEntityList.get(position);
        Picasso.with(viewHolder.mItemImage.getContext())
                .load(new File(itemDomainEntity))
                .resize(200, 200)
                .centerCrop()
                .noFade()
                .into(viewHolder.mItemImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        ObjectAnimator animator = ObjectAnimator.ofFloat(viewHolder.mItemImage, View.ALPHA, 0, 1);
                        animator.setDuration(500);
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                viewHolder.mItemImage.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                        animator.start();
                    }

                    @Override
                    public void onError() {

                    }
                });
        viewHolder.mItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "Clicked on " + position, Toast.LENGTH_SHORT).show();
                ArrayList<String> urls = new ArrayList<String>();
                for (String sizeElement : itemDomainEntityList) {
                    urls.add(sizeElement);
                }
                mGridActions.onPhotoClick(position, urls);
            }
        });

        viewHolder.mItemImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mGridActions.onLongPhotoClick(position, itemDomainEntity, view);
                return true;
            }
        });
    }

    public interface GridActions {

        void onPhotoClick(int position, ArrayList<String> urls);

        void onLongPhotoClick(int position, String url, View view);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return itemDomainEntityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.photo_view)
        ImageView mItemImage;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
