package com.mobeelizer.mobile.android.api;

import com.mobeelizer.mobile.android.Mobeelizer;

/**
 * The status of current sync.
 * 
 * @see Mobeelizer#sync()
 * @see Mobeelizer#sync(MobeelizerSyncCallback)
 * @see Mobeelizer#syncAll()
 * @see Mobeelizer#syncAll(MobeelizerSyncCallback)
 * @since 1.0
 */
public enum MobeelizerSyncStatus {

    /**
     * Sync has not been executed in the existing user session.
     * 
     * @since 1.0
     */
    NONE(false),

    /**
     * Sync is in progress. The file with local changes is being prepared.
     * 
     * @since 1.0
     */
    STARTED(true),

    /**
     * Sync is in progress. The file with local changes has been prepared and now is being transmitted to the cloud.
     * 
     * @since 1.0
     */
    FILE_CREATED(true),

    /**
     * Sync is in progress. The file with local changes has been transmitted to the cloud. Waiting for the cloud to finish
     * processing sync.
     * 
     * @since 1.0
     */
    TASK_CREATED(true),

    /**
     * Sync is in progress. The file with cloud changes has been prepared and now is being transmitted to the device.
     * 
     * @since 1.0
     */
    TASK_PERFORMED(true),

    /**
     * Sync is in progress. The file with cloud changes has been transmitted to the device cloud and now is being inserted into
     * local database.
     * 
     * @since 1.0
     */
    FILE_RECEIVED(true),

    /**
     * Sync has been finished successfully.
     * 
     * @since 1.0
     */
    FINISHED_WITH_SUCCESS(false),

    /**
     * Sync has not been finished successfully. Look for the explanation in the application logs.
     * 
     * @since 1.0
     */
    FINISHED_WITH_FAILURE(false);

    private final boolean running;

    private MobeelizerSyncStatus(final boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

}
