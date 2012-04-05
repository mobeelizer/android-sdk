// 
// MobeelizerDataFileServiceTest.java
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.util.Log;

import com.mobeelizer.mobile.android.sync.MobeelizerInputData;
import com.mobeelizer.mobile.android.sync.MobeelizerJsonEntity;
import com.mobeelizer.mobile.android.sync.MobeelizerOutputData;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MobeelizerDataFileService.class, MobeelizerInputData.class, MobeelizerOutputData.class, Log.class,
        Context.class })
class MobeelizerDataFileServiceTest {

    private MobeelizerDataFileService dataFileService;

    private MobeelizerDatabaseImpl database;

    private MobeelizerFileService fileService;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.when(Log.class, "i", anyString(), anyString()).thenReturn(0);

        Context context = PowerMockito.mock(Context.class);
        PowerMockito.when(context.getDir(anyString(), anyInt())).thenReturn(new File(System.getProperty("java.io.tmpdir")));

        MobeelizerApplication application = mock(MobeelizerApplication.class);
        database = mock(MobeelizerDatabaseImpl.class);
        fileService = mock(MobeelizerFileService.class);

        when(application.getFileService()).thenReturn(fileService);
        when(application.getDatabase()).thenReturn(database);
        when(application.getContext()).thenReturn(context);

        dataFileService = new MobeelizerDataFileService(application);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldProcessInputFile() throws Exception {
        // given
        File file = mock(File.class);
        FileInputStream fileStream = mock(FileInputStream.class);
        MobeelizerInputData inputData = mock(MobeelizerInputData.class);
        Iterable<MobeelizerJsonEntity> inputDataIterable = mock(Iterable.class);
        Iterator<MobeelizerJsonEntity> inputDataIterator = mock(Iterator.class);
        when(inputData.getInputData()).thenReturn(inputDataIterable);
        when(inputDataIterable.iterator()).thenReturn(inputDataIterator);
        PowerMockito.whenNew(FileInputStream.class).withArguments(file).thenReturn(fileStream);
        PowerMockito.whenNew(MobeelizerInputData.class).withArguments(eq(fileStream), any(File.class)).thenReturn(inputData);

        when(database.updateEntitiesFromSync(inputDataIterator, true)).thenReturn(true);

        // when
        boolean result = dataFileService.processInputFile(file, true);

        // then
        InOrder order = Mockito.inOrder(inputData, database, fileService);
        order.verify(fileService).addFilesFromSync(anyList(), any(MobeelizerInputData.class));
        order.verify(database).updateEntitiesFromSync(inputDataIterator, true);
        order.verify(fileService).deleteFilesFromSync(anyList());
        order.verify(inputData).close();
        assertTrue(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotProcessInputFileBecauseOfUpdateEntitiesFromSyncFailure() throws Exception {
        // given
        File file = mock(File.class);
        FileInputStream fileStream = mock(FileInputStream.class);
        MobeelizerInputData inputData = mock(MobeelizerInputData.class);
        Iterable<MobeelizerJsonEntity> inputDataIterable = mock(Iterable.class);
        Iterator<MobeelizerJsonEntity> inputDataIterator = mock(Iterator.class);
        when(inputData.getInputData()).thenReturn(inputDataIterable);
        when(inputDataIterable.iterator()).thenReturn(inputDataIterator);
        PowerMockito.whenNew(FileInputStream.class).withArguments(file).thenReturn(fileStream);
        PowerMockito.whenNew(MobeelizerInputData.class).withArguments(eq(fileStream), any(File.class)).thenReturn(inputData);

        when(database.updateEntitiesFromSync(inputDataIterator, false)).thenReturn(false);

        // when
        boolean result = dataFileService.processInputFile(file, false);

        // then
        InOrder order = Mockito.inOrder(inputData, database, fileService);
        order.verify(fileService).addFilesFromSync(anyList(), any(MobeelizerInputData.class));
        order.verify(database).updateEntitiesFromSync(inputDataIterator, false);
        order.verify(inputData).close();
        assertFalse(result);
    }

    @Test
    public void shouldPrepareOutputFile() throws Exception {
        // given
        File file = mock(File.class);
        MobeelizerOutputData outputData = mock(MobeelizerOutputData.class);
        PowerMockito.whenNew(MobeelizerOutputData.class).withArguments(eq(file), any(File.class)).thenReturn(outputData);

        MobeelizerSyncIterator iterator = mock(MobeelizerSyncIterator.class);
        when(database.getEntitiesToSync()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, true, false);
        MobeelizerJsonEntity entity1 = mock(MobeelizerJsonEntity.class);
        MobeelizerJsonEntity entity2 = mock(MobeelizerJsonEntity.class);
        when(iterator.next()).thenReturn(entity1, entity2);

        MobeelizerSyncFileIterator fileIterator = mock(MobeelizerSyncFileIterator.class);
        when(database.getFilesToSync()).thenReturn(fileIterator);
        when(fileIterator.hasNext()).thenReturn(true, true, true, false);
        when(fileIterator.next()).thenReturn("guid1", "guid2", "guid3", null);
        InputStream stream1 = mock(InputStream.class);
        InputStream stream3 = mock(InputStream.class);
        when(fileIterator.getStream()).thenReturn(stream1, null, stream3, null);

        // when
        boolean result = dataFileService.prepareOutputFile(file);

        // then
        InOrder order = Mockito.inOrder(outputData, database, iterator, fileIterator);
        order.verify(database).getEntitiesToSync();
        order.verify(outputData).writeEntity(entity1);
        order.verify(outputData).writeEntity(entity2);
        order.verify(outputData).writeFile("guid1", stream1);
        order.verify(outputData).writeFile("guid3", stream3);
        order.verify(iterator).close();
        order.verify(fileIterator).close();
        order.verify(outputData).close();
        assertTrue(result);
    }

}
