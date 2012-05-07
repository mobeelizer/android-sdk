// 
// MobeelizerConnectionManager.java
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
import java.util.List;
import java.util.Map;

import com.mobeelizer.mobile.android.MobeelizerRealConnectionManager.ConnectionException;

interface MobeelizerConnectionManager {

    boolean isNetworkAvailable();

    MobeelizerLoginResponse login();

    String sendSyncAllRequest() throws ConnectionException;

    String sendSyncDiffRequest(final File outputFile) throws ConnectionException;

    boolean waitUntilSyncRequestComplete(final String ticket) throws ConnectionException;

    File getSyncData(final String ticket) throws ConnectionException;

    void confirmTask(final String ticket) throws ConnectionException;

    void registerForRemoteNotifications(final String registrationId) throws ConnectionException;

    void sendRemoteNotification(final String device, final String group, final List<String> users,
            final Map<String, String> notification) throws ConnectionException;

}
