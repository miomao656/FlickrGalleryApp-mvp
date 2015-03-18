package com.misotest.flickrgalleryapp.presentation.views.adapters;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.misotest.flickrgalleryapp.R;
import com.misotest.flickrgalleryapp.presentation.PhotoPresentationModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhotosGridAdapter extends RecyclerView.Adapter<PhotosGridAdapter.ViewHolder> {

    private List<PhotoPresentationModel> itemDomainEntityList = new LinkedList<>();

    private GridActions mGridActions;

    public void addPhotos(List<PhotoPresentationModel> sizeElement) {
        int position = itemDomainEntityList.size();
        for (PhotoPresentationModel element : sizeElement) {
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
        final PhotoPresentationModel itemDomainEntity = itemDomainEntityList.get(position);
        if (itemDomainEntity.file_path != null && !itemDomainEntity.file_path.equals("")) {
            Picasso.with(viewHolder.mItemImage.getContext())
                    .load(new File(itemDomainEntity.file_path))
                    .resize(200, 200)
                    .centerCrop()
                    .noFade()
                    .into(viewHolder.mItemImage, callback(viewHolder));
        } else {
            Picasso.with(viewHolder.mItemImage.getContext())
                    .load(itemDomainEntity.photo_url)
                    .resize(200, 200)
                    .centerCrop()
                    .noFade()
                    .into(viewHolder.mItemImage, callback(viewHolder));
        }
        viewHolder.mItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGridActions.onPhotoClick(position);
            }
        });

        viewHolder.mItemImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                mGridActions.onLongPhotoClick(position, itemDomainEntity, view);
                return true;
            }
        });
    }

    private Callback callback(final ViewHolder viewHolder) {
        return new Callback() {
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
        };
    }

    public interface GridActions {

        void onPhotoClick(int position);

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
