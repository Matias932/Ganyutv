package com.miapp.tv;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;

import com.bumptech.glide.Glide;

public class CardPresenter extends Presenter {

    private static final int CARD_WIDTH = 313;
    private static final int CARD_HEIGHT = 176;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Context context = parent.getContext();
        ImageCardView cardView = new ImageCardView(context) {
            @Override
            public void setSelected(boolean selected) {
                // Resalta la card cuando recibe el foco del D-Pad
                setCardBackgroundColor(this, selected
                        ? Color.parseColor("#FFFFFF")
                        : Color.parseColor("#333333"));
                super.setSelected(selected);
            }
        };
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setMainImageAdjustable(true);
        cardView.setInfoVisibility(ImageCardView.CARD_TYPE_INFO_UNDER_WITH_EXTRA);
        setCardBackgroundColor(cardView, Color.parseColor("#333333"));
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Movie movie = (Movie) item;
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setTitleText(movie.titulo);
        cardView.setContentText(movie.descripcion);
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);

        Glide.with(cardView.getContext())
                .load(movie.portada)
                .centerCrop()
                .into(cardView.getMainImageView());
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setMainImage(null);
    }

    private void setCardBackgroundColor(ImageCardView view, int color) {
        view.setBackgroundColor(color);
        view.findViewById(androidx.leanback.R.id.info_field).setBackgroundColor(color);
    }
}
