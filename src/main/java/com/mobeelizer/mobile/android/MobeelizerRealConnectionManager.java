// 
// MobeelizerRealConnectionManager.java
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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRouteParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Proxy;
import android.util.Log;

import com.mobeelizer.java.api.MobeelizerMode;
import com.mobeelizer.java.connection.MobeelizerAuthenticateResponse;
import com.mobeelizer.java.connection.MobeelizerConnectionService;
import com.mobeelizer.java.connection.MobeelizerConnectionServiceDelegate;
import com.mobeelizer.java.connection.MobeelizerConnectionServiceImpl;
import com.mobeelizer.mobile.android.api.MobeelizerLoginStatus;

class MobeelizerRealConnectionManager implements MobeelizerConnectionManager {

    private static final String TAG = "mobeelizer:mobeelizerrealconnectionmanager";

    private MobeelizerConnectionService connectionService;

    private final MobeelizerApplication application;

    public MobeelizerRealConnectionManager(final MobeelizerApplication application) {
        this.application = application;
        this.connectionService = new MobeelizerConnectionServiceImpl(new MobeelizerConnectionServiceDelegate() {

            @Override
            public void setProxyIfNecessary(final HttpRequestBase request) {
                MobeelizerRealConnectionManager.this.setProxyIfNecessary(request);
            }

            @Override
            public void logInfo(final String message) {
                Log.i(TAG, message);
            }

            @Override
            public void logDebug(final String message) {
                Log.d(TAG, message);
            }

            @Override
            public boolean isNetworkAvailable() {
                return MobeelizerRealConnectionManager.this.isNetworkAvailable();
            }

            @Override
            public String getVersionDigest() {
                return application.getVersionDigest();
            }

            @Override
            public String getVendor() {
                return application.getVendor();
            }

            @Override
            public String getUser() {
                return application.getUser();
            }

            @Override
            public String getUrl() {
                return application.getUrl();
            }

            @Override
            public String getSdkVersion() {
                return "android-sdk-" + Mobeelizer.VERSION;
            }

            @Override
            public String getPassword() {
                return application.getPassword();
            }

            @Override
            public String getInstance() {
                return application.getInstance();
            }

            @Override
            public String getDeviceIdentifier() {
                return application.getDeviceIdentifier();
            }

            @Override
            public String getDevice() {
                return application.getDevice();
            }

            @Override
            public String getApplication() {
                return application.getApplication();
            }

            @Override
            public MobeelizerMode getMode() {
                return application.getMode();
            }

        });
    }

    @Override
    public MobeelizerLoginResponse login() {
        boolean networkConnected = isNetworkAvailable();

        if (!networkConnected) {
            String[] roleAndInstanceGuid = getRoleAndInstanceGuidFromDatabase(application);

            if (roleAndInstanceGuid[0] == null) {
                Log.e(TAG, "Login failure. Missing connection failure.");
                return new MobeelizerLoginResponse(MobeelizerLoginStatus.MISSING_CONNECTION_FAILURE);
            } else {
                Log.i(TAG, "Login '" + application.getUser() + "' from database successful.");
                return new MobeelizerLoginResponse(MobeelizerLoginStatus.OK, roleAndInstanceGuid[1], roleAndInstanceGuid[0],
                        false);
            }
        }

        MobeelizerAuthenticateResponse response = null;

        try {
            if (application.getRemoteNotificationToken() != null) {
                response = connectionService.authenticate(application.getUser(), application.getPassword(),
                        application.getRemoteNotificationToken());
            } else {
                response = connectionService.authenticate(application.getUser(), application.getPassword());
            }
        } catch (IOException e) {
            String[] roleAndInstanceGuid = getRoleAndInstanceGuidFromDatabase(application);

            if (roleAndInstanceGuid[0] == null) {
                return new MobeelizerLoginResponse(MobeelizerLoginStatus.CONNECTION_FAILURE);
            } else {
                return new MobeelizerLoginResponse(MobeelizerLoginStatus.OK, roleAndInstanceGuid[1], roleAndInstanceGuid[0],
                        false);
            }
        }

        if (response != null) {
            boolean initialSyncRequired = isInitialSyncRequired(application, response.getInstanceGuid());

            setRoleAndInstanceGuidInDatabase(application, response.getRole(), response.getInstanceGuid());
            Log.i(TAG, "Login '" + application.getUser() + "' successful.");
            return new MobeelizerLoginResponse(MobeelizerLoginStatus.OK, response.getInstanceGuid(), response.getRole(),
                    initialSyncRequired);
        } else {
            Log.e(TAG, "Login failure. Authentication error.");
            clearRoleAndInstanceGuidInDatabase(application);
            return new MobeelizerLoginResponse(MobeelizerLoginStatus.AUTHENTICATION_FAILURE);
        }
    }

    @Override
    public String sendSyncAllRequest() throws ConnectionException {
        try {
            return connectionService.sendSyncAllRequest();
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage(), e);
        }
    }

    @Override
    public String sendSyncDiffRequest(final File outputFile) throws ConnectionException {
        try {
            return connectionService.sendSyncDiffRequest(outputFile);
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage(), e);
        }
    }

    @Override
    public boolean waitUntilSyncRequestComplete(final String ticket) throws ConnectionException {
        try {
            return connectionService.waitUntilSyncRequestComplete(ticket).isSuccess();
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage(), e);
        }
    }

    @Override
    public File getSyncData(final String ticket) throws ConnectionException {
        try {
            return connectionService.getSyncData(ticket);
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage(), e);
        }
    }

    @Override
    public void confirmTask(final String ticket) throws ConnectionException {
        try {
            connectionService.confirmTask(ticket);
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage(), e);
        }
    }

    @Override
    public void registerForRemoteNotifications(final String token) throws ConnectionException {
        try {
            connectionService.registerForRemoteNotifications(token);
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage(), e);
        }
    }

    @Override
    public void unregisterForRemoteNotifications(final String token) throws ConnectionException {
        try {
            connectionService.unregisterForRemoteNotifications(token);
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage(), e);
        }
    }

    @Override
    public void sendRemoteNotification(final String device, final String group, final List<String> users,
            final Map<String, String> notification) throws ConnectionException {
        try {
            connectionService.sendRemoteNotification(device, group, users, notification);
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage(), e);
        }
    }

    @Override
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);

        if (isConnected(connectivityManager)) {
            return true;
        }

        for (int i = 0; i < 10; i++) {
            if (isConnecting(connectivityManager)) {
                // wait for connection
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.w(TAG, e.getMessage(), e);
                    break;
                }

                if (isConnected(connectivityManager)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isInitialSyncRequired(final MobeelizerApplication application, final String instanceGuid) {
        return application.getInternalDatabase().isInitialSyncRequired(application.getInstance(), instanceGuid,
                application.getUser());
    }

    private String[] getRoleAndInstanceGuidFromDatabase(final MobeelizerApplication application) {
        return application.getInternalDatabase().getRoleAndInstanceGuid(application.getInstance(), application.getUser(),
                application.getPassword());
    }

    private void setRoleAndInstanceGuidInDatabase(final MobeelizerApplication application, final String role,
            final String instanceGuid) {
        application.getInternalDatabase().setRoleAndInstanceGuid(application.getInstance(), application.getUser(),
                application.getPassword(), role, instanceGuid);
    }

    private void clearRoleAndInstanceGuidInDatabase(final MobeelizerApplication application) {
        application.getInternalDatabase().clearRoleAndInstanceGuid(application.getInstance(), application.getUser());
    }

    private boolean isConnecting(final ConnectivityManager connectivityManager) {
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting()
                || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
    }

    private boolean isConnected(final ConnectivityManager connectivityManager) {
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()
                || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
    }

    private void setProxyIfNecessary(final HttpRequestBase request) {
        String proxyHost = Proxy.getHost(application.getContext());
        if (proxyHost == null) {
            return;
        }

        int proxyPort = Proxy.getPort(application.getContext());
        if (proxyPort < 0) {
            return;
        }

        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        ConnRouteParams.setDefaultProxy(request.getParams(), proxy);
    }

    public static class ConnectionException extends Exception {

        private static final long serialVersionUID = 8495472053163912742L;

        public ConnectionException(final String message) {
            super(message);
        }

        public ConnectionException(final String message, final Throwable throwable) {
            super(message, throwable);
        }

    }

}
