package ir.afraapps.view.bottombar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.TextView;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.annotation.StyleRes;

import static androidx.annotation.Dimension.DP;


class MiscUtils {


  private static final char[] persianDigits = {'۰', '۱', '۲', '۳', '۴', '۵', '۶',
    '۷', '۸', '۹'};


  public static String formatNumber(Object number) {

    if (number == null) {
      return null;
    }

    StringBuilder sb = new StringBuilder();
    String stringNumber = null;

    if (number instanceof String) {
      stringNumber = (String) number;

    } else if (number instanceof Integer) {
      stringNumber = Integer.toString((int) number);

    } else if (number instanceof Long) {
      stringNumber = Long.toString((long) number);

    } else if (number instanceof Float) {
      stringNumber = Float.toString((float) number);

    } else if (number instanceof Double) {
      stringNumber = Double.toString((double) number);
    }

    if (stringNumber == null) {
      return null;
    }

    for (char i : stringNumber.toCharArray()) {
      if (Character.isDigit(i)) {
        sb.append(persianDigits[Integer.parseInt(i + "")]);
      } else {
        sb.append(i);
      }
    }

    return sb.toString();
  }


  @NonNull
  protected static TypedValue getTypedValue(@NonNull Context context, @AttrRes int resId) {
    TypedValue tv = new TypedValue();
    context.getTheme().resolveAttribute(resId, tv, true);
    return tv;
  }

  @ColorInt
  protected static int getColor(@NonNull Context context, @AttrRes int color) {
    return getTypedValue(context, color).data;
  }

  @DrawableRes
  protected static int getDrawableRes(@NonNull Context context, @AttrRes int drawable) {
    return getTypedValue(context, drawable).resourceId;
  }

  /**
   * Converts dps to pixels nicely.
   *
   * @param context the Context for getting the resources
   * @param dp      dimension in dps
   * @return dimension in pixels
   */
  protected static int dpToPixel(@NonNull Context context, @Dimension(unit = DP) float dp) {
    Resources resources = context.getResources();
    DisplayMetrics metrics = resources.getDisplayMetrics();

    try {
      return (int) (dp * metrics.density);
    } catch (NoSuchFieldError ignored) {
      return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
  }

  /**
   * Converts pixels to dps just as well.
   *
   * @param context the Context for getting the resources
   * @param px      dimension in pixels
   * @return dimension in dps
   */
  protected static int pixelToDp(@NonNull Context context, @Px int px) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return Math.round(px / displayMetrics.density);
  }

  /**
   * Returns screen width.
   *
   * @param context Context to get resources and device specific display metrics
   * @return screen width
   */
  protected static int getScreenWidth(@NonNull Context context) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return (int) (displayMetrics.widthPixels / displayMetrics.density);
  }

  /**
   * A convenience method for setting text appearance.
   *
   * @param textView a TextView which textAppearance to modify.
   * @param resId    a style resource for the text appearance.
   */
  protected static void setTextAppearance(@NonNull TextView textView, @StyleRes int resId) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      textView.setTextAppearance(resId);
    } else {
      textView.setTextAppearance(textView.getContext(), resId);
    }
  }

  /**
   * Determine if the current UI Mode is Night Mode.
   *
   * @param context Context to get the configuration.
   * @return true if the night mode is enabled, otherwise false.
   */
  protected static boolean isNightMode(@NonNull Context context) {
    int currentNightMode = context.getResources().getConfiguration().uiMode
      & Configuration.UI_MODE_NIGHT_MASK;
    return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
  }


  public static String toMD5(String text) {
    MessageDigest mdEnc = null;
    try {
      mdEnc = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      //
    } // Encryption algorithm
    mdEnc.update(text.getBytes(), 0, text.length());
    String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
    while (md5.length() < 32) {
      md5 = "0" + md5;
    }
    return md5;
  }

  public static ColorFilter getColorFilter(int tintMode, int color) {
    if (color == 0) return new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    switch (tintMode) {
      case 1:
        return getGrayscaleFilter();
      case 2:
        return getHueFilter(color);
      case 3:
        return new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_OVER);
      case 5:
        return new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
      case 9:
        return new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
      case 14:
        return new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
      case 15:
        return new PorterDuffColorFilter(color, PorterDuff.Mode.SCREEN);
      case 16:
        return new PorterDuffColorFilter(color, PorterDuff.Mode.ADD);
      default:
        return new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }
  }

  private static ColorMatrixColorFilter getHueFilter(int color) {
    float lr = 0.2126F;
    float lg = 0.7152F;
    float lb = 0.0722F;
    ColorMatrix grayscaleMatrix = new ColorMatrix(new float[]{
      lr, lg, lb, 0.0F, 0.0F,
      lr, lg, lb, 0.0F, 0.0F,
      lr, lg, lb, 0.0F, 0.0F,
      0.0F, 0.0F, 0.0F, 1.0F, 0.0F});

    int dr = Color.red(color);
    int dg = Color.green(color);
    int db = Color.blue(color);
    float drf = (float) dr / 255.0F;
    float dgf = (float) dg / 255.0F;
    float dbf = (float) db / 255.0F;
    ColorMatrix tintMatrix = new ColorMatrix(new float[]{
      drf, 0.0F, 0.0F, 0.0F, 0.0F,
      0.0F, dgf, 0.0F, 0.0F, 0.0F,
      0.0F, 0.0F, dbf, 0.0F, 0.0F,
      0.0F, 0.0F, 0.0F, 1.0F, 0.0F});
    tintMatrix.preConcat(grayscaleMatrix);
    return new ColorMatrixColorFilter(tintMatrix);
  }

  private static ColorMatrixColorFilter getGrayscaleFilter() {
    float lr = 0.33F;
    float lg = 0.33F;
    float lb = 0.33F;
    ColorMatrix grayscaleMatrix = new ColorMatrix(new float[]{
      lr, lg, lb, 0.0F, 0.0F,
      lr, lg, lb, 0.0F, 0.0F,
      lr, lg, lb, 0.0F, 0.0F,
      0.0F, 0.0F, 0.0F, 1.0F, 0.0F});
    return new ColorMatrixColorFilter(grayscaleMatrix);
  }

}
