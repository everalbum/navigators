package com.everalbum.navigators;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public abstract class Coordinator {

    private boolean   attached;
    private Navigator navigator;
    private State state = new State();

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
     * with a {@link State}. Coordinators can gleam information from previous coordinators in the
     * navigation tree this way.
     * <p>
     * This method is called BEFORE a coordinator is attached to its view.
     * NOTE: {@link State} is immutable, so coordinators need not worry about modifying the state
     * or changing the data.
     * @param state
     */
    void setState(@NonNull State state) {
        this.state = state;
    }

    /**
     * Callback for when the hardware back button is pressed.
     * When returning true for this method, be sure to eventually call {@link Navigator#previousPage()}
     *
     * @return true if this instance will handle the back press, or false if it would like
     *         the parent to handle it.
     */
    public boolean onBackPress() {
        return false;
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
     * Callback for when a coordinator is being added via forward navigation (i.e. it is entering)
     *
     * Allows Coordinator to animate its views in in a proper fashion
     *
     * Called after {@link #attach(View)}
     *
     * @param view The view associated with this coordinator
     */
    public void onEnter(View view) {

    }

    /**
     * Callback for when a coordinator is being added via backward navigation (i.e. it is
     * reentering)
     *
     * Allows Coordinator to animate its views in in a proper fashion
     *
     * Called after {@link #attach(View)}
     *
     * @param view The view associated with this coordinator
     */
    public void onReenter(View view) {

    }

    /**
     * Callback for when a coordinator is being removed via forward navigation.
     *
     * Allows Coordinator to animate its views out in a proper fashion
     *
     * Called before {@link #detach(View)}
     *
     * @param view The view associated with this coordinator
     */
    public void onExitForwards(View view) {

    }

    /**
     * Callback for when a coordinator is being removed via backward navigation.
     *
     * Allows Coordinator to animate its views out in a proper fashion
     *
     * Called before {@link #detach(View)}
     *
     * @param view The view associated with this coordinator
     */
    public void onExitBackwards(View view) {

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
     * <p>
     * This method is called BEFORE a coordinator is detached.
     * @param state
     * @return an instance of {@link State}. Can be the same one (unmodified), or a new modified version.
     */
    @NonNull
    protected State getEndingState(@NonNull State state) {
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

    public final State getState() {
        return state;
    }

}