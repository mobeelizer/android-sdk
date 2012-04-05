// 
// MobeelizerInternalDatabase.java
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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class MobeelizerInternalDatabase {

    private static final String FIELD_USER = "user";

    private static final String FIELD_INSTANCE = "instance";

    private static final String FIELD_PASSWORD = "password";

    private static final String FIELD_ROLE = "role";

    private static final String FIELD_INSTANCE_GUID = "instanceGuid";

    private static final String FIELD_INITIAL_SYNC_REQUIRED = "initialSyncRequired";

    private static final String TABLE_NAME = "roles";

    private static final String INSTANCE_AND_USER_AND_PASSWORD = "instance = ? and user = ? and password = ?";

    private static final String INSTANCE_AND_USER = "instance = ? and user = ?";

    private final MobeelizerInternalDatabaseHelper databaseHelper;

    public MobeelizerInternalDatabase(final MobeelizerApplication application) {
        this.databaseHelper = new MobeelizerInternalDatabaseHelper(application);
    }

    public boolean isInitialSyncRequired(final String instance, final String instanceGuid, final String user) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        Cursor cursor = database.query(TABLE_NAME, new String[] { FIELD_INITIAL_SYNC_REQUIRED, FIELD_INSTANCE_GUID },
                INSTANCE_AND_USER, new String[] { instance, user }, null, null, null);

        boolean initialSyncRequired = true;

        if (cursor.moveToNext() && instanceGuid.equals(cursor.getString(cursor.getColumnIndex(FIELD_INSTANCE_GUID)))
                && cursor.getInt(cursor.getColumnIndex(FIELD_INITIAL_SYNC_REQUIRED)) == 0) {
            initialSyncRequired = false;
        }

        cursor.close();
        database.close();

        return initialSyncRequired;
    }

    public void setInitialSyncAsNotRequired(final String instance, final String user) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FIELD_INITIAL_SYNC_REQUIRED, 0);
        database.update(TABLE_NAME, values, INSTANCE_AND_USER, new String[] { instance, user });

        database.close();
    }

    public void setRoleAndInstanceGuid(final String instance, final String user, final String password, final String role,
            final String instanceGuid) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        Cursor cursor = database.query(TABLE_NAME, new String[] { FIELD_ROLE }, INSTANCE_AND_USER,
                new String[] { instance, user }, null, null, null);

        ContentValues values = new ContentValues();
        values.put(FIELD_ROLE, role);
        values.put(FIELD_INSTANCE_GUID, instanceGuid);
        values.put(FIELD_PASSWORD, getMd5(password));

        if (cursor.moveToNext()) {
            database.update(TABLE_NAME, values, INSTANCE_AND_USER, new String[] { instance, user });
        } else {
            values.put(FIELD_INSTANCE, instance);
            values.put(FIELD_USER, user);
            values.put(FIELD_INITIAL_SYNC_REQUIRED, 1);
            database.insert(TABLE_NAME, null, values);
        }

        cursor.close();
        database.close();
    }

    public void clearRoleAndInstanceGuid(final String instance, final String user) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FIELD_ROLE, (String) null);
        values.put(FIELD_INSTANCE_GUID, (String) null);

        database.update(TABLE_NAME, values, INSTANCE_AND_USER, new String[] { instance, user });

        database.close();
    }

    public String[] getRoleAndInstanceGuid(final String instance, final String user, final String password) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        Cursor cursor = database.query(TABLE_NAME, new String[] { FIELD_ROLE, FIELD_INSTANCE_GUID },
                INSTANCE_AND_USER_AND_PASSWORD, new String[] { instance, user, getMd5(password) }, null, null, null);

        String role = null;
        String instanceGuid = null;

        if (cursor.moveToNext()) {
            role = cursor.getString(cursor.getColumnIndex(FIELD_ROLE));
            instanceGuid = cursor.getString(cursor.getColumnIndex(FIELD_INSTANCE_GUID));
        }

        cursor.close();
        database.close();

        return new String[] { role, instanceGuid };
    }

    private String getMd5(final String password) {
        MessageDigest m;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        m.update(password.getBytes(), 0, password.length());

        String hash = new BigInteger(1, m.digest()).toString(16);

        return hash;
    }

}
