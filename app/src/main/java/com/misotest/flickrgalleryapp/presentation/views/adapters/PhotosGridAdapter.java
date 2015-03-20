package com.misotest.flickrgalleryapp.presentation.views.adapters;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import timber.log.Timber;

/**
 * Adapter for showing photos in grid list view
 */
public class PhotosGridAdapter extends RecyclerView.Adapter<PhotosGridAdapter.ViewHolder> {

    private static final long NOTIFY_DELAY = 500;


    private List<PhotoPresentationModel> presentationModelList = new LinkedList<>();
    private ViewHolder.GridActions mGridActions;

    /**
     * Constructor including callback for onclick events
     *
     * @param mGridActions
     */
    public PhotosGridAdapter(ViewHolder.GridActions mGridActions) {
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
        if (!presentationModelList.isEmpty()) {
            presentationModelList.clear();
            for (PhotoPresentationModel model : presentationModels) {
                presentationModelList.add(model);
            }
            notifyItemRangeChanged(presentationModelList.size() - 1, presentationModelList.size() - 1);
        } else {
            addPhotos(presentationModels, false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list_row, parent, false);
        return new ViewHolder(v, mGridActions);
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
//        for (int i = 0; i < presentationModelList.size() - 1; i++) {
//            if (presentationModelList.get(i))
//        }
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
     * @param position
     */
    public void removeItem(final int position) {
        presentationModelList.remove(position);
        // notify of the removal with a delay so there is a brief pause after returning
        // from the book details screen; this makes the animation more noticeable
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyItemRemoved(position);
            }
        }, NOTIFY_DELAY);
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
     * Get photo object at given position
     *
     * @param position
     * @return
     */
    public PhotoPresentationModel getPhoto(int position) {
        return presentationModelList.get(position);
    }

    /**
     * Class representing view holder for recycler view list elements
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener, View.OnTouchListener {

        private final GridActions listener;
        @InjectView(R.id.photo_view)
        ImageView mItemImage;

        //todo handle long press as a staring point for menu animation
        final Handler handler = new Handler();
        Runnable mLongPressed = new Runnable() {
            public void run() {
                Log.i("", "Long press!");
            }
        };

        public ViewHolder(View view, GridActions listener) {
            super(view);
            ButterKnife.inject(this, view);
            mItemImage.setOnClickListener(this);
            mItemImage.setOnLongClickListener(this);
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onPhotoClick(getPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            return listener != null && listener.onPhotoLongClicked(getPosition());
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            //todo get the positon for drawing the circle
            Timber.d((view.getLeft() + motionEvent.getX()) + "," + (view.getTop() + motionEvent.getY()));
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    handler.postDelayed(mLongPressed, 1000);
//                }
//                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    handler.removeCallbacks(mLongPressed);
//                }
            return false;
        }

        /**
         * Callback for on click and on long click events for recycler adapter
         */
        public interface GridActions {

            public void onPhotoClick(int position);

            public boolean onPhotoLongClicked(int position);
        }
    }
}
