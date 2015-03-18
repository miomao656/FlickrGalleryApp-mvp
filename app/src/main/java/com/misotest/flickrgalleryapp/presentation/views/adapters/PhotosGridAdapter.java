package com.misotest.flickrgalleryapp.presentation.views.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.misotest.flickrgalleryapp.R;
import com.misotest.flickrgalleryapp.presentation.entity.PhotoPresentationModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Adapter for showing photos in grid list view
 */
public class PhotosGridAdapter extends RecyclerView.Adapter<PhotosGridAdapter.ViewHolder> {

    private List<PhotoPresentationModel> itemDomainEntityList = new LinkedList<>();

    private GridActions mGridActions;

    /**
     * Add photo list to adapter and refresh added views
     *
     * @param presentationModels
     */
    public void addPhotos(List<PhotoPresentationModel> presentationModels, boolean isPaging) {
        if (isPaging) {
            int position = itemDomainEntityList.size();
            for (PhotoPresentationModel element : presentationModels) {
                itemDomainEntityList.add(element);
            }
            notifyItemRangeChanged(position, presentationModels.size() - 1);
        } else {
            itemDomainEntityList.clear();
            for (PhotoPresentationModel element : presentationModels) {
                itemDomainEntityList.add(element);
            }
            notifyDataSetChanged();
        }
    }

    public void removePhoto(int position) {
        itemDomainEntityList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemDomainEntityList.size() - 1);
    }

    /**
     * Constructor including callback for onclick events
     *
     * @param mGridActions
     */
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
        if (itemDomainEntity.photo_file_path != null && !itemDomainEntity.photo_file_path.equals("")) {
            Picasso.with(viewHolder.mItemImage.getContext())
                    .load(new File(itemDomainEntity.photo_file_path))
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
                mGridActions.onLongPhotoClick(position, itemDomainEntity, view);
                return true;
            }
        });
    }

    /**
     * Picasso callback for notifying animation to start
     *
     * @param viewHolder
     * @return
     */
    private Callback callback(final ViewHolder viewHolder) {
        return new Callback() {
            @Override
            public void onSuccess() {
                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator animator = ObjectAnimator.ofFloat(viewHolder.mItemImage, View.ALPHA, 0, 1);
//                ObjectAnimator animator2 = ObjectAnimator.ofFloat(viewHolder.mItemImage, "x", 100);
//                TranslateAnimation translateAnimation = new TranslateAnimation(viewHolder.mItemImage.getContext(),)
                animator.setDuration(400);
//                animator2.setDuration(400);
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
//                animatorSet.playTogether(animator, animator2);
//                animatorSet.playTogether(animator, animator2);
//                animatorSet.start();
            }

            @Override
            public void onError() {

            }
        };
    }

    /**
     * Callback for on click and on long click events for recycler adapter
     */
    public interface GridActions {

        void onPhotoClick(int position);

        void onLongPhotoClick(int position, PhotoPresentationModel presentationModel, View view);
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
