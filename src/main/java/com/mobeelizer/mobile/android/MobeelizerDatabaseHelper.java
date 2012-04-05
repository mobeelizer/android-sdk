// 
// MobeelizerDatabaseHelper.java
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

import java.util.Set;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mobeelizer.mobile.android.model.MobeelizerModelDefinitionImpl;

class MobeelizerDatabaseHelper extends SQLiteOpenHelper {

    private final static String TAG = "mobeelizer:databasehelper";

    private final Set<MobeelizerModelDefinitionImpl> models;

    public MobeelizerDatabaseHelper(final MobeelizerApplication application, final Set<MobeelizerModelDefinitionImpl> models) {
        super(application.getContext(), application.getInstanceGuid() + "_" + application.getUser() + "_data", null, application
                .getDatabaseVersion());
        this.models = models;
    }

    @Override
    public void onCreate(final SQLiteDatabase database) {
        Log.i(TAG, "Creating " + models.size() + " tables");

        for (MobeelizerModelDefinitionImpl model : models) {
            model.onCreate(database);
        }

        database.execSQL("CREATE TABLE " + MobeelizerDatabaseImpl._FILE_TABLE_NAME + " (" + MobeelizerDatabaseImpl._FILE_GUID
                + " TEXT NOT NULL, " + MobeelizerDatabaseImpl._FILE_PATH + " TEXT NOT NULL, "
                + MobeelizerDatabaseImpl._FILE_MODIFIED + " INTEGER(1) NOT NULL DEFAULT 0, PRIMARY KEY("
                + MobeelizerDatabaseImpl._FILE_GUID + "))");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion) {
        Log.i(TAG, "Updating " + models.size() + " tables");

        for (MobeelizerModelDefinitionImpl model : models) {
            model.onUpgrade(database);
        }
    }

}
