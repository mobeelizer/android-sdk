// 
// MobeelizerOutputData.java
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

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONException;

import android.util.Log;

public class MobeelizerOutputData {

    private static final String TAG = "mobeelizer:outputdata";

    private final File dataFile;

    private ZipOutputStream zip;

    private OutputStream dataOutputStream;

    private final List<String> deletedFiles;

    public MobeelizerOutputData(final File file, final File tmpFile) {
        try {
            zip = new ZipOutputStream(new FileOutputStream(file));
            dataFile = tmpFile;
            dataOutputStream = new BufferedOutputStream(new FileOutputStream(dataFile));
            deletedFiles = new LinkedList<String>();
        } catch (IOException e) {
            closeQuietly(zip);
            closeQuietly(dataOutputStream);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void writeEntity(final MobeelizerJsonEntity jsonEntity) {
        try {
            dataOutputStream.write(jsonEntity.getJson().getBytes("UTF-8"));
            dataOutputStream.write('\n');
        } catch (IOException e) {
            closeQuietly(zip);
            closeQuietly(dataOutputStream);
            throw new IllegalStateException(e.getMessage(), e);
        } catch (JSONException e) {
            closeQuietly(zip);
            closeQuietly(dataOutputStream);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void writeFile(final String guid, final InputStream stream) {
        try {
            zip.putNextEntry(new ZipEntry(guid));
            copy(stream, zip);
            zip.closeEntry();
        } catch (IOException e) {
            closeQuietly(zip);
            closeQuietly(dataOutputStream);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void close() {
        InputStream dataInputStream = null;

        try {
            dataOutputStream.close();

            zip.putNextEntry(new ZipEntry(MobeelizerInputData.DATA_ENTRY_NAME));
            dataInputStream = new FileInputStream(dataFile);
            copy(dataInputStream, zip);
            dataInputStream.close();
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry(MobeelizerInputData.DELETED_FILES_ENTRY_NAME));
            writeLines(deletedFiles, "\n", zip);
            zip.closeEntry();

            zip.close();
        } catch (IOException e) {
            closeQuietly(zip);
            closeQuietly(dataInputStream);
            closeQuietly(dataOutputStream);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void copy(final InputStream is, final OutputStream os) throws IOException {
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = is.read(buffer))) {
            os.write(buffer, 0, n);
        }
    }

    private void writeLines(final Collection<String> lines, final String lineEnding, final OutputStream os) throws IOException {
        if (lines == null) {
            return;
        }
        for (String line : lines) {
            if (line != null) {
                os.write(line.getBytes());
            }
            os.write(lineEnding.getBytes());
        }
    }

    private void closeQuietly(final Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }

}
