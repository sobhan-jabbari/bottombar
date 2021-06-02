package ir.afraapps.view.bottombar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import androidx.annotation.DrawableRes;
import androidx.core.view.ViewCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;


class BottomBarIconBadge extends AppCompatImageView {
  private int icon;
  private boolean isVisible = false;

  BottomBarIconBadge(Context context) {
    super(context);
  }

  /**
   * Set the unread / new item / whatever icon for this Badge.
   *
   * @param icon the the drawable res id this Badge should show.
   */
  void setIcon(@DrawableRes int icon) {
    this.icon = icon;
    setImageResource(icon);
  }

  /**
   * Get the currently showing icon for this Badge.
   *
   * @return current icon for the Badge.
   */
  int getIcon() {
    return icon;
  }

  /**
   * Shows the badge with a neat little scale animation.
   */
  void show() {
    isVisible = true;
    ViewCompat.animate(this)
      .setDuration(150)
      .alpha(1)
      .scaleX(1)
      .scaleY(1)
      .start();
  }

  /**
   * Hides the badge with a neat little scale animation.
   */
  void hide() {
    isVisible = false;
    ViewCompat.animate(this)
      .setDuration(150)
      .alpha(0)
      .scaleX(0)
      .scaleY(0)
      .start();
  }

  /**
   * Is this badge currently visible?
   *
   * @return true is this badge is visible, otherwise false.
   */
  boolean isVisible() {
    return isVisible;
  }

  void attachToTab(BottomBarTab tab, int backgroundColor) {
    int                    size   = MiscUtils.dpToPixel(getContext(), 12);
    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(size, size);

    setLayoutParams(params);
    setScaleType(ScaleType.CENTER_INSIDE);

    // setColoredCircleBackground(backgroundColor);
    wrapTabAndBadgeInSameContainer(tab);
  }

  void setColoredCircleBackground(int circleColor) {
    int           innerPadding     = MiscUtils.dpToPixel(getContext(), 1);
    ShapeDrawable backgroundCircle = BadgeCircle.make(innerPadding * 3, circleColor);
    // setPadding(innerPadding, innerPadding, innerPadding, innerPadding);
    setBackgroundCompat(backgroundCircle);
  }

  private void wrapTabAndBadgeInSameContainer(final BottomBarTab tab) {
    ViewGroup tabContainer = (ViewGroup) tab.getParent();
    tabContainer.removeView(tab);

    final BadgeContainer badgeContainer = new BadgeContainer(getContext());
    badgeContainer.setLayoutParams(new ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    badgeContainer.addView(tab);
    badgeContainer.addView(this);

    tabContainer.addView(badgeContainer, tab.getIndexInTabContainer());

    badgeContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @SuppressWarnings("deprecation")
      @Override
      public void onGlobalLayout() {
        badgeContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        adjustPositionAndSize(tab);
      }
    });
  }

  void removeFromTab(BottomBarTab tab) {
    BadgeContainer badgeAndTabContainer = (BadgeContainer) getParent();
    ViewGroup      originalTabContainer = (ViewGroup) badgeAndTabContainer.getParent();

    badgeAndTabContainer.removeView(tab);
    originalTabContainer.removeView(badgeAndTabContainer);
    originalTabContainer.addView(tab, tab.getIndexInTabContainer());
  }

  void adjustPositionAndSize(BottomBarTab tab) {
    AppCompatImageView     iconView = tab.getIconView();
    ViewGroup.LayoutParams params   = getLayoutParams();

    int   size    = Math.max(getWidth(), getHeight());
    float xOffset = (float) (iconView.getWidth() / 1.18);

    setX(iconView.getX() + xOffset);
    setTranslationY(12);

    if (params.width != size || params.height != size) {
      params.width = size;
      params.height = size;
      setLayoutParams(params);
    }
  }

  @SuppressWarnings("deprecation")
  private void setBackgroundCompat(Drawable background) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      setBackground(background);
    } else {
      setBackgroundDrawable(background);
    }
  }
}
