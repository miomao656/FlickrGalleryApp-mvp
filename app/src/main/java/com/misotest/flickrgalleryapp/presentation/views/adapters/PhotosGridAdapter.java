package com.misotest.flickrgalleryapp.presentation.views.adapters;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    final Handler handler = new Handler();
    Runnable mLongPressed = new Runnable() {
        public void run() {
            Log.i("", "Long press!");
//            mGridActions.onLongPhotoClick();
        }
    };
    private List<PhotoPresentationModel> presentationModelList = new LinkedList<>();
    private GridActions mGridActions;

    /**
     * Constructor including callback for onclick events
     *
     * @param mGridActions
     */
    public PhotosGridAdapter(GridActions mGridActions) {
        this.mGridActions = mGridActions;
    }

    /**
     * Add photo list to adapter and refresh added views
     *
     * @param presentationModels
     */
    public void addPhotos(List<PhotoPresentationModel> presentationModels, boolean isPaging) {
        if (isPaging) {
            int position = presentationModelList.size();
            for (PhotoPresentationModel element : presentationModels) {
                presentationModelList.add(element);
            }
            notifyItemRangeChanged(position, presentationModels.size());
        } else {
            presentationModelList.clear();
            for (PhotoPresentationModel element : presentationModels) {
                presentationModelList.add(element);
            }
            notifyDataSetChanged();
        }
    }

    /**
     * Update photo list view new data and refresh views
     *
     * @param presentationModels
     */
    public void updatePhotos(List<PhotoPresentationModel> presentationModels) {
        presentationModelList.clear();
        for (PhotoPresentationModel model : presentationModels) {
            presentationModelList.add(model);
        }
        notifyItemRangeChanged(presentationModelList.size() - 1, presentationModelList.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final PhotoPresentationModel itemDomainEntity = presentationModelList.get(position);
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
//        viewHolder.mItemImage.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                Timber.d((view.getLeft() + motionEvent.getX()) + "," + (view.getTop() + motionEvent.getY()));
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    handler.postDelayed(mLongPressed, 1000);
//                }
//                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    handler.removeCallbacks(mLongPressed);
//                }
//                return false;
//            }
//        });
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
                ObjectAnimator animator = ObjectAnimator.ofFloat(viewHolder.mItemImage, View.ALPHA, 0, 1);
                animator.setDuration(400);
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

    /**
     * Update photo at photo downloaded to device
     *
     * @param presentationModel
     */
    public void updatePhoto(PhotoPresentationModel presentationModel) {
        PhotoPresentationModel photoPresentationModel = null;
        for (PhotoPresentationModel model : presentationModelList) {
            if (model.photo_id.equals(presentationModel.photo_id)) {
                model.photo_file_path = presentationModel.photo_file_path;
                photoPresentationModel = model;
            }
        }
        if (photoPresentationModel != null) {
            notifyItemChanged(presentationModelList.indexOf(photoPresentationModel));
        }
    }

    /**
     * Delete photo with photo_id at removed position and refresh view
     *
     * @param photo_id
     * @param removedPosition
     */
    public void deletedPhoto(String photo_id, int removedPosition) {
        if (presentationModelList.get(removedPosition).photo_id.equals(photo_id)) {
            presentationModelList.remove(removedPosition);
        }
        notifyItemRemoved(removedPosition);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return presentationModelList.size();
    }

    /**
     * Callback for on click and on long click events for recycler adapter
     */
    public interface GridActions {

        void onPhotoClick(int position);

        void onLongPhotoClick(int position, PhotoPresentationModel presentationModel, View view);
    }

    /**
     * Class representing view holder for recycler view list elements
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.photo_view)
        ImageView mItemImage;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            mItemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
