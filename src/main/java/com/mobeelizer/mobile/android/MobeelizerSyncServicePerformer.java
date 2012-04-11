// 
// MobeelizerSyncServicePerformer.java
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

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mobeelizer.mobile.android.MobeelizerRealConnectionManager.ConnectionException;
import com.mobeelizer.mobile.android.api.MobeelizerSyncStatus;

class MobeelizerSyncServicePerformer {

    private static final String TAG = "mobeelizer:syncserviceperformer";

    private final MobeelizerDataFileService dataFileService;

    private final boolean isAllSynchronization;

    private final MobeelizerApplication application;

    public MobeelizerSyncServicePerformer(final MobeelizerApplication application, final boolean isAllSynchronization) {
        this.application = application;
        this.isAllSynchronization = isAllSynchronization;
        this.dataFileService = new MobeelizerDataFileService(application);
    }

    public MobeelizerSyncStatus sync() {
        if (application.checkSyncStatus() != MobeelizerSyncStatus.STARTED) {
            Log.w(TAG, "Send is already running - skipping.");
            return MobeelizerSyncStatus.NONE;
        }

        MobeelizerDatabaseImpl database = application.getDatabase();
        MobeelizerConnectionManager connectionManager = application.getConnectionManager();

        File outputFile = null;
        File inputFile = null;

        boolean success = false;

        try {
            database.lockModifiedFlag();

            String ticket = null;

            if (isAllSynchronization) {
                Log.i(TAG, "Send sync all request.");
                ticket = connectionManager.sendSyncAllRequest();
            } else {
                outputFile = File.createTempFile("sync", "sync", application.getContext().getDir("sync", Context.MODE_PRIVATE));

                if (!dataFileService.prepareOutputFile(outputFile)) {
                    Log.i(TAG, "Send file haven't been created.");
                    return MobeelizerSyncStatus.FINISHED_WITH_FAILURE;
                }

                changeStatus(MobeelizerSyncStatus.FILE_CREATED);

                Log.i(TAG, "Send sync request.");
                ticket = connectionManager.sendSyncDiffRequest(outputFile);
            }

            Log.i(TAG, "Sync request completed: " + ticket + ".");

            changeStatus(MobeelizerSyncStatus.TASK_CREATED);

            if (!connectionManager.waitUntilSyncRequestComplete(ticket)) {
                return MobeelizerSyncStatus.FINISHED_WITH_FAILURE;
            }

            Log.i(TAG, "Sync process complete with success.");
            changeStatus(MobeelizerSyncStatus.TASK_PERFORMED);

            inputFile = connectionManager.getSyncData(ticket);

            changeStatus(MobeelizerSyncStatus.FILE_RECEIVED);

            success = dataFileService.processInputFile(inputFile, isAllSynchronization);

            if (!success) {
                return MobeelizerSyncStatus.FINISHED_WITH_FAILURE;
            }

            connectionManager.confirmTask(ticket);
            database.clearModifiedFlag();
            application.getInternalDatabase().setInitialSyncAsNotRequired(application.getInstance(), application.getUser());
        } catch (IOException e) {
            success = false;
            Log.e(TAG, e.getMessage(), e);
        } catch (ConnectionException e) {
            success = false;
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (outputFile != null && !outputFile.delete()) {
                Log.w(TAG, "Cannot delete file " + outputFile.getAbsolutePath());
            }
            if (inputFile != null && !inputFile.delete()) {
                Log.w(TAG, "Cannot delete file " + inputFile.getAbsolutePath());
            }
            database.unlockModifiedFlag();
            if (success) {
                changeStatus(MobeelizerSyncStatus.FINISHED_WITH_SUCCESS);
            } else {
                Log.i(TAG, "Sync process complete with failure.");
                changeStatus(MobeelizerSyncStatus.FINISHED_WITH_FAILURE);
            }
        }

        return success ? MobeelizerSyncStatus.FINISHED_WITH_SUCCESS : MobeelizerSyncStatus.FINISHED_WITH_FAILURE;
    }

    public void changeStatus(final MobeelizerSyncStatus status) {
        application.setSyncStatus(status);
        Intent intent = new Intent(Mobeelizer.BROADCAST_SYNC_STATUS_CHANGE);
        intent.putExtra(Mobeelizer.BROADCAST_SYNC_STATUS_CHANGE_STATUS, status);
        application.getContext().sendBroadcast(intent);
    }

}
