// 
// MobeelizerTest.java
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
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.mobeelizer.java.api.MobeelizerFile;
import com.mobeelizer.mobile.android.api.MobeelizerDatabase;
import com.mobeelizer.mobile.android.api.MobeelizerLoginStatus;
import com.mobeelizer.mobile.android.api.MobeelizerSyncStatus;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Mobeelizer.class, MobeelizerFileImpl.class })
public class MobeelizerTest {

    private MobeelizerApplication application;

    @Before
    public void init() {
        application = mock(MobeelizerApplication.class);
        Mobeelizer.setInstance(application);
    }

    @Test
    public void shouldGetInstance() throws Exception {
        // when
        MobeelizerApplication instance = Mobeelizer.getInstance();

        // then
        assertSame(application, instance);
    }

    @Test
    public void shouldGetDatabase() throws Exception {
        // given
        MobeelizerDatabaseImpl expectedDatabase = mock(MobeelizerDatabaseImpl.class);
        when(application.getDatabase()).thenReturn(expectedDatabase);

        // when
        MobeelizerDatabase database = Mobeelizer.getDatabase();

        // then
        assertSame(expectedDatabase, database);
    }

    @Test
    public void shouldLogin() throws Exception {
        // given
        MobeelizerLoginStatus expectedStatus = MobeelizerLoginStatus.OK;
        when(application.login("instance", "user", "password")).thenReturn(expectedStatus);

        // when
        MobeelizerLoginStatus status = Mobeelizer.login("instance", "user", "password");

        // then
        assertEquals(expectedStatus, status);
    }

    @Test
    public void shouldLoginWithFailure() throws Exception {
        // given
        MobeelizerLoginStatus expectedStatus = MobeelizerLoginStatus.CONNECTION_FAILURE;
        when(application.login("instance", "user", "password")).thenReturn(expectedStatus);

        // when
        MobeelizerLoginStatus status = Mobeelizer.login("instance", "user", "password");

        // then
        assertEquals(expectedStatus, status);
    }

    @Test
    public void shouldLogout() throws Exception {
        // when
        Mobeelizer.logout();

        // then
        verify(application).logout();
    }

    @Test
    public void shouldSync() throws Exception {
        // when
        Mobeelizer.sync();

        // then
        verify(application).sync();
    }

    @Test
    public void shouldSyncAll() throws Exception {
        // when
        Mobeelizer.syncAll();

        // then
        verify(application).syncAll();
    }

    @Test
    public void shouldIsLoggedIn() throws Exception {
        // when
        Mobeelizer.isLoggedIn();

        // then
        verify(application).isLoggedIn();
    }

    @Test
    public void shouldSetSyncStatus() throws Exception {
        // when
        Mobeelizer.setSyncStatus(MobeelizerSyncStatus.FILE_RECEIVED);

        // then
        verify(application).setSyncStatus(MobeelizerSyncStatus.FILE_RECEIVED);
    }

    @Test
    public void shouldCheckSyncStatus() throws Exception {
        // given
        when(application.checkSyncStatus()).thenReturn(MobeelizerSyncStatus.FILE_CREATED);

        // when
        MobeelizerSyncStatus status = Mobeelizer.checkSyncStatus();

        // then
        assertEquals(MobeelizerSyncStatus.FILE_CREATED, status);
    }

    @Test
    public void shouldCreateFile() throws Exception {
        // given
        InputStream stream = mock(InputStream.class);
        MobeelizerFileImpl file = mock(MobeelizerFileImpl.class);
        PowerMockito.whenNew(MobeelizerFileImpl.class).withArguments("name", stream).thenReturn(file);

        // when
        MobeelizerFile actualFile = Mobeelizer.createFile("name", stream);

        // then
        assertEquals(file, actualFile);
    }

    @Test
    public void shouldCreateExistingFile() throws Exception {
        // given
        MobeelizerFileImpl file = mock(MobeelizerFileImpl.class);
        PowerMockito.whenNew(MobeelizerFileImpl.class).withArguments("name", "guid").thenReturn(file);

        // when
        MobeelizerFile actualFile = Mobeelizer.createFile("name", "guid");

        // then
        assertEquals(file, actualFile);
    }

    @Test
    public void shouldCheckSyncStatusWithOtherResult() throws Exception {
        // given
        when(application.checkSyncStatus()).thenReturn(MobeelizerSyncStatus.FINISHED_WITH_SUCCESS);

        // when
        MobeelizerSyncStatus status = Mobeelizer.checkSyncStatus();

        // then
        assertEquals(MobeelizerSyncStatus.FINISHED_WITH_SUCCESS, status);
    }

}
