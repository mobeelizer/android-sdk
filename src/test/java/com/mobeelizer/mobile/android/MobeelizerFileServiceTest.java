// 
// MobeelizerFileServiceTest.java
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.os.Environment;
import android.util.Log;

import com.mobeelizer.mobile.android.sync.MobeelizerInputData;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ UUID.class, Environment.class, File.class, MobeelizerFileService.class, Log.class })
public class MobeelizerFileServiceTest {

    private UUID uuid;

    private MobeelizerFileService fileService;

    private MobeelizerDatabaseImpl database;

    private File directory;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.when(Log.i(anyString(), anyString())).thenReturn(1);
        PowerMockito.mockStatic(Environment.class);
        File externalStorageDirectory = mock(File.class);
        when(externalStorageDirectory.getAbsolutePath()).thenReturn("externalPath");
        PowerMockito.when(Environment.getExternalStorageDirectory()).thenReturn(externalStorageDirectory);

        directory = mock(File.class);
        PowerMockito
                .whenNew(File.class)
                .withArguments(
                        "externalPath" + File.separator + "mobeelizer" + File.separator + "application" + File.separator
                                + "instance" + File.separator + "user" + File.separator).thenReturn(directory);

        uuid = UUID.randomUUID();

        PowerMockito.mockStatic(UUID.class);
        PowerMockito.when(UUID.randomUUID()).thenReturn(uuid);

        MobeelizerApplication application = mock(MobeelizerApplication.class);
        when(application.getInstance()).thenReturn("instance");
        when(application.getUser()).thenReturn("user");
        when(application.getApplication()).thenReturn("application");

        database = mock(MobeelizerDatabaseImpl.class);
        when(application.getDatabase()).thenReturn(database);

        fileService = new MobeelizerFileService(application);
    }

    @Test
    public void shouldAddFile() throws Exception {
        // given
        File file = testFolder.newFile("xxx");
        PowerMockito.whenNew(File.class).withArguments(directory, uuid.toString()).thenReturn(file);

        InputStream stream = new ByteArrayInputStream(new byte[] { 1, 3 });

        // when
        String guid = fileService.addFile(stream);

        // then
        assertEquals(uuid.toString(), guid);
        verify(database).addFile(uuid.toString(), file.getAbsolutePath());
        verify(directory).mkdirs();

        FileInputStream fileInputStream = new FileInputStream(file);

        assertEquals(1, fileInputStream.read());
        assertEquals(3, fileInputStream.read());
        assertEquals(-1, fileInputStream.read());
    }

    @Test
    public void shouldDeleteFilesFromSync() throws Exception {
        // given
        when(database.getFilePath("guid1")).thenReturn("path1");
        when(database.getFilePath("guid2")).thenReturn("path2");
        when(database.getFilePath("guid3")).thenReturn(null);

        File file1 = mock(File.class);
        PowerMockito.whenNew(File.class).withArguments("path1").thenReturn(file1);
        File file2 = mock(File.class);
        PowerMockito.whenNew(File.class).withArguments("path2").thenReturn(file2);

        // when
        fileService.deleteFilesFromSync(Arrays.asList(new String[] { "guid1", "guid2", "guid3" }));

        // then
        verify(file1).delete();
        verify(file2).delete();
        verify(database).deleteFileFromSync("guid1");
        verify(database).deleteFileFromSync("guid2");
    }

    @Test
    public void shouldAddFileFromSync() throws Exception {
        // given
        MobeelizerInputData inputData = mock(MobeelizerInputData.class);
        when(inputData.getFile("guid2")).thenReturn(new ByteArrayInputStream(new byte[] { 1, 3 }));

        File file = testFolder.newFile("xxx");
        PowerMockito.whenNew(File.class).withArguments(directory, "guid2").thenReturn(file);

        when(database.isFileExists("guid1")).thenReturn(true);
        when(database.isFileExists("guid2")).thenReturn(false);

        // when
        fileService.addFilesFromSync(Arrays.asList(new String[] { "guid1", "guid2" }), inputData);

        // then
        verify(database).addFileFromSync("guid2", file.getAbsolutePath());

        FileInputStream fileInputStream = new FileInputStream(file);

        assertEquals(1, fileInputStream.read());
        assertEquals(3, fileInputStream.read());
        assertEquals(-1, fileInputStream.read());
    }

    @Test
    public void shouldGetFile() throws Exception {
        // given
        File expectedFile = mock(File.class);
        PowerMockito.whenNew(File.class).withArguments(directory, "guid").thenReturn(expectedFile);

        // when
        File file = fileService.getFile("guid");

        // then
        assertSame(expectedFile, file);
    }
}
