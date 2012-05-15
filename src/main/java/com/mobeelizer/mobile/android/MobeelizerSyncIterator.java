// 
// MobeelizerSyncIterator.java
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

import java.util.Collection;
import java.util.Iterator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobeelizer.java.sync.MobeelizerJsonEntity;
import com.mobeelizer.mobile.android.model.MobeelizerAndroidModel;

class MobeelizerSyncIterator implements Iterator<MobeelizerJsonEntity> {

    private final Iterator<MobeelizerAndroidModel> models;

    private final SQLiteDatabase database;

    private MobeelizerAndroidModel model;

    private Cursor cursor;

    public MobeelizerSyncIterator(final SQLiteDatabase database, final Collection<MobeelizerAndroidModel> models) {
        this.database = database;
        this.models = models.iterator();
    }

    @Override
    public boolean hasNext() {
        if (model == null) {
            if (models.hasNext()) {
                model = models.next();
                cursor = model.getEntitiesToSync(database);
            } else {
                return false;
            }
        }

        if (cursor.moveToNext()) {
            return true;
        }

        model = null;
        cursor.close();
        cursor = null;

        return hasNext();
    }

    @Override
    public MobeelizerJsonEntity next() {
        return model.getJsonEntity(cursor);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Operation not supported.");

    }

    public void close() {
        if (cursor != null) {
            cursor.close();
        }
    }

}
