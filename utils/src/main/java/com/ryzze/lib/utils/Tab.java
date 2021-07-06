package com.ryzze.lib.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

class Tab {
    private final BottomBarItem item;
    private final View root;

    private final TextView title;
    private final Context context;
    private final ImageView icon;

    @ColorInt
    private final int activeColor;

    @ColorInt
    private final int inactiveColor;

    private final Drawable iconDrawable;

    Tab(BottomBarItem item, View root, int activeColor, int inactiveColor) {
        this.item = item;
        this.root = root;

        /** pegando id's dos items do bottom bar **/
        context = root.getContext();
        title = (TextView) root.findViewById(R.id.tab_title);
        icon = (ImageView) root.findViewById(R.id.tab_icon);

        this.activeColor = activeColor;
        this.inactiveColor = inactiveColor;
        iconDrawable = item.getIconDrawable(context);


        setupIcon();
        setupTitle();
    }

    /** setando o icone **/
    private void setupIcon() {
        DrawableCompat.setTint(iconDrawable, inactiveColor);
        icon.setImageDrawable(iconDrawable);

    }

    private int getSizeInPx(@DimenRes int res) {
        return context.getResources().getDimensionPixelSize(res);
    }

    /** quando selecionado **/
    void select(boolean animate) {
        title.setTextColor(activeColor);
        DrawableCompat.setTint(iconDrawable, activeColor);
        icon.getDrawable().invalidateSelf();

        // ----- animate ----- //
        if (animate) {
            // null
        } else {
            // null
        }
    }

    /** quando descelecionado **/
    void deselect(boolean animate) {
        title.setTextColor(inactiveColor);
        DrawableCompat.setTint(iconDrawable, inactiveColor);
        icon.getDrawable().invalidateSelf();

        // ----- animate ----- //

        if (animate) {
            // null
        } else {
            // null
        }
    }

    /** setando o titulo **/
    private void setupTitle() {
        if (item.getTitle() == 0) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(item.getTitle());
        }

        title.setTextColor(inactiveColor);
    }

    // ???
    void showBadge(@NonNull Drawable badge) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{iconDrawable, badge});
        layerDrawable.setLayerInset(
                1, iconDrawable.getIntrinsicWidth(),
                0,
                0, iconDrawable.getIntrinsicHeight()
        );
        icon.setImageDrawable(layerDrawable);
    }

    void hideBadge() {
        icon.setImageDrawable(iconDrawable);
    }
}
