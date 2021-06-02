package ir.afraapps.view.bottombar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;


public class BottomBarTab extends LinearLayout {
    @VisibleForTesting
    static final String STATE_BADGE_COUNT = "STATE_BADGE_COUNT_FOR_TAB_";
    static final String STATE_BADGE_ICON = "STATE_BADGE_ICON_FOR_TAB_";

    private static final long ANIMATION_DURATION = 150;

    private final int sixDps;
    private final int sixteenDps;

    @VisibleForTesting
    BottomBarBadge badge;
    BottomBarIconBadge badgeIcon;

    private Type type = Type.FIXED;
    private boolean isTitleless;
    private int iconResId;
    private Drawable iconDrawable;
    private String title;
    private float inActiveAlpha;
    private float activeAlpha;
    private float activeIconScale;
    private float inActiveIconScale;
    private float activeTitleScale;
    private float inActiveTitleScale;
    private float activeIconTranslateY;
    private float inActiveIconTranslateY;
    private int inActiveColor;
    private int activeColor;
    private int tintMode;
    private int barColorWhenSelected;
    private int badgeBackgroundColor;
    private boolean badgeHidesWhenActive;
    private AppCompatImageView iconView;
    private TextView titleView;
    private boolean isActive;
    private int indexInContainer;
    private int titleTextAppearanceResId;
    private Typeface titleTypeFace;
    private boolean tintIcon;
    private boolean isSpace;
    private int itemId;

    public BottomBarTab(Context context) {
        super(context);

        sixDps = MiscUtils.dpToPixel(context, 6);
        sixteenDps = MiscUtils.dpToPixel(context, 16);
    }

    public void setConfig(@NonNull Config config) {
        setInActiveAlpha(config.inActiveTabAlpha);
        setActiveAlpha(config.activeTabAlpha);
        setTintMode(config.tintMode);
        setTintIcon(config.tintIcon);
        if (inActiveColor == 0 && config.inActiveTabColor != -1) {
            setInActiveColor(config.inActiveTabColor);
        }
        if (activeColor == 0 && config.activeTabColor != -1) {
            setActiveColor(config.activeTabColor);
        }
        setBarColorWhenSelected(config.barColorWhenSelected);
        setBadgeBackgroundColor(config.badgeBackgroundColor);
        setBadgeHidesWhenActive(config.badgeHidesWhenSelected);
        setTitleTextAppearance(config.titleTextAppearance);
        setTitleTypeface(config.titleTypeFace);
        setActiveIconScale(config.activeIconScale);
        setInActiveIconScale(config.inActiveIconScale);
        setActiveTitleScale(config.activeTitleScale);
        setInActiveTitleScale(config.inActiveTitleScale);
        setActiveIconTranslateY(config.activeIconTranslateY);
        setInActiveIconTranslateY(config.inActiveIconTranslateY);
    }

    void prepareLayout() {
        inflate(getContext(), getLayoutResource(), this);
        setOrientation(VERTICAL);
        setGravity(isTitleless ? Gravity.CENTER : Gravity.CENTER_HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        setBackgroundResource(MiscUtils.getDrawableRes(getContext(), R.attr.selectableItemBackgroundBorderless));

        iconView = findViewById(R.id.bb_bottom_bar_icon);


        if (iconDrawable != null) {
            iconView.setImageDrawable(iconDrawable);

        } else {
            iconView.setImageResource(iconResId);
        }

        if (type != Type.TABLET && !isTitleless) {
            titleView = findViewById(R.id.bb_bottom_bar_title);
            titleView.setVisibility(VISIBLE);

            if (type == Type.SHIFTING) {
                findViewById(R.id.spacer).setVisibility(VISIBLE);
            }

            updateTitle();
        }

        updateCustomTextAppearance();
        updateCustomTypeface();
    }


    @VisibleForTesting
    int getLayoutResource() {
        int layoutResource;
        switch (type) {
            case FIXED:
                layoutResource = R.layout.bb_bottom_bar_item_fixed;
                break;
            case SHIFTING:
                layoutResource = R.layout.bb_bottom_bar_item_shifting;
                break;
            case TABLET:
                layoutResource = R.layout.bb_bottom_bar_item_fixed_tablet;
                break;
            default:
                // should never happen
                throw new RuntimeException("Unknown BottomBarTab type.");
        }
        return layoutResource;
    }

    private void updateTitle() {
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    private void updateCustomTextAppearance() {
        if (titleView == null || titleTextAppearanceResId == 0) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            titleView.setTextAppearance(titleTextAppearanceResId);
        } else {
            titleView.setTextAppearance(getContext(), titleTextAppearanceResId);
        }

        titleView.setTag(R.id.bb_bottom_bar_appearance_id, titleTextAppearanceResId);
    }

    private void updateCustomTypeface() {
        if (titleTypeFace != null && titleView != null) {
            titleView.setTypeface(titleTypeFace);
        }
    }

    Type getType() {
        return type;
    }

    void setType(Type type) {
        this.type = type;
    }

    public boolean isTitleless() {
        return isTitleless;
    }

    public void setTitleless(boolean isTitleless) {
        if (isTitleless && getIconResId() == 0 && iconDrawable == null) {
            throw new IllegalStateException("This tab is supposed to be " +
                    "icon only, yet it has no icon specified. Index in " +
                    "container: " + getIndexInTabContainer());
        }

        this.isTitleless = isTitleless;
    }

    public ViewGroup getOuterView() {
        return (ViewGroup) getParent();
    }

    AppCompatImageView getIconView() {
        return iconView;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }


    TextView getTitleView() {
        return titleView;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        updateTitle();
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
        setId(itemId);
    }

    public boolean isSpace() {
        return isSpace;
    }

    public void setSpace(boolean space) {
        isSpace = space;
    }

    public float getInActiveAlpha() {
        return inActiveAlpha;
    }

    public void setInActiveAlpha(float inActiveAlpha) {
        this.inActiveAlpha = inActiveAlpha;

        if (!isActive) {
            setAlphas(inActiveAlpha);
        }
    }

    public float getActiveAlpha() {
        return activeAlpha;
    }

    public void setActiveAlpha(float activeAlpha) {
        this.activeAlpha = activeAlpha;

        if (isActive) {
            setAlphas(activeAlpha);
        }
    }

    public void setTintIcon(boolean tintIcon) {
        this.tintIcon = tintIcon;
    }

    public void setTintMode(int tintMode) {
        this.tintMode = tintMode;
    }

    public int getInActiveColor() {
        return inActiveColor;
    }

    public void setInActiveColor(int inActiveColor) {
        this.inActiveColor = inActiveColor;

        if (!isActive) {
            setColors(inActiveColor);
        }
    }

    public int getActiveColor() {
        return activeColor;
    }

    public void setActiveColor(int activeIconColor) {
        this.activeColor = activeIconColor;

        if (isActive) {
            setColors(activeColor);
        }
    }

    public void setActiveIconScale(float activeIconScale) {
        this.activeIconScale = activeIconScale;

        if (isActive) {
            setIconScale(activeIconScale);
        }
    }

    public void setInActiveIconScale(float inActiveIconScale) {
        this.inActiveIconScale = inActiveIconScale;

        if (!isActive) {
            setIconScale(inActiveIconScale);
        }
    }

    public void setActiveTitleScale(float activeTitleScale) {
        this.activeTitleScale = activeTitleScale;

        if (isActive) {
            setTitleScale(activeTitleScale);
        }
    }

    public void setInActiveTitleScale(float inActiveTitleScale) {
        this.inActiveTitleScale = inActiveTitleScale;

        if (!isActive) {
            setTitleScale(inActiveTitleScale);
        }
    }

    public void setActiveIconTranslateY(float activeIconTranslateY) {
        this.activeIconTranslateY = activeIconTranslateY;

        if (isActive) {
            setIconTranslateY(activeIconTranslateY);
        }
    }

    public void setInActiveIconTranslateY(float inActiveIconTranslateY) {
        this.inActiveIconTranslateY = inActiveIconTranslateY;

        if (!isActive) {
            setIconTranslateY(inActiveIconTranslateY);
        }
    }


    public int getBarColorWhenSelected() {
        return barColorWhenSelected;
    }

    public void setBarColorWhenSelected(int barColorWhenSelected) {
        this.barColorWhenSelected = barColorWhenSelected;
    }

    public int getBadgeBackgroundColor() {
        return badgeBackgroundColor;
    }

    public void setBadgeBackgroundColor(int badgeBackgroundColor) {
        this.badgeBackgroundColor = badgeBackgroundColor;

        if (badge != null) {
            badge.setColoredCircleBackground(badgeBackgroundColor);
        }
    }

    public boolean getBadgeHidesWhenActive() {
        return badgeHidesWhenActive;
    }

    public void setBadgeHidesWhenActive(boolean hideWhenActive) {
        this.badgeHidesWhenActive = hideWhenActive;
    }

    int getCurrentDisplayedIconColor() {
        Object tag = iconView.getTag(R.id.bb_bottom_bar_color_id);

        if (tag instanceof Integer) {
            return (int) tag;
        }

        return 0;
    }

    int getCurrentDisplayedTitleColor() {
        if (titleView != null) {
            return titleView.getCurrentTextColor();
        }

        return 0;
    }

    int getCurrentDisplayedTextAppearance() {
        Object tag = titleView.getTag(R.id.bb_bottom_bar_appearance_id);

        if (titleView != null && tag instanceof Integer) {
            return (int) tag;
        }

        return 0;
    }

    public void setBadgeIcon(@DrawableRes int icon) {
        if (icon == 0) {
            if (badgeIcon != null) {
                badgeIcon.removeFromTab(this);
                badgeIcon = null;
            }

            return;
        }

        if (badgeIcon == null) {
            badgeIcon = new BottomBarIconBadge(getContext());
            badgeIcon.attachToTab(this, badgeBackgroundColor);
        }

        badgeIcon.setIcon(icon);

        if (isActive && badgeHidesWhenActive) {
            badgeIcon.hide();
        }
    }


    public void removeBadgeIcon() {
        setBadgeIcon(0);
    }


    public void setBadgeCount(int count) {
        if (count <= 0) {
            if (badge != null) {
                badge.removeFromTab(this);
                badge = null;
            }

            return;
        }

        if (badge == null) {
            badge = new BottomBarBadge(getContext());
            badge.attachToTab(this, badgeBackgroundColor);
        }

        badge.setCount(count);

        if (isActive && badgeHidesWhenActive) {
            badge.hide();
        }
    }


    public void removeBadge() {
        setBadgeCount(0);
    }

    boolean isActive() {
        return isActive;
    }

    boolean hasActiveBadge() {
        return badge != null && badgeIcon != null;
    }

    public int getIndexInTabContainer() {
        return indexInContainer;
    }

    public void setIndexInContainer(int indexInContainer) {
        this.indexInContainer = indexInContainer;
    }

    void setIconTint(int tint) {
        iconView.setColorFilter(tint);
    }

    public int getTitleTextAppearance() {
        return titleTextAppearanceResId;
    }

    void setTitleTextAppearance(int resId) {
        this.titleTextAppearanceResId = resId;
        updateCustomTextAppearance();
    }

    public void setTitleTypeface(Typeface typeface) {
        this.titleTypeFace = typeface;
        updateCustomTypeface();
    }

    public Typeface getTitleTypeFace() {
        return titleTypeFace;
    }


    void select(boolean animate) {
        isActive = true;

        if (animate) {
            animateIcon(activeAlpha, activeIconScale, activeIconTranslateY);
            animateTitle(sixDps, activeTitleScale, activeAlpha);
            animateColors(inActiveColor, activeColor);
        } else {
            setTitleScale(activeTitleScale);
            setTopPadding(sixDps);
            setIconScale(activeIconScale);
            setIconTranslateY(activeIconTranslateY);
            setColors(activeColor);
            setAlphas(activeAlpha);
        }

        setSelected(true);
        /*if (iconView != null) {
            iconView.setSelected(true);
        }*/

        if (badgeHidesWhenActive) {

            if (badge != null) {
                badge.hide();
            }

            if (badgeIcon != null) {
                badgeIcon.hide();
            }
        }

    }

    void deselect(boolean animate) {
        isActive = false;

        boolean isShifting = type == Type.SHIFTING;

        float titleScale = isShifting ? 0f : inActiveTitleScale;
        int iconPaddingTop = isShifting ? sixteenDps : inActiveTitleScale == 0f ? sixDps * 2 : sixDps;

        if (animate) {
            animateTitle(iconPaddingTop, titleScale, inActiveAlpha);
            animateIcon(inActiveAlpha, inActiveIconScale, inActiveIconTranslateY);
            animateColors(activeColor, inActiveColor);
        } else {
            setTitleScale(titleScale);
            setTopPadding(iconPaddingTop);
            setIconScale(inActiveIconScale);
            setIconTranslateY(inActiveIconTranslateY);
            setColors(inActiveColor);
            setAlphas(inActiveAlpha);
        }

        setSelected(false);
        /*if (iconView != null) {
            iconView.setSelected(false);
        }*/

        if (!isShifting) {
            if (badge != null && !badge.isVisible()) {
                badge.show();
            }

            if (badgeIcon != null && !badgeIcon.isVisible()) {
                badgeIcon.show();
            }
        }
    }

    private void animateColors(int previousColor, int color) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(previousColor, color);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(
                valueAnimator -> setColors((Integer) valueAnimator.getAnimatedValue()));

        anim.setDuration(150);
        anim.start();
    }

    private void setColors(int color) {
        if (iconView != null && tintIcon) {
            iconView.setColorFilter(MiscUtils.getColorFilter(tintMode, color));
            iconView.setTag(R.id.bb_bottom_bar_color_id, color);
        }

        if (titleView != null) {
            titleView.setTextColor(color);
        }
    }

    private void setAlphas(float alpha) {
        if (iconView != null) {
            iconView.setAlpha(alpha);
        }

        if (titleView != null) {
            titleView.setAlpha(alpha);
        }
    }

    void updateWidth(float endWidth, boolean animated) {
        if (!animated) {
            getLayoutParams().width = (int) endWidth;

            if (!isActive) {

                if (badge != null) {
                    badge.adjustPositionAndSize(this);
                    badge.show();
                }

                if (badgeIcon != null) {
                    badgeIcon.adjustPositionAndSize(this);
                    badgeIcon.show();
                }
            }
            return;
        }

        float start = getWidth();

        ValueAnimator animator = ValueAnimator.ofFloat(start, endWidth);
        animator.setDuration(150);
        animator.addUpdateListener(animator1 -> {
            ViewGroup.LayoutParams params = getLayoutParams();
            if (params == null) return;

            params.width = Math.round((float) animator1.getAnimatedValue());
            setLayoutParams(params);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isActive) {
                    if (badge != null) {
                        badge.adjustPositionAndSize(BottomBarTab.this);
                        badge.show();
                    }

                    if (badgeIcon != null) {
                        badgeIcon.adjustPositionAndSize(BottomBarTab.this);
                        badgeIcon.show();
                    }
                }
            }
        });
        animator.start();
    }

    private void updateBadgePosition() {
        if (badge != null) {
            badge.adjustPositionAndSize(this);
        }
        if (badgeIcon != null) {
            badgeIcon.adjustPositionAndSize(this);
        }
    }

    private void setTopPaddingAnimated(int start, int end) {
        if (type == Type.TABLET || isTitleless) {
            return;
        }

        ValueAnimator paddingAnimator = ValueAnimator.ofInt(start, end);
        paddingAnimator.addUpdateListener(animation -> iconView.setPadding(
                iconView.getPaddingLeft(),
                (Integer) animation.getAnimatedValue(),
                iconView.getPaddingRight(),
                iconView.getPaddingBottom()
        ));

        paddingAnimator.setDuration(ANIMATION_DURATION);
        paddingAnimator.start();
    }

    private void animateTitle(int padding, float scale, float alpha) {
        if (type == Type.TABLET && isTitleless) {
            return;
        }

        setTopPaddingAnimated(iconView.getPaddingTop(), padding);

        ViewPropertyAnimatorCompat titleAnimator = ViewCompat.animate(titleView)
                .setDuration(ANIMATION_DURATION)
                .scaleX(scale)
                .scaleY(scale);
        titleAnimator.alpha(alpha);
        titleAnimator.start();
    }

    private void animateIconScaleTranslateY(float scale, float translateY) {
        if (iconView == null) return;
        ViewCompat.animate(iconView)
                .setDuration(ANIMATION_DURATION)
                .scaleX(scale)
                .scaleY(scale)
                .translationY(translateY)
                .start();
    }

    private void animateIcon(float alpha, float scale, float translateY) {
        ViewCompat.animate(iconView)
                .setDuration(ANIMATION_DURATION)
                .alpha(alpha)
                .start();

        //if (isTitleless) {
        animateIconScaleTranslateY(scale, translateY);
        //}
    }

    private void setTopPadding(int topPadding) {
        if (type == Type.TABLET || isTitleless) {
            return;
        }

        iconView.setPadding(
                iconView.getPaddingLeft(),
                topPadding,
                iconView.getPaddingRight(),
                iconView.getPaddingBottom()
        );
    }

    private void setTitleScale(float scale) {
        if (titleView == null || type == Type.TABLET || isTitleless) {
            return;
        }

        titleView.setScaleX(scale);
        titleView.setScaleY(scale);
    }

    private void setIconScale(float scale) {
        //if (isTitleless) {
        if (iconView != null) {
            iconView.setScaleX(scale);
            iconView.setScaleY(scale);
        }
        //}
    }

    private void setIconTranslateY(float translateY) {
        if (iconView != null && isTitleless) {
            iconView.setTranslationY(translateY);
        }
    }


    @Override
    public Parcelable onSaveInstanceState() {
        if (badge != null || badgeIcon != null) {
            Bundle bundle = saveState();
            bundle.putParcelable("superstate", super.onSaveInstanceState());

            return bundle;
        }

        return super.onSaveInstanceState();
    }

    @VisibleForTesting
    Bundle saveState() {
        Bundle outState = new Bundle();
        if (badge != null) {
            outState.putInt(STATE_BADGE_COUNT + getIndexInTabContainer(), badge.getCount());
        }

        if (badgeIcon != null) {
            outState.putInt(STATE_BADGE_ICON + getIndexInTabContainer(), badgeIcon.getIcon());
        }

        return outState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            restoreState(bundle);

            state = bundle.getParcelable("superstate");
        }

        super.onRestoreInstanceState(state);
    }

    @VisibleForTesting
    void restoreState(Bundle savedInstanceState) {
        int previousBadgeCount = savedInstanceState.getInt(STATE_BADGE_COUNT + getIndexInTabContainer(), 0);
        setBadgeCount(previousBadgeCount);
        int previousBadgeIcon = savedInstanceState.getInt(STATE_BADGE_ICON + getIndexInTabContainer(), 0);
        setBadgeIcon(previousBadgeIcon);
    }

    public enum Type {
        FIXED, SHIFTING, TABLET
    }

    public static class Config {
        private final float inActiveTabAlpha;
        private final float activeTabAlpha;
        private final float activeIconScale;
        private final float inActiveIconScale;
        private final float activeTitleScale;
        private final float inActiveTitleScale;
        private final float activeIconTranslateY;
        private final float inActiveIconTranslateY;
        private final int inActiveTabColor;
        private final int activeTabColor;
        private final int barColorWhenSelected;
        private final int badgeBackgroundColor;
        private final int titleTextAppearance;
        private final Typeface titleTypeFace;
        private boolean badgeHidesWhenSelected;
        private boolean tintIcon;
        private int tintMode;

        private Config(Builder builder) {
            this.inActiveTabAlpha = builder.inActiveTabAlpha;
            this.activeTabAlpha = builder.activeTabAlpha;
            this.activeIconScale = builder.activeIconScale;
            this.inActiveIconScale = builder.inActiveIconScale;
            this.activeTitleScale = builder.activeTitleScale;
            this.inActiveTitleScale = builder.inActiveTitleScale;
            this.activeIconTranslateY = builder.activeIconTranslateY;
            this.inActiveIconTranslateY = builder.inActiveIconTranslateY;
            this.inActiveTabColor = builder.inActiveTabColor;
            this.activeTabColor = builder.activeTabColor;
            this.barColorWhenSelected = builder.barColorWhenSelected;
            this.badgeBackgroundColor = builder.badgeBackgroundColor;
            this.badgeHidesWhenSelected = builder.hidesBadgeWhenSelected;
            this.titleTextAppearance = builder.titleTextAppearance;
            this.titleTypeFace = builder.titleTypeFace;
            this.tintIcon = builder.tintIcon;
            this.tintMode = builder.tintMode;
        }

        public static class Builder {
            private float inActiveTabAlpha;
            private float activeTabAlpha;
            private float activeIconScale;
            private float inActiveIconScale;
            private float activeTitleScale;
            private float inActiveTitleScale;
            private float activeIconTranslateY;
            private float inActiveIconTranslateY;
            private int inActiveTabColor;
            private int activeTabColor;
            private int barColorWhenSelected;
            private int badgeBackgroundColor;
            private boolean hidesBadgeWhenSelected = true;
            private boolean tintIcon = true;
            private int tintMode;
            private int titleTextAppearance;
            private Typeface titleTypeFace;

            public Builder inActiveTabAlpha(float alpha) {
                this.inActiveTabAlpha = alpha;
                return this;
            }

            public Builder activeTabAlpha(float alpha) {
                this.activeTabAlpha = alpha;
                return this;
            }

            public Builder activeIconScale(float activeIconScale) {
                this.activeIconScale = activeIconScale;
                return this;
            }

            public Builder inActiveIconScale(float inActiveIconScale) {
                this.inActiveIconScale = inActiveIconScale;
                return this;
            }

            public Builder activeTitleScale(float activeTitleScale) {
                this.activeTitleScale = activeTitleScale;
                return this;
            }

            public Builder inActiveTitleScale(float inActiveTitleScale) {
                this.inActiveTitleScale = inActiveTitleScale;
                return this;
            }

            public Builder inActiveIconTranslate(float inActiveIconTranslateY) {
                this.inActiveIconTranslateY = inActiveIconTranslateY;
                return this;
            }

            public Builder activeIconTranslate(float activeIconTranslateY) {
                this.activeIconTranslateY = activeIconTranslateY;
                return this;
            }


            public Builder inActiveTabColor(@ColorInt int color) {
                this.inActiveTabColor = color;
                return this;
            }

            public Builder activeTabColor(@ColorInt int color) {
                this.activeTabColor = color;
                return this;
            }

            public Builder barColorWhenSelected(@ColorInt int color) {
                this.barColorWhenSelected = color;
                return this;
            }

            public Builder badgeBackgroundColor(@ColorInt int color) {
                this.badgeBackgroundColor = color;
                return this;
            }

            public Builder hideBadgeWhenSelected(boolean hide) {
                this.hidesBadgeWhenSelected = hide;
                return this;
            }

            public Builder tintIcon(boolean tint) {
                this.tintIcon = tint;
                return this;
            }

            public Builder tintMode(int tintMode) {
                this.tintMode = tintMode;
                return this;
            }

            public Builder titleTextAppearance(int titleTextAppearance) {
                this.titleTextAppearance = titleTextAppearance;
                return this;
            }

            public Builder titleTypeFace(Typeface titleTypeFace) {
                this.titleTypeFace = titleTypeFace;
                return this;
            }

            public Config build() {
                return new Config(this);
            }
        }
    }
}
