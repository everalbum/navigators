package com.everalbum.navigators;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

import static com.everalbum.navigators.Navigator.Direction.BACKWARDS;
import static com.everalbum.navigators.Navigator.Direction.FORWARD;


/**
 * Special type of {@link Coordinator} that can have children {@link Coordinator}s and {@link Navigator}s
 * and navigate forwards and backwards between them.
 * <p>
 * A {@link Navigator} is essentially a tree, where {@link Coordinator}s are leaf nodes and {@link Navigator}s
 * are regular nodes. Navigation in this tree happens in a depth-first fashion.
 * <p>
 * Child {@link Navigator}s MUST have a view group with the id set to {@code R.id.navigator_content}
 */
public abstract class Navigator extends Coordinator {
    @Retention(RetentionPolicy.CLASS)
    @IntDef({
            FORWARD,
            BACKWARDS
    })
    public @interface Direction {
        int FORWARD   = 0;
        int BACKWARDS = 1;
    }

    private final PageManager        pageManager;
    private       ViewGroup          viewGroup;
    private       NavigationCallback navigationCallback;

    public Navigator(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    /**
     * Initializes and binds this Navigator to the given view group.
     * @param group
     */
    public void initialize(ViewGroup group) {
        initialize(group, true);
    }

    void initialize(ViewGroup group, boolean bind) {
        this.viewGroup = group;
        if (bind) {
            CoordinatorUtils.bind(viewGroup, this);
        }
        boolean initialized = nextPage();
        if (!initialized) {
            throw new IllegalStateException("Provided page manager needs to have at least one page");
        }
    }

    /**
     * Add a {@link NavigationCallback} to this Navigator. The callback will be invoked whenever
     * the next or previous pages are loaded.
     * <p>
     * Used primarily to know when the last or first pages are reached.
     */
    public final void setNavigationCallback(NavigationCallback navigationCallback) {
        this.navigationCallback = navigationCallback;
    }

    @Override
    @CallSuper
    public void detach(View view) {
        pageManager.reset();
        viewGroup = null;
        navigationCallback = null;
    }

    @Override
    public boolean onBackPress() {
        Coordinator current = pageManager.currentPage();
        if(current != null && current.onBackPress()) {
            // Child is handling back press
            return true;
        }
        return previousPage();
    }

    /**
     * Move to the next page.
     * @return true if handled by a child coordinator or navigator, false if no next page
     */
    public final boolean nextPage() {
        return navigateRecursively(FORWARD);
    }

    /**
     * Move to the previous page.
     * @return true if handled by a child coordinator or navigator, false if no previous page
     */
    public final boolean previousPage() {
        return navigateRecursively(BACKWARDS);
    }

    /**
     * Check all children coordinators and navigators and parent navigators for the next or previous pages.
     */
    private boolean navigateRecursively(@Direction int direction) {
        return checkChildNavigator(direction) || navigate(direction);
    }

    private boolean navigate(@Direction int direction) {
        final Coordinator coordinator = direction == FORWARD ? pageManager.nextPage() : pageManager.previousPage();
        if (coordinator == null) {
            // We can't handle the next or previous pages. Check to see if parent navigator can
            // handle.
            if (getNavigator() != null) {
                return getNavigator().navigate(direction);
            }
            invokeCallback(false, direction);
            return false;
        }
        // Detach current coordinator and view
        View current = viewGroup.getChildAt(0);
        if (current != null) {
            Coordinator c = CoordinatorUtils.getCoordinator(current);
            if(c != null) {
                if(direction == FORWARD) {
                    c.onExitForwards(current);
                } else {
                    c.onExitBackwards(current);
                }
                State state = c.getEndingState(getState());
                setState(state);
            }
            viewGroup.removeView(current);
        }

        // Inflate, attach and bind next view and coordinator
        View v = LayoutInflater.from(viewGroup.getContext())
                               .inflate(coordinator.getLayoutRes(), viewGroup, false);
        coordinator.setState(getState());
        viewGroup.addView(v);
        CoordinatorUtils.bind(v, coordinator, this);
        if(direction == FORWARD) {
            coordinator.onEnter(v);
        } else {
            coordinator.onReenter(v);
        }
        if (coordinator instanceof Navigator) {
            ViewGroup content = (ViewGroup) v.findViewById(R.id.navigator_content);
            if (content == null ) {
                throw new NullPointerException(String.format(Locale.US, "Provided page manager {%s} trying " +
                        "to attach navigator {%s} to view without necessary view group. Please ensure a view group with id {R.id.navigator_content}" +
                        " exists in layout.", pageManager.getClass().getSimpleName(), coordinator.getClass().getSimpleName()));
            }
            ((Navigator) coordinator).initialize(content, false);
        }
        invokeCallback(true, direction);
        return true;
    }

    private void invokeCallback(boolean handled, @Direction int direction) {
        if (navigationCallback != null) {
            if (direction == FORWARD) {
                navigationCallback.onNext(handled);
            } else {
                navigationCallback.onPrevious(handled);
            }
        }
    }

    /**
     * Check to see if current coordinator is actually a navigator. If it is, then it takes priority
     * to handle next/previous page calls.
     */
    private boolean checkChildNavigator(@Direction int direction) {
        final Coordinator coordinator = pageManager.currentPage();
        if (coordinator == null) {
            return false;
        }
        if (coordinator instanceof Navigator) {
            final Navigator navigator = (Navigator) coordinator;
            return direction == FORWARD ? navigator.nextPage() : navigator.previousPage();
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setStatusBarColor(int color) {
        if(viewGroup != null) {
            if(viewGroup.getContext() instanceof Activity) {
                ((Activity) viewGroup.getContext()).getWindow().setStatusBarColor(color);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public int getStatusBarColor() {
        if(viewGroup != null) {
            if(viewGroup.getContext() instanceof Activity) {
                return ((Activity) viewGroup.getContext()).getWindow().getStatusBarColor();
            }
        }
        throw new RuntimeException("Navigator view group was either or parent context was not an activity");
    }

}
