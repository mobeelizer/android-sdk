package com.mobeelizer.mobile.android.api;

/**
 * Listener used to notify about synchronization status change.
 * 
 * @since 1.0
 */
public interface MobeelizerSyncListener {

    /**
     * Method invoked when synchronization status changed.
     * 
     * @param newStatus
     *            sync status
     * @since 1.0
     */
    void onSyncStatusChange(final MobeelizerSyncStatus newStatus);
}
