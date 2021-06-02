package ir.afraapps.view.bottombar;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Color;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.annotation.XmlRes;
import androidx.core.content.ContextCompat;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;


class TabParser {
  private static final String TAB_TAG = "tab";
  private static final int AVG_NUMBER_OF_TABS = 5;
  private static final int COLOR_NOT_SET = -1;
  private static final int RESOURCE_NOT_FOUND = 0;

  @NonNull
  private final Context context;

  @NonNull
  private final BottomBarTab.Config defaultTabConfig;

  @NonNull
  private final XmlResourceParser parser;

  @Nullable
  private List<BottomBarTab> tabs = null;

  TabParser(@NonNull Context context, @NonNull BottomBarTab.Config defaultTabConfig, @XmlRes int tabsXmlResId) {
    this.context = context;
    this.defaultTabConfig = defaultTabConfig;
    this.parser = context.getResources().getXml(tabsXmlResId);
  }

  @CheckResult
  @NonNull
  public List<BottomBarTab> parseTabs() {
    if (tabs == null) {
      tabs = new ArrayList<>();
      try {
        int eventType;
        do {
          eventType = parser.next();
          if (eventType == XmlResourceParser.START_TAG && TAB_TAG.equals(parser.getName())) {
            BottomBarTab bottomBarTab = parseNewTab(parser, tabs.size());
            tabs.add(bottomBarTab);
          }
        } while (eventType != XmlResourceParser.END_DOCUMENT);
      } catch (IOException | XmlPullParserException e) {
        e.printStackTrace();
        throw new TabParserException();
      }
    }

    return tabs;
  }

  @NonNull
  private BottomBarTab parseNewTab(@NonNull XmlResourceParser parser, @IntRange(from = 0) int containerPosition) {
    BottomBarTab workingTab = tabWithDefaults();
    workingTab.setIndexInContainer(containerPosition);

    final int numberOfAttributes = parser.getAttributeCount();
    for (int i = 0; i < numberOfAttributes; i++) {
      @TabAttribute
      String attrName = parser.getAttributeName(i);
      switch (attrName) {
        case TabAttribute.ID:
          workingTab.setItemId(parser.getIdAttributeResourceValue(i));
          break;
        case TabAttribute.ICON:
          workingTab.setIconResId(parser.getAttributeResourceValue(i, RESOURCE_NOT_FOUND));
          break;
        case TabAttribute.TITLE:
          workingTab.setTitle(getTitleValue(parser, i));
          break;
        case TabAttribute.INACTIVE_COLOR:
          int inactiveColor = getColorValue(parser, i);
          if (inactiveColor == COLOR_NOT_SET) continue;
          workingTab.setInActiveColor(inactiveColor);
          break;
        case TabAttribute.ACTIVE_COLOR:
          int activeColor = getColorValue(parser, i);
          if (activeColor == COLOR_NOT_SET) continue;
          workingTab.setActiveColor(activeColor);
          break;
        case TabAttribute.ACTIVE_ICON_SCALE:
          float activeIconScale = parser.getAttributeFloatValue(i, 1.24f);
          workingTab.setActiveIconScale(activeIconScale);
          break;
        case TabAttribute.INACTIVE_ICON_SCALE:
          float inActiveIconScale = parser.getAttributeFloatValue(i, 1f);
          workingTab.setInActiveIconScale(inActiveIconScale);
          break;
        case TabAttribute.ACTIVE_TITLE_SCALE:
          float activeTitleScale = parser.getAttributeFloatValue(i, 1f);
          workingTab.setActiveTitleScale(activeTitleScale);
          break;
        case TabAttribute.INACTIVE_TITLE_SCALE:
          float inActiveTitleScale = parser.getAttributeFloatValue(i, 0.86f);
          workingTab.setInActiveTitleScale(inActiveTitleScale);
          break;
        case TabAttribute.ACTIVE_ICON_TRANSLATE_Y:
          float activeIconTranslateY = parser.getAttributeFloatValue(i, 0f);
          workingTab.setActiveIconTranslateY(activeIconTranslateY);
          break;
        case TabAttribute.INACTIVE_ICON_TRANSLATE_Y:
          float inActiveIconTranslateY = parser.getAttributeFloatValue(i, 0f);
          workingTab.setInActiveIconTranslateY(inActiveIconTranslateY);
          break;
        case TabAttribute.BAR_COLOR_WHEN_SELECTED:
          int barColorWhenSelected = getColorValue(parser, i);
          if (barColorWhenSelected == COLOR_NOT_SET) continue;
          workingTab.setBarColorWhenSelected(barColorWhenSelected);
          break;
        case TabAttribute.BADGE_BACKGROUND_COLOR:
          int badgeBackgroundColor = getColorValue(parser, i);
          if (badgeBackgroundColor == COLOR_NOT_SET) continue;
          workingTab.setBadgeBackgroundColor(badgeBackgroundColor);
          break;
        case TabAttribute.BADGE_HIDES_WHEN_ACTIVE:
          boolean badgeHidesWhenActive = parser.getAttributeBooleanValue(i, true);
          workingTab.setBadgeHidesWhenActive(badgeHidesWhenActive);
          break;
        case TabAttribute.IS_TITLELESS:
          boolean isTitleless = parser.getAttributeBooleanValue(i, false);
          workingTab.setTitleless(isTitleless);
          break;
        case TabAttribute.IS_SPACE:
          boolean isSpace = parser.getAttributeBooleanValue(i, false);
          workingTab.setSpace(isSpace);
          break;
      }
    }

    return workingTab;
  }

  @NonNull
  private BottomBarTab tabWithDefaults() {
    BottomBarTab tab = new BottomBarTab(context);
    tab.setConfig(defaultTabConfig);

    return tab;
  }

  @NonNull
  private String getTitleValue(@NonNull XmlResourceParser parser, @IntRange(from = 0) int attrIndex) {
    int titleResource = parser.getAttributeResourceValue(attrIndex, 0);
    return titleResource == RESOURCE_NOT_FOUND
      ? parser.getAttributeValue(attrIndex) : context.getString(titleResource);
  }

  @ColorInt
  private int getColorValue(@NonNull XmlResourceParser parser, @IntRange(from = 0) int attrIndex) {
    int colorResource = parser.getAttributeResourceValue(attrIndex, 0);

    if (colorResource == RESOURCE_NOT_FOUND) {
      try {
        String colorValue = parser.getAttributeValue(attrIndex);
        return Color.parseColor(colorValue);
      } catch (Exception ignored) {
        return COLOR_NOT_SET;
      }
    }

    return ContextCompat.getColor(context, colorResource);
  }

  @Retention(RetentionPolicy.SOURCE)
  @StringDef({
    TabAttribute.ID,
    TabAttribute.ICON,
    TabAttribute.TITLE,
    TabAttribute.INACTIVE_COLOR,
    TabAttribute.ACTIVE_COLOR,
    TabAttribute.ACTIVE_ICON_SCALE,
    TabAttribute.INACTIVE_ICON_SCALE,
    TabAttribute.ACTIVE_TITLE_SCALE,
    TabAttribute.INACTIVE_TITLE_SCALE,
    TabAttribute.ACTIVE_ICON_TRANSLATE_Y,
    TabAttribute.INACTIVE_ICON_TRANSLATE_Y,
    TabAttribute.BAR_COLOR_WHEN_SELECTED,
    TabAttribute.BADGE_BACKGROUND_COLOR,
    TabAttribute.BADGE_HIDES_WHEN_ACTIVE,
    TabAttribute.IS_TITLELESS,
    TabAttribute.IS_SPACE
  })
  @interface TabAttribute {
    String ID = "id";
    String ICON = "icon";
    String TITLE = "title";
    String INACTIVE_COLOR = "inActiveColor";
    String ACTIVE_COLOR = "activeColor";
    String ACTIVE_ICON_SCALE = "activeIconScale";
    String INACTIVE_ICON_SCALE = "InActiveIconScale";
    String ACTIVE_TITLE_SCALE = "activeTitleScale";
    String INACTIVE_TITLE_SCALE = "InActiveTitleScale";
    String ACTIVE_ICON_TRANSLATE_Y = "activeIconTranslateY";
    String INACTIVE_ICON_TRANSLATE_Y = "inActiveIconTranslateY";
    String BAR_COLOR_WHEN_SELECTED = "barColorWhenSelected";
    String BADGE_BACKGROUND_COLOR = "badgeBackgroundColor";
    String BADGE_HIDES_WHEN_ACTIVE = "badgeHidesWhenActive";
    String IS_TITLELESS = "iconOnly";
    String IS_SPACE = "isSpace";
  }

  @SuppressWarnings("WeakerAccess")
  public static class TabParserException extends RuntimeException {
    // This class is just to be able to have a type of Runtime Exception that will make it clear where the error originated.
  }
}
