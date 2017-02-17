package com.everalbum.navigators;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

final class CoordinatorUtils {
    private CoordinatorUtils() {
    }

    /**
     * Attempts to bind a view to a {@link Coordinator}.
     *
     */
    static void bind(View view, Coordinator coordinator) {
        bind(view, coordinator, null);
    }

    /**
     * Attempts to bind a view to a {@link Coordinator} and sets the navigator as the parent to the
     * coordinator.
     */
    static void bind(View view, Coordinator coordinator, Navigator navigator) {
        View.OnAttachStateChangeListener binding = new Binding(coordinator, navigator);
        view.addOnAttachStateChangeListener(binding);
        // Sometimes we missed the first attach because the child's already been added.
        // Sometimes we didn't. The binding keeps track to avoid double attachment of the Coordinator,
        // and to guard against attachment to two different views simultaneously.
        binding.onViewAttachedToWindow(view);
    }

    @Nullable
    static Coordinator getCoordinator(View view) {
        return (Coordinator) view.getTag(R.id.coordinator);
    }

    /**
     * Helper class to bind a coordinator to a View. See {@link CoordinatorUtils#bind(View, Coordinator)}
     */
    final static class Binding implements View.OnAttachStateChangeListener {
        private final Coordinator coordinator;
        @Nullable private final Navigator navigator;
        private View attached;

        Binding(Coordinator coordinator, @Nullable Navigator navigator) {
            this.coordinator = coordinator;
            this.navigator = navigator;
        }

        @Override public void onViewAttachedToWindow(@NonNull View v) {
            if (v != attached) {
                if (coordinator.isAttached()) {
                    throw new IllegalStateException(
                            "Coordinator " + coordinator.getClass().getSimpleName() + " is already attached to a View");
                }
                coordinator.setNavigator(navigator);
                attached = v;
                coordinator.setAttached(true);
                coordinator.attach(attached);
                attached.setTag(R.id.coordinator, coordinator);
            }
        }

        @Override public void onViewDetachedFromWindow(@NonNull View v) {
            if (v == attached) {
                coordinator.detach(attached);
                coordinator.setAttached(false);
                coordinator.setNavigator(null);
                // Happens if rapidly moving backwards and finishing an activity
                if(attached != null) {
                    attached.setTag(R.id.coordinator, null);
                    attached.removeOnAttachStateChangeListener(this);
                }
                attached = null;
            }
        }
    }
}