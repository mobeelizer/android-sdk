// 
// MobeelizerFileImplTest.java
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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Mobeelizer.class, FileInputStream.class, MobeelizerFileImpl.class })
public class MobeelizerFileImplTest {

    private MobeelizerFileService fileService;

    private MobeelizerDatabaseImpl database;

    @Before
    public void init() {
        fileService = mock(MobeelizerFileService.class);
        database = mock(MobeelizerDatabaseImpl.class);

        MobeelizerApplication application = mock(MobeelizerApplication.class);
        when(application.getFileService()).thenReturn(fileService);
        when(application.getDatabase()).thenReturn(database);

        PowerMockito.mockStatic(Mobeelizer.class);
        PowerMockito.when(Mobeelizer.getInstance()).thenReturn(application);
    }

    @Test
    public void shouldAddFile() throws Exception {
        // given
        ByteArrayInputStream stream = new ByteArrayInputStream(new byte[0]);
        when(fileService.addFile(stream)).thenReturn("guid");
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.canRead()).thenReturn(true);
        when(fileService.getFile("guid")).thenReturn(file);
        FileInputStream fileStream = mock(FileInputStream.class);
        PowerMockito.whenNew(FileInputStream.class).withArguments(file).thenReturn(fileStream);

        // when
        MobeelizerFileImpl mobeelizerFile = new MobeelizerFileImpl("name", stream);

        // then
        assertEquals("guid", mobeelizerFile.getGuid());
        assertEquals("name", mobeelizerFile.getName());
        assertEquals(fileStream, mobeelizerFile.getInputStream());
    }

    @Test
    public void shouldUseExistingFile() throws Exception {
        // given
        when(database.isFileExists("guid")).thenReturn(true);
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.canRead()).thenReturn(true);
        when(fileService.getFile("guid")).thenReturn(file);
        FileInputStream fileStream = mock(FileInputStream.class);
        PowerMockito.whenNew(FileInputStream.class).withArguments(file).thenReturn(fileStream);

        // when
        MobeelizerFileImpl mobeelizerFile = new MobeelizerFileImpl("name", "guid");

        // then
        assertEquals("guid", mobeelizerFile.getGuid());
        assertEquals("name", mobeelizerFile.getName());
        assertEquals(fileStream, mobeelizerFile.getInputStream());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailWhileusingNotExistingFile() throws Exception {
        // given
        when(database.isFileExists("guid")).thenReturn(false);

        // when
        new MobeelizerFileImpl("name", "guid");
    }

    @Test
    public void shouldReturnNullIfFileCannotBeRead() throws Exception {
        // given
        ByteArrayInputStream stream = new ByteArrayInputStream(new byte[0]);
        when(fileService.addFile(stream)).thenReturn("guid");
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.canRead()).thenReturn(false);
        when(fileService.getFile("guid")).thenReturn(file);

        // when
        MobeelizerFileImpl mobeelizerFile = new MobeelizerFileImpl("name", stream);

        // then
        assertEquals("guid", mobeelizerFile.getGuid());
        assertEquals("name", mobeelizerFile.getName());
        assertNull(mobeelizerFile.getInputStream());
    }

    @Test
    public void shouldReturnNullIfNotExistCannotBeRead() throws Exception {
        // given
        ByteArrayInputStream stream = new ByteArrayInputStream(new byte[0]);
        when(fileService.addFile(stream)).thenReturn("guid");
        File file = mock(File.class);
        when(file.exists()).thenReturn(false);
        when(file.canRead()).thenReturn(true);
        when(fileService.getFile("guid")).thenReturn(file);

        // when
        MobeelizerFileImpl mobeelizerFile = new MobeelizerFileImpl("name", stream);

        // then
        assertEquals("guid", mobeelizerFile.getGuid());
        assertEquals("name", mobeelizerFile.getName());
        assertNull(mobeelizerFile.getInputStream());
    }

    @Test
    public void shouldReturnNullIfFileIsNull() throws Exception {
        // given
        ByteArrayInputStream stream = new ByteArrayInputStream(new byte[0]);
        when(fileService.addFile(stream)).thenReturn("guid");
        when(fileService.getFile("guid")).thenReturn(null);

        // when
        MobeelizerFileImpl mobeelizerFile = new MobeelizerFileImpl("name", stream);

        // then
        assertEquals("guid", mobeelizerFile.getGuid());
        assertEquals("name", mobeelizerFile.getName());
        assertNull(mobeelizerFile.getInputStream());
    }

}
