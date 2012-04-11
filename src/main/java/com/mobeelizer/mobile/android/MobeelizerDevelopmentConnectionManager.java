// 
// MobeelizerDevelopmentConnectionManager.java
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

import com.mobeelizer.mobile.android.api.MobeelizerLoginStatus;

class MobeelizerDevelopmentConnectionManager implements MobeelizerConnectionManager {

    private static final String SYNC_IS_NOT_SUPPORTED_IN_DEVELOPMENT_MODE = "Sync is not supported in development mode.";

    private final String developmentRole;

    public MobeelizerDevelopmentConnectionManager(final String developmentRole) {
        this.developmentRole = developmentRole;
    }

    @Override
    public boolean isNetworkAvailable() {
        return false;
    }

    @Override
    public MobeelizerLoginResponse login() {
        return new MobeelizerLoginResponse(MobeelizerLoginStatus.OK, "00000000-0000-0000-0000-000000000000", developmentRole,
                false);
    }

    @Override
    public String sendSyncAllRequest() {
        throw new UnsupportedOperationException(SYNC_IS_NOT_SUPPORTED_IN_DEVELOPMENT_MODE);
    }

    @Override
    public String sendSyncDiffRequest(final File outputFile) {
        throw new UnsupportedOperationException(SYNC_IS_NOT_SUPPORTED_IN_DEVELOPMENT_MODE);
    }

    @Override
    public boolean waitUntilSyncRequestComplete(final String ticket) {
        throw new UnsupportedOperationException(SYNC_IS_NOT_SUPPORTED_IN_DEVELOPMENT_MODE);
    }

    @Override
    public File getSyncData(final String ticket) {
        throw new UnsupportedOperationException(SYNC_IS_NOT_SUPPORTED_IN_DEVELOPMENT_MODE);
    }

    @Override
    public void confirmTask(final String ticket) {
        throw new UnsupportedOperationException(SYNC_IS_NOT_SUPPORTED_IN_DEVELOPMENT_MODE);
    }

}
