// 
// MobeelizerFileService.java
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import android.os.Environment;
import android.util.Log;

import com.mobeelizer.java.sync.MobeelizerInputData;

class MobeelizerFileService {

    private static final String TAG = "mobeelizer:fileservice";

    private final MobeelizerApplication application;

    MobeelizerFileService(final MobeelizerApplication application) {
        this.application = application;
    }

    String addFile(final InputStream stream) {
        String guid = UUID.randomUUID().toString();
        String path = savaFile(guid, stream);

        application.getDatabase().addFile(guid, path);

        return guid;
    }

    void deleteFilesFromSync(final List<String> files) {
        for (String guid : files) {
            Log.i(TAG, "Delete file from sync: " + guid);

            String path = application.getDatabase().getFilePath(guid);

            if (path == null) {
                continue;
            }

            File file = new File(path);

            if (!file.delete()) {
                Log.w(TAG, "Cannot remove file " + file.getAbsolutePath());
            }

            application.getDatabase().deleteFileFromSync(guid);
        }
    }

    void addFilesFromSync(final List<String> files, final MobeelizerInputData inputData) {
        for (String guid : files) {
            if (application.getDatabase().isFileExists(guid)) {
                Log.i(TAG, "Skip existing file from sync: " + guid);
                continue;
            }

            Log.i(TAG, "Add file from sync: " + guid);

            String path = null;
            try {
                path = savaFile(guid, inputData.getFile(guid));
            } catch (IOException e) {
                Log.w(TAG, e.getMessage(), e);
                path = "/unknown";
            }

            application.getDatabase().addFileFromSync(guid, path);
        }
    }

    private String savaFile(final String guid, final InputStream stream) {
        File dir = getStorageDirectory();
        File file = new File(dir, guid);

        FileOutputStream fos = null;
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int read;

            while ((read = stream.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }

            fos.flush();
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.w(TAG, e.getMessage(), e);
                }
            }
        }

        return file.getAbsolutePath();
    }

    private File getStorageDirectory() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mobeelizer"
                + File.separator + application.getApplication() + File.separator + application.getInstance() + File.separator
                + application.getUser() + File.separator);
        dir.mkdirs();
        return dir;
    }

    File getFile(final String guid) {
        return new File(getStorageDirectory(), guid);
    }

}
