// 
// MobeelizerFileImpl.java
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
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.mobeelizer.java.api.MobeelizerFile;

public class MobeelizerFileImpl implements MobeelizerFile {

    private final String name;

    private final String guid;

    private final File file;

    public MobeelizerFileImpl(final String name, final InputStream stream) {
        if (name == null || "".equals(name)) {
            throw new IllegalStateException("Filename is empty.");
        }
        this.name = name;
        this.guid = Mobeelizer.getInstance().getFileService().addFile(stream);
        this.file = resolveFile();
    }

    public MobeelizerFileImpl(final String name, final String guid) {
        if (!Mobeelizer.getInstance().getDatabase().isFileExists(guid)) {
            throw new IllegalStateException("File " + guid + " doesn't exist.");
        }
        if (name == null || "".equals(name)) {
            throw new IllegalStateException("Filename is empty.");
        }
        this.guid = guid;
        this.name = name;
        this.file = resolveFile();
    }

    private File resolveFile() {
        return Mobeelizer.getInstance().getFileService().getFile(guid);
    }

    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InputStream getInputStream() {
        if (file == null || !file.exists() || !file.canRead()) {
            return null; // TODO V3 external storage was removed?
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

}
