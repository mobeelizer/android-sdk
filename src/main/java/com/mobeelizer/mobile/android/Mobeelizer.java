// 
// Mobeelizer.java
// 
// Copyright (C) 2012 Mobeelizer Ltd. All Rights Reserved.
//
// Mobeelizer SDK is free software; you can redistribute it and/or modify it 
// under the terms of the GNU Affero General Public License as published by 
// the Free Software Foundation; either version 3 of the License, or (at your
// option) any later version.
//
// This program is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
// for more details.
//
// You should have received a copy of the GNU Affero General Public License 
// along with this program; if not, write to the Free Software Foundation, Inc., 
// 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
// 

package com.mobeelizer.mobile.android;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.app.Application;

import com.mobeelizer.mobile.android.api.MobeelizerCommunicationStatus;
import com.mobeelizer.mobile.android.api.MobeelizerDatabase;
import com.mobeelizer.mobile.android.api.MobeelizerFile;
import com.mobeelizer.mobile.android.api.MobeelizerLoginCallback;
import com.mobeelizer.mobile.android.api.MobeelizerLoginStatus;
import com.mobeelizer.mobile.android.api.MobeelizerSyncCallback;
import com.mobeelizer.mobile.android.api.MobeelizerSyncStatus;

/**
 * Entry point to the Mobeelizer application that holds references to the user sessions and the database.<br/>
 * <br/>
 * Usage:<br/>
 * 
 * <pre>
 * {@code
 * // login
 * Mobeelizer.login(&quot;user&quot;, &quot;password&quot;);
 * 
 * // get database
 * MobeelizerDatabase database = Mobeelizer.getDatabase();
 * 
 * // logout
 * Mobeelizer.logout();
 * }
 * </pre>
 * 
 * @since 1.0
 */
public class Mobeelizer extends Application {

    /**
     * Version of Mobeelizer SDK.
     */
    public static final String VERSION = "${project.version}";

    private static MobeelizerApplication instance;

    /**
     * Name of the broadcast that notifies about sync status change.
     * 
     * <pre>
     * {@code
     * BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
     *     public void onReceive(final Context context, final Intent intent) {
     *         // implement me
     *     }
     * }
     * registerReceiver(broadcastReceiver, new IntentFilter(Mobeelizer.BROADCAST_SYNC_STATUS_CHANGE));
     * }
     * </pre>
     * 
     * @see #sync()
     * @see #syncAndWait()
     * @see #syncAll()
     * @see #syncAllAndWait()
     * @see #BROADCAST_SYNC_STATUS_CHANGE_STATUS
     * @since 1.0
     */
    public static final String BROADCAST_SYNC_STATUS_CHANGE = "mobeelizer:sync_change_status";

    /**
     * Name of the bundle element that contains new sync status.
     * 
     * <pre>
     * {@code
     * intent.getExtras().getSerializable(Mobeelizer.BROADCAST_SYNC_STATUS_CHANGE_STATUS);
     * }
     * </pre>
     * 
     * @see #sync()
     * @see #syncAndWait()
     * @see #syncAll()
     * @see #syncAllAndWait()
     * @see #checkSyncStatus()
     * @see #BROADCAST_SYNC_STATUS_CHANGE
     * @since 1.0
     */
    public static final String BROADCAST_SYNC_STATUS_CHANGE_STATUS = "status";

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(new MobeelizerApplication(this));
    }

    static MobeelizerApplication getInstance() {
        return instance;
    }

    static void setInstance(final MobeelizerApplication instance) {
        Mobeelizer.instance = instance;
    }

    @Override
    public void onTerminate() {
        getInstance().logout();
        super.onTerminate();
    }

    /**
     * Create a user session for the given login, password and instance.
     * 
     * @param instance
     *            instance's name
     * @param login
     *            login
     * @param password
     *            password
     * @param callback
     *            callback
     * @see MobeelizerLoginStatus
     * @since 1.0
     */
    public static void login(final String instance, final String login, final String password,
            final MobeelizerLoginCallback callback) {
        getInstance().login(instance, login, password, callback);
    }

    /**
     * Create a user session for the given login, password and instance. This version of method is synchronous and lock the
     * invoker thread. Do not call this method in UI thread.
     * 
     * @param instance
     *            instance's name
     * @param login
     *            login
     * @param password
     *            password
     * @return login status
     * @see MobeelizerLoginStatus
     * @since 1.0
     */
    public static MobeelizerLoginStatus login(final String instance, final String login, final String password) {
        return getInstance().login(instance, login, password);
    }

    /**
     * Create a user session for the given login, password and instance equal to the MOBEELIZER_MODE ("test" or "production").
     * 
     * @param login
     *            login
     * @param password
     *            password
     * @param callback
     *            callback
     * @see MobeelizerLoginStatus
     * @see #login(String, String, String)
     * @since 1.0
     */
    public static void login(final String login, final String password, final MobeelizerLoginCallback callback) {
        getInstance().login(login, password, callback);
    }

    /**
     * Create a user session for the given login, password and instance equal to the MOBEELIZER_MODE ("test" or "production").
     * This version of method is synchronous and lock the invoker thread. Do not call this method in UI thread.
     * 
     * @param login
     *            login
     * @param password
     *            password
     * @return login status
     * @see MobeelizerLoginStatus
     * @see #login(String, String, String)
     * @since 1.0
     */
    public static MobeelizerLoginStatus login(final String login, final String password) {
        return getInstance().login(login, password);
    }

    /**
     * Close the user session.
     * 
     * @since 1.0
     */
    public static void logout() {
        getInstance().logout();
    }

    /**
     * Check if the user session is active.
     * 
     * @return true if user session is active
     * @since 1.0
     */
    public static boolean isLoggedIn() {
        return getInstance().isLoggedIn();
    }

    /**
     * Get the database for the active user session.
     * 
     * @return database
     * @throws IllegalStateException
     *             if user session is not active
     * @since 1.0
     */
    public static MobeelizerDatabase getDatabase() {
        return getInstance().getDatabase();
    }

    /**
     * Start a differential sync. Change of status will be sent through the {@link Mobeelizer#BROADCAST_SYNC_STATUS_CHANGE}.
     * 
     * @param callback
     *            callback
     * @throws IllegalStateException
     *             if user session is not active
     * @since 1.0
     */
    public static void sync(final MobeelizerSyncCallback callback) {
        getInstance().sync(callback);
    }

    /**
     * Start a differential sync. Change of status will be sent through the {@link Mobeelizer#BROADCAST_SYNC_STATUS_CHANGE}. This
     * version of method is synchronous and lock the invoker thread. Do not call this method in UI thread.
     * 
     * @return sync status
     * @throws IllegalStateException
     *             if user session is not active
     * @since 1.0
     */
    public static MobeelizerSyncStatus sync() {
        return getInstance().sync();
    }

    /**
     * Start a full sync. All unsynced data will be lost. Change of status will be sent through the broadcast
     * {@link Mobeelizer#BROADCAST_SYNC_STATUS_CHANGE}.
     * 
     * @param callback
     *            callback
     * @throws IllegalStateException
     *             if user session is not active
     * @since 1.0
     */
    public static void syncAll(final MobeelizerSyncCallback callback) {
        getInstance().syncAll(callback);
    }

    /**
     * Start a full sync. All unsynced data will be lost. Change of status will be sent through the broadcast
     * {@link Mobeelizer#BROADCAST_SYNC_STATUS_CHANGE}. This version of method is synchronous and lock the invoker thread. Do not
     * call this method in UI thread.
     * 
     * @return sync status
     * @throws IllegalStateException
     *             if user session is not active
     * @since 1.0
     */
    public static MobeelizerSyncStatus syncAll() {
        return getInstance().syncAll();
    }

    /**
     * Check and return the status of current sync.
     * 
     * @return sync status
     * @throws IllegalStateException
     *             if user session is not active
     * @since 1.0
     */
    public static MobeelizerSyncStatus checkSyncStatus() {
        return getInstance().checkSyncStatus();
    }

    /**
     * Create a new file with a given name and content. The returned file is ready to use as a field in the entity.
     * 
     * @param name
     *            name
     * @param stream
     *            content
     * @return file
     * @throws IllegalStateException
     *             if user session is not active
     * @since 1.0
     */
    public static MobeelizerFile createFile(final String name, final InputStream stream) {
        return new MobeelizerFileImpl(name, stream);
    }

    /**
     * Create a file with a given name that points to a file with a given guid. Note that there is no new file created. The
     * returned file is ready to use as a field in the entity.
     * 
     * @param name
     *            name
     * @param guid
     *            existing file's guid
     * @return file
     * @throws IllegalStateException
     *             if user session is not active
     * @since 1.0
     */
    public static MobeelizerFile createFile(final String name, final String guid) {
        return new MobeelizerFileImpl(name, guid);
    }

    /**
     * Registers device to receive push notifications.
     * 
     * @param registrationId
     *            obtained c2dm registration id
     * @since 1.0
     */
    public static MobeelizerCommunicationStatus registerForRemoteNotifications(final String registrationId) {
        return getInstance().registerForRemoteNotifications(registrationId);
    }

    /**
     * Sends remote notification to all users on all devices.
     * 
     * @param notification
     *            notification to send
     * @return communication status
     * @since 1.0
     */
    public static MobeelizerCommunicationStatus sendRemoteNotification(final Map<String, String> notification) {
        return getInstance().sendRemoteNotification(null, null, null, notification);
    }

    /**
     * Sends remote notification to all users on specified device.
     * 
     * @param notification
     *            notification to send
     * @param device
     *            device
     * @return communication status
     * @since 1.0
     */
    public MobeelizerCommunicationStatus sendRemoteNotificationToDevice(final Map<String, String> notification,
            final String device) {
        return getInstance().sendRemoteNotification(device, null, null, notification);
    }

    /**
     * Sends remote notification to given users.
     * 
     * @param notification
     *            notification to send
     * @param users
     *            list of users
     * @return communication status
     * @since 1.0
     */
    public static MobeelizerCommunicationStatus sendRemoteNotificationToUsers(final Map<String, String> notification,
            final List<String> users) {
        return getInstance().sendRemoteNotification(null, null, users, notification);
    }

    /**
     * Sends remote notification to given users on specified device.
     * 
     * @param notification
     *            notification to send
     * @param users
     *            list of users
     * @param device
     *            device
     * @return communication status
     * @since 1.0
     */
    public static MobeelizerCommunicationStatus sendRemoteNotificationToUsersOnDevice(final Map<String, String> notification,
            final List<String> users, final String device) {
        return getInstance().sendRemoteNotification(device, null, users, notification);
    }

    /**
     * Sends remote notification to given group.
     * 
     * @param notification
     *            notification to send
     * @param group
     *            group
     * @return communication status
     * @since 1.0
     */
    public static MobeelizerCommunicationStatus sendRemoteNotificationToGroup(final Map<String, String> notification,
            final String group) {
        return getInstance().sendRemoteNotification(null, group, null, notification);
    }

    /**
     * Sends remote notification to given group on specified device.
     * 
     * @param notification
     *            notification to send
     * @param group
     *            group
     * @param device
     *            device
     * @return communication status
     * @since 1.0
     */
    public static MobeelizerCommunicationStatus sendRemoteNotificationToGroupOnDevice(final Map<String, String> notification,
            final String group, final String device) {
        return getInstance().sendRemoteNotification(device, group, null, notification);
    }

    static void setSyncStatus(final MobeelizerSyncStatus status) {
        getInstance().setSyncStatus(status);
    }

}
