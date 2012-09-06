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
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.mobeelizer.java.api.MobeelizerOperationError;
import com.mobeelizer.java.errors.MobeelizerOperationStatus;

interface MobeelizerConnectionManager {

    boolean isNetworkAvailable();

    MobeelizerLoginResponse login();

    MobeelizerOperationStatus<String> sendSyncAllRequest();

    MobeelizerOperationStatus<String> sendSyncDiffRequest(final File outputFile);

    MobeelizerOperationError waitUntilSyncRequestComplete(final String ticket);

    File getSyncData(final String ticket) throws IOException;

    MobeelizerOperationError confirmTask(final String ticket);

    MobeelizerOperationError registerForRemoteNotifications(final String registrationId);

    MobeelizerOperationError unregisterForRemoteNotifications(final String remoteNotificationToken);

    MobeelizerOperationError sendRemoteNotification(final String device, final String group, final List<String> users,
            final Map<String, String> notification);

}
