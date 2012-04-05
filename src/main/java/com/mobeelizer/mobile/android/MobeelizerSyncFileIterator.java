// 
// MobeelizerSyncFileIterator.java
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
import java.util.Iterator;

import android.database.Cursor;

class MobeelizerSyncFileIterator implements Iterator<String> {

    private final Cursor cursor;

    public MobeelizerSyncFileIterator(final Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public boolean hasNext() {
        if (cursor.moveToNext()) {
            return true;
        }

        cursor.close();

        return false;
    }

    @Override
    public String next() {
        return cursor.getString(cursor.getColumnIndex(MobeelizerDatabaseImpl._FILE_GUID));
    }

    public InputStream getStream() {
        String path = cursor.getString(cursor.getColumnIndex(MobeelizerDatabaseImpl._FILE_PATH));

        try {
            return new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Operation not supported.");
    }

    public void close() {
        if (!cursor.isClosed()) {
            cursor.close();
        }
    }

}
