package com.everalbum.navigators;

import android.support.annotation.Nullable;

/**
 * Stateful class that maintains track of which page a {@link Navigator} is on.
 * Must have at least one page, otherwise an exception will be thrown when initializing the navigator.
 */
public interface PageManager {

    /**
     * @return the coordinator of the current page
     */
    @Nullable
    Coordinator currentPage();

    /**
     * @return the coordinator of the next page, or null if no next page
     */
    @Nullable
    Coordinator nextPage();

    /**
     * @return the coordinator of the previous page, or null if no previous page
     */
    @Nullable
    Coordinator previousPage();


    /**
     * Resets state of PageManager. Called when the Navigator is being detached.
     */
    void reset();
}
