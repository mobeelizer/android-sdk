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

import com.mobeelizer.mobile.android.MobeelizerRealConnectionManager.ConnectionException;

interface MobeelizerConnectionManager {

    MobeelizerLoginResponse login();

    String sendSyncAllRequest() throws ConnectionException;

    String sendSyncDiffRequest(final File outputFile) throws ConnectionException;

    boolean waitUntilSyncRequestComplete(final String ticket) throws ConnectionException;

    File getSyncData(final String ticket) throws ConnectionException;

    void confirmTask(final String ticket) throws ConnectionException;

}
