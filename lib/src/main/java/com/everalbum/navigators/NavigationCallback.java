package com.everalbum.navigators;

/**
 * Callback for whenever the navigation tree changes.
 */
public interface NavigationCallback {
    /**
     * Called whenever a coordinator is brought on screen via {@link Navigator#nextPage()}
     * @param handled true if a child coordinator or navigator has handled the navigation, false if
     *                the last page was reached.
     */
    void onNext(boolean handled);
    /**
     * Called whenever a coordinator is brought on screen via {@link Navigator#previousPage()} ()}
     * @param handled true if a child coordinator or navigator has handled the navigation, false if
     *                the first page was reached.
     */
    void onPrevious(boolean handled);
}
