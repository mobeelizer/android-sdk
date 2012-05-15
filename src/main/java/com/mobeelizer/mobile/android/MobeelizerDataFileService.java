// 
// MobeelizerDataFileService.java
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

import com.mobeelizer.java.sync.MobeelizerInputData;
import com.mobeelizer.java.sync.MobeelizerJsonEntity;
import com.mobeelizer.java.sync.MobeelizerOutputData;

class MobeelizerDataFileService {

    private static final String TAG = "mobeelizer:datafileservice";

    private final MobeelizerApplication application;

    MobeelizerDataFileService(final MobeelizerApplication application) {
        this.application = application;
    }

    boolean processInputFile(final File inputFile, final boolean isAllSynchronization) {
        MobeelizerInputData inputData = null;

        try {
            inputData = new MobeelizerInputData(new FileInputStream(inputFile), File.createTempFile("sync", "input", application
                    .getContext().getDir("sync", Context.MODE_PRIVATE)));

            application.getFileService().addFilesFromSync(inputData.getFiles(), inputData);

            boolean isSuccessful = application.getDatabase().updateEntitiesFromSync(inputData.getInputData().iterator(),
                    isAllSynchronization);

            if (!isSuccessful) {
                return false;
            }

            application.getFileService().deleteFilesFromSync(inputData.getDeletedFiles());

            return true;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            if (inputData != null) {
                inputData.close();
            }
        }
    }

    boolean prepareOutputFile(final File outputFile) {
        MobeelizerOutputData outputData = null;
        MobeelizerSyncIterator iterator = null;
        MobeelizerSyncFileIterator fileIterator = null;

        try {
            outputData = new MobeelizerOutputData(outputFile, File.createTempFile("sync", "output", application.getContext()
                    .getDir("sync", Context.MODE_PRIVATE)));

            iterator = application.getDatabase().getEntitiesToSync();

            while (iterator.hasNext()) {
                MobeelizerJsonEntity next = iterator.next();
                Log.i(TAG, "Add entity to sync: " + next.toString());
                outputData.writeEntity(next);
            }

            fileIterator = application.getDatabase().getFilesToSync();

            while (fileIterator.hasNext()) {
                String guid = fileIterator.next();
                InputStream stream = fileIterator.getStream();

                if (stream == null) {
                    continue; // TODO V3 external storage was removed?
                }

                outputData.writeFile(guid, stream);
                Log.i(TAG, "Add file to sync: " + guid);
            }

            return true;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            if (iterator != null) {
                iterator.close();
            }
            if (fileIterator != null) {
                fileIterator.close();
            }
            if (outputData != null) {
                outputData.close();
            }
        }
    }

}
