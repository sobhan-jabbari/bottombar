package ir.afraapps.view.bottombar;

public interface OnTabSelectListener {
  /**
   * The method being called when currently visible {@link BottomBarTab} changes.
   * <p>
   * This listener is fired for the first time after the items have been set and
   * also after a configuration change, such as when screen orientation changes
   * from portrait to landscape.
   *
   * @param tabId the new visible {@link BottomBarTab}
   */
  void onTabSelected(int tabId);
}
