package com.everalbum.navigators;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class CachingPageManager implements PageManager {
    List<Coordinator> cached = new ArrayList<>();
    private int currentPage = -1;

    @Nullable
    @Override
    public Coordinator currentPage() {
        if(withinBounds(currentPage)) {
            return cached.get(currentPage);
        }
        return null;
    }

    @Nullable
    @Override
    public Coordinator nextPage() {
        currentPage++;
        if(withinBounds(currentPage)) {
            return cached.get(currentPage);
        }
        Coordinator c = createCoordinator(currentPage);
        if(c != null) {
            cached.add(currentPage, c);
        }
        return c;
    }

    @Nullable
    @Override
    public Coordinator previousPage() {
        currentPage--;
        if(withinBounds(currentPage)) {
            return cached.get(currentPage);
        }
        Coordinator c = createCoordinator(currentPage);
        if(c != null) {
            cached.add(currentPage, c);
        }
        return c;
    }

    @Override
    @CallSuper
    public void reset() {
        cached.clear();
        currentPage = -1;
    }

    private boolean withinBounds(int page) {
        return page > -1 && page < cached.size();
    }

    /**
     * Called to obtain a {@link Coordinator} for a page.
     * <p>
     * Newly created Coordinators are cached so that navigation is maintained.
     *
     * @return null if we have no more pages
     */
    @Nullable
    protected abstract Coordinator createCoordinator(int pageNumber);
}
