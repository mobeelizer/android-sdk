// 
// MobeelizerApplication.java
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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mobeelizer.mobile.android.MobeelizerRealConnectionManager.ConnectionException;
import com.mobeelizer.mobile.android.api.MobeelizerCommunicationStatus;
import com.mobeelizer.mobile.android.api.MobeelizerLoginCallback;
import com.mobeelizer.mobile.android.api.MobeelizerLoginStatus;
import com.mobeelizer.mobile.android.api.MobeelizerSyncCallback;
import com.mobeelizer.mobile.android.api.MobeelizerSyncStatus;
import com.mobeelizer.mobile.android.definition.MobeelizerApplicationDefinition;
import com.mobeelizer.mobile.android.definition.MobeelizerDefinitionParser;
import com.mobeelizer.mobile.android.model.MobeelizerModelDefinitionImpl;

public class MobeelizerApplication {

    private enum Mode {
        DEVELOPMENT, TEST, PRODUCTION
    }

    private static final String TAG = "mobeelizer";

    private static final String DEFAULT_TEST_URL = "http://cloud.mobeelizer.com/sync";

    private static final String DEFAULT_PRODUCTION_URL = "http://cloud.mobeelizer.com/sync";

    private static final String META_DEVICE = "MOBEELIZER_DEVICE";

    private static final String META_URL = "MOBEELIZER_URL";

    private static final String META_PACKAGE = "MOBEELIZER_PACKAGE";

    private static final String META_DEFINITION_ASSET = "MOBEELIZER_DEFINITION_ASSET";

    private static final String META_DATABASE_VERSION = "MOBEELIZER_DB_VERSION";

    private static final String META_MODE = "MOBEELIZER_MODE";

    private static final String META_DEVELOPMENT_ROLE = "MOBEELIZER_DEVELOPMENT_ROLE";

    private final String vendor;

    private final String application;

    private final String versionDigest;

    private final String device;

    private final String deviceIdentifier;

    private final String entityPackage;

    private final Mobeelizer mobeelizer;

    private final int databaseVersion;

    private final Mode mode;

    private final String developmentRole;

    private String definitionXml;

    private String url;

    private String instance;

    private String user;

    private String role;

    private String instanceGuid;

    private String password;

    private boolean loggedIn = false;

    private String remoteNotificationToken;

    private MobeelizerDatabaseImpl database;

    private final MobeelizerInternalDatabase internalDatabase;

    private MobeelizerApplicationDefinition definition;

    private final MobeelizerDefinitionManager definitionManager = new MobeelizerDefinitionManager();

    private final MobeelizerConnectionManager connectionManager;

    private final MobeelizerFileService fileService;

    private MobeelizerSyncStatus syncStatus = MobeelizerSyncStatus.NONE;

    public MobeelizerApplication(final Mobeelizer mobeelizer) {
        Log.i(TAG, "Creating Mobeelizer SDK " + Mobeelizer.VERSION);

        this.mobeelizer = mobeelizer;
        Mobeelizer.setInstance(this);

        String state = Environment.getExternalStorageState();

        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                throw new IllegalStateException("External storage must be available and read-only.");
            } else {
                throw new IllegalStateException("External storage must be available.");
            }
        }

        Bundle metaData = getMetaData(mobeelizer);

        device = metaData.getString(META_DEVICE);
        entityPackage = metaData.getString(META_PACKAGE);
        definitionXml = metaData.getString(META_DEFINITION_ASSET);
        developmentRole = metaData.getString(META_DEVELOPMENT_ROLE);
        databaseVersion = metaData.getInt(META_DATABASE_VERSION, 1);
        url = metaData.getString(META_URL);

        if (device == null || entityPackage == null) {
            throw new IllegalStateException(META_DEVICE + " and " + META_PACKAGE + " must be set in manifest file.");
        }

        String stringMode = metaData.getString(META_MODE);

        if (stringMode == null) {
            mode = Mode.DEVELOPMENT;
        } else {
            mode = Mode.valueOf(stringMode.toUpperCase(Locale.ENGLISH));
        }

        if (mode == Mode.DEVELOPMENT && developmentRole == null) {
            throw new IllegalStateException(META_DEVELOPMENT_ROLE + " must be set in development mode.");
        }

        if (definitionXml == null) {
            definitionXml = "application.xml";
        }

        if (url == null) {
            if (mode == Mode.PRODUCTION) {
                url = DEFAULT_PRODUCTION_URL;
            } else {
                url = DEFAULT_TEST_URL;
            }
        }

        deviceIdentifier = ((TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

        if (deviceIdentifier == null) {
            throw new IllegalStateException("Could to resolve device identifier.");
        }

        if (mode == Mode.DEVELOPMENT) {
            connectionManager = new MobeelizerDevelopmentConnectionManager(developmentRole);
        } else {
            connectionManager = new MobeelizerRealConnectionManager(this);
        }

        try {
            definition = MobeelizerDefinitionParser.parse(getDefinitionXmlAsset(mobeelizer, definitionXml));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read definition from " + definitionXml + ".", e);
        }

        vendor = definition.getVendor();
        application = definition.getApplication();
        versionDigest = definition.getDigest();

        internalDatabase = new MobeelizerInternalDatabase(this);

        fileService = new MobeelizerFileService(this);
    }

    public void login(final String user, final String password, final MobeelizerLoginCallback callback) {
        login(mode == Mode.PRODUCTION ? "production" : "test", user, password, callback);
    }

    public MobeelizerLoginStatus login(final String user, final String password) {
        return login(mode == Mode.PRODUCTION ? "production" : "test", user, password);
    }

    void login(final String instance, final String user, final String password, final MobeelizerLoginCallback callback) {
        new AsyncTask<Void, Void, MobeelizerLoginStatus>() {

            @Override
            protected MobeelizerLoginStatus doInBackground(final Void... params) {
                return login(instance, user, password);
            }

            @Override
            protected void onPostExecute(final MobeelizerLoginStatus status) {
                super.onPostExecute(status);
                callback.onLoginFinished(status);
            }

        }.execute();
    }

    MobeelizerLoginStatus login(final String instance, final String user, final String password) {
        if (isLoggedIn()) {
            logout();
        }

        Log.i(TAG, "login: " + vendor + ", " + application + ", " + instance + ", " + user + ", " + password);

        this.instance = instance;
        this.user = user;
        this.password = password;

        MobeelizerLoginResponse status = connectionManager.login();

        Log.i(TAG, "Login result: " + status.getStatus() + ", " + status.getRole() + ", " + status.getInstanceGuid());

        if (status.getStatus() != MobeelizerLoginStatus.OK) {
            this.instance = null;
            this.user = null;
            this.password = null;
            return status.getStatus();
        }

        role = status.getRole();
        instanceGuid = status.getInstanceGuid();

        loggedIn = true;

        Set<MobeelizerModelDefinitionImpl> models = definitionManager.convert(definition, entityPackage, role);

        database = new MobeelizerDatabaseImpl(this, models);
        database.open();

        if (status.isInitialSyncRequired()) {
            sync(true);
        }

        return MobeelizerLoginStatus.OK;
    }

    void logout() {
        if (!isLoggedIn()) {
            return; // ignore
        }

        if (checkSyncStatus().isRunning()) {
            throw new IllegalStateException("Cannot logout when sync is in progress.");
        }

        Log.i(TAG, "logout");

        this.instance = null;
        this.user = null;
        this.password = null;

        if (database != null) {
            database.close();
            database = null;
        }

        loggedIn = false;
    }

    void sync(final MobeelizerSyncCallback callback) {
        checkIfLoggedIn();
        Log.i(TAG, "Start sync service.");
        sync(false, callback);
    }

    MobeelizerSyncStatus sync() {
        checkIfLoggedIn();
        Log.i(TAG, "Truncate data and start sync service.");
        return sync(false);
    }

    void syncAll(final MobeelizerSyncCallback callback) {
        checkIfLoggedIn();
        Log.i(TAG, "Truncate data and start sync service.");
        sync(true, callback);
    }

    MobeelizerSyncStatus syncAll() {
        checkIfLoggedIn();
        Log.i(TAG, "Truncate data and start sync service.");
        return sync(true);
    }

    private MobeelizerSyncStatus sync(final boolean syncAll) {
        if (mode == Mode.DEVELOPMENT || checkSyncStatus().isRunning()) {
            Log.w(TAG, "Sync is already running - skipping.");
            return MobeelizerSyncStatus.NONE;
        }

        if (!connectionManager.isNetworkAvailable()) {
            Log.w(TAG, "Sync cannot be performed - network is not available.");
            setSyncStatus(MobeelizerSyncStatus.FINISHED_WITH_FAILURE);
            return MobeelizerSyncStatus.FINISHED_WITH_FAILURE;
        }

        setSyncStatus(MobeelizerSyncStatus.STARTED);

        return new MobeelizerSyncServicePerformer(Mobeelizer.getInstance(), syncAll).sync();
    }

    private void sync(final boolean syncAll, final MobeelizerSyncCallback callback) {
        new AsyncTask<Void, Void, MobeelizerSyncStatus>() {

            @Override
            protected MobeelizerSyncStatus doInBackground(final Void... params) {
                return sync(syncAll);
            }

            @Override
            protected void onPostExecute(final MobeelizerSyncStatus status) {
                super.onPostExecute(status);
                callback.onSyncFinished(status);
            }

        }.execute();
    }

    MobeelizerConnectionManager getConnectionManager() {
        return connectionManager;
    }

    MobeelizerSyncStatus checkSyncStatus() {
        checkIfLoggedIn();
        Log.i(TAG, "Check sync status.");

        if (mode == Mode.DEVELOPMENT) {
            return MobeelizerSyncStatus.NONE;
        }

        return syncStatus;
    }

    void setSyncStatus(final MobeelizerSyncStatus status) {
        this.syncStatus = status;
    }

    boolean isLoggedIn() {
        return loggedIn;
    }

    MobeelizerCommunicationStatus registerForRemoteNotifications(final String registrationId) {
        try {
            remoteNotificationToken = registrationId;
            if (isLoggedIn()) {
                connectionManager.registerForRemoteNotifications(registrationId);
            }
            return MobeelizerCommunicationStatus.SUCCESS;
        } catch (ConnectionException e) {
            Log.e(TAG, e.getMessage(), e);
            return MobeelizerCommunicationStatus.CONNECTION_FAILURE;
        }
    }

    public MobeelizerCommunicationStatus unregisterForRemoteNotifications() {
        try {
            checkIfLoggedIn();
            connectionManager.unregisterForRemoteNotifications(remoteNotificationToken);
            return MobeelizerCommunicationStatus.SUCCESS;
        } catch (ConnectionException e) {
            Log.e(TAG, e.getMessage(), e);
            return MobeelizerCommunicationStatus.CONNECTION_FAILURE;
        }
    }

    MobeelizerCommunicationStatus sendRemoteNotification(final String device, final String group, final List<String> users,
            final Map<String, String> notification) {
        try {
            connectionManager.sendRemoteNotification(device, group, users, notification);
            return MobeelizerCommunicationStatus.SUCCESS;
        } catch (ConnectionException e) {
            Log.e(TAG, e.getMessage(), e);
            return MobeelizerCommunicationStatus.CONNECTION_FAILURE;
        }
    }

    int getDatabaseVersion() {
        return databaseVersion;
    }

    String getUser() {
        return user;
    }

    String getInstance() {
        return instance;
    }

    MobeelizerDatabaseImpl getDatabase() {
        checkIfLoggedIn();
        return database;
    }

    MobeelizerInternalDatabase getInternalDatabase() {
        return internalDatabase;
    }

    String getVendor() {
        return vendor;
    }

    String getApplication() {
        return application;
    }

    String getVersionDigest() {
        return versionDigest;
    }

    Context getContext() {
        return mobeelizer;
    }

    String getPassword() {
        return password;
    }

    String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    String getDevice() {
        return device;
    }

    String getUrl() {
        return url;
    }

    String getInstanceGuid() {
        return instanceGuid;
    }

    String getRemoteNotificationToken() {
        return remoteNotificationToken;
    }

    MobeelizerFileService getFileService() {
        return fileService;
    }

    MobeelizerApplicationDefinition getDefinition() {
        return definition;
    }

    private void checkIfLoggedIn() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("User is not logged in.");
        }
    }

    private Bundle getMetaData(final Mobeelizer mobeelizer) {
        try {
            return mobeelizer.getPackageManager().getApplicationInfo(mobeelizer.getPackageName(), PackageManager.GET_META_DATA).metaData;
        } catch (NameNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private InputStream getDefinitionXmlAsset(final Mobeelizer mobeelizer, final String definitionXml) throws IOException {
        return mobeelizer.getAssets().open(definitionXml);
    }

}
