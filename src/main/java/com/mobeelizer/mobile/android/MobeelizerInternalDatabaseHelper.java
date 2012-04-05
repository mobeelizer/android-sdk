// 
// MobeelizerInternalDatabaseHelper.java
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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class MobeelizerInternalDatabaseHelper extends SQLiteOpenHelper {

    public MobeelizerInternalDatabaseHelper(final MobeelizerApplication application) {
        super(application.getContext(), "internal", null, 1);
    }

    @Override
    public void onCreate(final SQLiteDatabase database) {
        database.execSQL("CREATE TABLE roles (instance TEXT NOT NULL, user TEXT NOT NULL, password TEXT NOT NULL, role TEXT, instanceGuid TEXT, initialSyncRequired INTEGER(1) NOT NULL DEFAULT 0, PRIMARY KEY(instance, user))");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion) {
        // empty
    }

}
