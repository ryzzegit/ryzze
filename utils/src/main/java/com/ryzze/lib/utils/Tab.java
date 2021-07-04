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

import static android.view.View.GONE;
import static com.ryzze.lib.utils.TabAnimator.animateTranslationY;

class Tab {
    private final BottomBarItem item;
    private final View root;
    private final TextView title;
    private final Context context;
    private final ImageView icon;

    private final int activeTopMargin;
    private final int inactiveTopMargin;
    @ColorInt
    private final int activeColor;
    @ColorInt
    private final int inactiveColor;
    private final Drawable iconDrawable;

    Tab(@NonNull BottomBarItem item, @NonNull View root, @ColorInt int activeColor, @ColorInt int inactiveColor) {
        this.item = item;
        this.root = root;
        context = root.getContext();
        title = (TextView) root.findViewById(R.id.xyz_tab_title);
        icon = (ImageView) root.findViewById(R.id.xyz_tab_icon);

        activeTopMargin = getSizeInPx(R.dimen.bottom_bar_icon_top_margin_active);
        inactiveTopMargin = getSizeInPx(R.dimen.bottom_bar_icon_top_margin_inactive);
        this.activeColor = activeColor;
        this.inactiveColor = inactiveColor;
        iconDrawable = item.getIconDrawable(context);

        setupIcon();
        setupTitle();
    }

    private void setupIcon() {
        DrawableCompat.setTint(iconDrawable, inactiveColor);
        icon.setImageDrawable(iconDrawable);
    }

    private int getSizeInPx(@DimenRes int res) {
        return context.getResources().getDimensionPixelSize(res);
    }

    void select(boolean animate) {
        title.setTextColor(activeColor);
        DrawableCompat.setTint(iconDrawable, activeColor);
        icon.getDrawable().invalidateSelf();

        if (animate) {
            animateTranslationY(root, activeTopMargin);
        } else {
            root.setTranslationY(activeTopMargin);
        }
    }

    void deselect(boolean animate) {
        title.setTextColor(inactiveColor);
        DrawableCompat.setTint(iconDrawable, inactiveColor);
        icon.getDrawable().invalidateSelf();

        if (animate) {
            animateTranslationY(root, inactiveTopMargin);
        } else {
            root.setTranslationY(inactiveTopMargin);
        }
    }

    private void setupTitle() {
        if (item.getTitle() == 0) {
            title.setVisibility(GONE);
        } else {
            title.setText(item.getTitle());
        }
        title.setTextColor(inactiveColor);
    }

    void showBadge(@NonNull Drawable badge) {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{iconDrawable, badge});
        layerDrawable.setLayerInset(
                1,
                iconDrawable.getIntrinsicWidth(),
                0,
                0,
                iconDrawable.getIntrinsicHeight()
        );
        icon.setImageDrawable(layerDrawable);
    }

    void hideBadge() {
        icon.setImageDrawable(iconDrawable);
    }
}