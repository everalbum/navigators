package com.everalbum.navigators;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

import com.drew.lang.annotations.Nullable;

public abstract class Coordinator {

    private boolean   attached;
    private Navigator navigator;

    @LayoutRes
    public abstract int getLayoutRes();

    /**
     * Set whether or not this coordinator is attached to a view currently.
     * @param attached
     */
    final void setAttached(boolean attached) {
        this.attached = attached;
    }

    /**
     * Set the parent navigator for this coordinator. A {@link Navigator} allows a coordinator
     * to move forward or backward in the navigation.
     * @param component
     */
    final void setNavigator(Navigator component) {
        this.navigator = component;
    }

    /**
     * The parent {@link Navigator} will give its child coordinators a chance to set themselves up
     * with a {@link State}. CoordinatorUtils can gleam information from previous coordinators in the
     * navigation tree this way.
     * </br>
     * This method is called AFTER a coordinator is attached to its view.
     * NOTE: {@link State} is immutable, so coordinators need not worry about modifying the state
     * or changing the data.
     * @param state
     */
    protected void setStartingState(@NonNull State state) {

    }
    /**
     * Called when the view is attached to a Window.
     *
     * Default implementation does nothing.
     *
     * @see View#onAttachedToWindow()
     */
    public void attach(View view) {

    }

    /**
     * Called when the view is detached from a Window.
     *
     * Default implementation does nothing.
     *
     * @see View#onDetachedFromWindow()
     */
    public void detach(View view) {

    }

    /**
     * A parent {@link Navigator} will allow a coordinator to modify its state when it is being detached.
     * This allows a coordinator a chance to pass information along to the parent navigator or to the
     * next coordinator in the navigation tree.
     * </br>
     * This method is called BEFORE a coordinator is detached.
     * @param state
     * @return an instance of {@link State}. Can be the same one (unmodified), or a new modified version.
     */
    @NonNull
    protected State setEndingState(@NonNull State state) {
        return state;
    }

    /**
     * True from just before attach until just after detach.
     */
    public final boolean isAttached() {
        return attached;
    }

    /**
     * Returns the parent navigator, if there's any.
     * @return
     */
    @Nullable
    public final Navigator getNavigator() {
        return navigator;
    }

}