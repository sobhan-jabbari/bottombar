package ir.afraapps.view.bottombar;

public interface TabSelectionInterceptor {
  /**
   * The method being called when currently visible {@link BottomBarTab} is about to change.
   * <p>
   * This listener is fired when the current {@link BottomBar} is about to change. This gives
   * an opportunity to interrupt the {@link BottomBarTab} change.
   *
   * @param oldTabId the currently visible {@link BottomBarTab}
   * @param newTabId the {@link BottomBarTab} that will be switched to
   * @return true if you want to override/stop the tab change, false to continue as normal
   */
  boolean shouldInterceptTabSelection(long oldTabId, long newTabId);
}
