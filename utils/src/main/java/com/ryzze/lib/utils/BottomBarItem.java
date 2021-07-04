package com.ryzze.lib.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class BottomBarItem {

    @DrawableRes
    private final int iconId;
    @Nullable
    private final Drawable iconDrawable;
    @StringRes
    private final int title;

    public BottomBarItem(@DrawableRes int iconId, @StringRes int title) {
        this.iconId = iconId;
        this.title = title;
        iconDrawable = null;
        if (iconId == 0) {
            throw new RuntimeException("O ícone deve ser fornecido");
        }
    }

    public BottomBarItem(@NonNull Drawable iconDrawable, @StringRes int titleId) {
        this.iconDrawable = iconDrawable;
        this.title = titleId;
        this.iconId = 0;
    }

    public BottomBarItem(@NonNull Drawable iconDrawable) {
        this(iconDrawable, 0);
    }

    public BottomBarItem(@DrawableRes int iconId) {
        this(iconId, 0);
    }

    @NonNull
    Drawable getIconDrawable(@NonNull Context context) {
        Drawable drawable;
        if (iconDrawable != null) {
            drawable = iconDrawable;
        } else {
            drawable = ContextCompat.getDrawable(context, iconId);
        }
        //wrapped for tinting
        return DrawableCompat.wrap(drawable).mutate();
    }

    @StringRes
    public int getTitle() {
        return title;
    }
}
