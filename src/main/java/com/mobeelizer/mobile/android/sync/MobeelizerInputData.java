// 
// MobeelizerInputData.java
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

package com.mobeelizer.mobile.android.sync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.util.Log;

public class MobeelizerInputData {

    static final String DATA_ENTRY_NAME = "data";

    static final String DELETED_FILES_ENTRY_NAME = "deletedFiles";

    private static final String TAG = "mobeelizer:inputdata";

    private ZipFile zipFile;

    private File tmpFile;

    public MobeelizerInputData(final InputStream inputStream, final File tmpFile) {
        try {
            this.tmpFile = tmpFile;
            copy(inputStream, tmpFile);
            zipFile = new ZipFile(tmpFile, ZipFile.OPEN_READ);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public InputStream getFile(final String guid) throws IOException {
        ZipEntry fileEntry = zipFile.getEntry(guid);
        if (fileEntry == null) {
            throw new FileNotFoundException("File '" + guid + "' not foud.");
        }
        return zipFile.getInputStream(fileEntry);
    }

    public List<String> getFiles() {
        List<String> result = new LinkedList<String>();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            String entryName = entries.nextElement().getName();
            if (!entryName.equals(DATA_ENTRY_NAME) && !entryName.equals(DELETED_FILES_ENTRY_NAME)) {
                result.add(entryName);
            }
        }
        return result;
    }

    public Iterable<MobeelizerJsonEntity> getInputData() {
        return new MobeelizerInputDataIterable(this);
    }

    public List<String> getDeletedFiles() {
        ZipEntry entry = zipFile.getEntry(DELETED_FILES_ENTRY_NAME);

        if (entry == null) {
            throw new IllegalStateException("Zip entry " + DELETED_FILES_ENTRY_NAME + " hasn't been found");
        }

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)));
            String line;
            List<String> lines = new LinkedList<String>();
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage(), e);
                }
            }
        }
    }

    public InputStream getDataInputStream() {
        ZipEntry entry = zipFile.getEntry(DATA_ENTRY_NAME);

        if (entry == null) {
            throw new IllegalStateException("Zip entry " + DATA_ENTRY_NAME + " hasn't been found");
        }

        try {
            return zipFile.getInputStream(entry);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void close() {
        try {
            if (tmpFile != null && tmpFile.exists() && !tmpFile.delete()) {
                Log.w(TAG, "File '" + tmpFile.getAbsolutePath() + "' cannot be deleted");
            }
            zipFile.close();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void copy(final InputStream is, final File file) throws IOException {
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = is.read(buffer))) {
                os.write(buffer, 0, n);
            }
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

}
