// 
// MobeelizerInternalDatabaseAdapterTest.java
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MobeelizerInternalDatabase.class, ContentValues.class })
public class MobeelizerInternalDatabaseAdapterTest {

    private MobeelizerInternalDatabase databaseAdapter;

    private MobeelizerInternalDatabaseHelper databaseHelper;

    private SQLiteDatabase database;

    private ContentValues values;

    @Before
    public void init() throws Exception {
        MobeelizerApplication application = mock(MobeelizerApplication.class);
        when(application.getUser()).thenReturn("owner");

        database = mock(SQLiteDatabase.class);

        values = mock(ContentValues.class);
        PowerMockito.whenNew(ContentValues.class).withNoArguments().thenReturn(values);

        databaseHelper = mock(MobeelizerInternalDatabaseHelper.class);
        when(databaseHelper.getWritableDatabase()).thenReturn(database);

        whenNew(MobeelizerInternalDatabaseHelper.class).withArguments(application).thenReturn(databaseHelper);

        databaseAdapter = new MobeelizerInternalDatabase(application);
    }

    @Test
    public void shouldSaveNewRole() throws Exception {
        // given
        Cursor cursor = PowerMockito.mock(Cursor.class);
        when(
                database.query("roles", new String[] { "role" }, "instance = ? and user = ?",
                        new String[] { "instance", "user" }, null, null, null)).thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(false);

        // when
        databaseAdapter.setRoleAndInstanceGuid("instance", "user", "password", "role", "0000");

        // then
        verify(values).put("role", "role");
        verify(values).put("instanceGuid", "0000");
        verify(values).put("instance", "instance");
        verify(values).put("user", "user");
        verify(values).put("initialSyncRequired", 1);
        verify(values).put("password", getMd5("password"));
        verify(databaseHelper).getWritableDatabase();
        verify(database).close();
        verify(database).insert("roles", null, values);
    }

    @Test
    public void shouldSaveRole() throws Exception {
        // given
        Cursor cursor = PowerMockito.mock(Cursor.class);
        when(
                database.query("roles", new String[] { "role" }, "instance = ? and user = ?",
                        new String[] { "instance", "user" }, null, null, null)).thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(true);

        // when
        databaseAdapter.setRoleAndInstanceGuid("instance", "user", "password", "role", "0000");

        // then
        verify(values).put("role", "role");
        verify(values).put("instanceGuid", "0000");
        verify(database).update("roles", values, "instance = ? and user = ?", new String[] { "instance", "user" });
        verify(databaseHelper).getWritableDatabase();
        verify(database).close();
    }

    @Test
    public void shouldClearRole() throws Exception {
        // when
        databaseAdapter.clearRoleAndInstanceGuid("instance", "user");

        // then

        verify(values).put("role", (String) null);
        verify(database).update("roles", values, "instance = ? and user = ?", new String[] { "instance", "user" });
        verify(databaseHelper).getWritableDatabase();
        verify(database).close();
    }

    @Test
    public void shouldSetInitialSyncAsNotRequired() throws Exception {
        // when
        databaseAdapter.setInitialSyncAsNotRequired("instance", "user");

        // then

        verify(values).put("initialSyncRequired", 0);
        verify(database).update("roles", values, "instance = ? and user = ?", new String[] { "instance", "user" });
        verify(databaseHelper).getWritableDatabase();
        verify(database).close();
    }

    @Test
    public void shouldGetInitialSyncRequired() throws Exception {
        // given
        Cursor cursor = PowerMockito.mock(Cursor.class);
        when(
                database.query("roles", new String[] { "initialSyncRequired", "instanceGuid" }, "instance = ? and user = ?",
                        new String[] { "instance", "user" }, null, null, null)).thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(true);
        when(cursor.getColumnIndex("initialSyncRequired")).thenReturn(12);
        when(cursor.getColumnIndex("instanceGuid")).thenReturn(13);
        when(cursor.getInt(12)).thenReturn(1);
        when(cursor.getString(13)).thenReturn("0000");

        // when
        boolean initialSyncRequired = databaseAdapter.isInitialSyncRequired("instance", "0000", "user");

        // then
        assertTrue(initialSyncRequired);
        verify(databaseHelper).getWritableDatabase();
        verify(database).close();
    }

    @Test
    public void shouldGetInitialSyncRequiredFalse() throws Exception {
        // given
        Cursor cursor = PowerMockito.mock(Cursor.class);
        when(
                database.query("roles", new String[] { "initialSyncRequired", "instanceGuid" }, "instance = ? and user = ?",
                        new String[] { "instance", "user" }, null, null, null)).thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(true);
        when(cursor.getColumnIndex("initialSyncRequired")).thenReturn(12);
        when(cursor.getColumnIndex("instanceGuid")).thenReturn(13);
        when(cursor.getInt(12)).thenReturn(0);
        when(cursor.getString(13)).thenReturn("0000");

        // when
        boolean initialSyncRequired = databaseAdapter.isInitialSyncRequired("instance", "0000", "user");

        // then
        assertFalse(initialSyncRequired);
        verify(databaseHelper).getWritableDatabase();
        verify(database).close();
    }

    @Test
    public void shouldGetInitialSyncRequiredFalse2() throws Exception {
        // given
        Cursor cursor = PowerMockito.mock(Cursor.class);
        when(
                database.query("roles", new String[] { "initialSyncRequired", "instanceGuid" }, "instance = ? and user = ?",
                        new String[] { "instance", "user" }, null, null, null)).thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(true);
        when(cursor.getColumnIndex("initialSyncRequired")).thenReturn(12);
        when(cursor.getColumnIndex("instanceGuid")).thenReturn(13);
        when(cursor.getInt(12)).thenReturn(0);
        when(cursor.getString(13)).thenReturn("0000");

        // when
        boolean initialSyncRequired = databaseAdapter.isInitialSyncRequired("instance", "0001", "user");

        // then
        assertTrue(initialSyncRequired);
        verify(databaseHelper).getWritableDatabase();
        verify(database).close();
    }

    @Test
    public void shouldGetInitialSyncRequiredFalse3() throws Exception {
        // given
        Cursor cursor = PowerMockito.mock(Cursor.class);
        when(
                database.query("roles", new String[] { "initialSyncRequired", "instanceGuid" }, "instance = ? and user = ?",
                        new String[] { "instance", "user" }, null, null, null)).thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(false);

        // when
        boolean initialSyncRequired = databaseAdapter.isInitialSyncRequired("instance", "0000", "user");

        // then
        assertTrue(initialSyncRequired);
        verify(databaseHelper).getWritableDatabase();
        verify(database).close();
    }

    @Test
    public void shouldGetRole() throws Exception {
        // given
        Cursor cursor = PowerMockito.mock(Cursor.class);
        when(
                database.query("roles", new String[] { "role", "instanceGuid" }, "instance = ? and user = ? and password = ?",
                        new String[] { "instance", "user", getMd5("password") }, null, null, null)).thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(true);
        when(cursor.getColumnIndex("role")).thenReturn(12);
        when(cursor.getColumnIndex("instanceGuid")).thenReturn(13);
        when(cursor.getString(12)).thenReturn("role");
        when(cursor.getString(13)).thenReturn("0000");

        // when
        String[] roleAndInstanceGuid = databaseAdapter.getRoleAndInstanceGuid("instance", "user", "password");

        // then
        assertEquals("role", roleAndInstanceGuid[0]);
        assertEquals("0000", roleAndInstanceGuid[1]);
        verify(databaseHelper).getWritableDatabase();
        verify(database).close();
    }

    @Test
    public void shouldGetNullRole() throws Exception {
        // given
        Cursor cursor = PowerMockito.mock(Cursor.class);
        when(
                database.query("roles", new String[] { "role", "instanceGuid" }, "instance = ? and user = ? and password = ?",
                        new String[] { "instance", "user", getMd5("password") }, null, null, null)).thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(false);

        // when
        String[] roleAndInstanceGuid = databaseAdapter.getRoleAndInstanceGuid("instance", "user", "password");

        // then
        assertNull(roleAndInstanceGuid[0]);
        assertNull(roleAndInstanceGuid[1]);
        verify(databaseHelper).getWritableDatabase();
        verify(database).close();
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
