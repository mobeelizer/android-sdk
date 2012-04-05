// 
// MobeelizerSyncServicePerformerTest.java
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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mobeelizer.mobile.android.api.MobeelizerSyncStatus;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Log.class, MobeelizerSyncServicePerformer.class, Context.class, File.class, Intent.class })
public class MobeelizerSyncServicePerformerTest {

    private MobeelizerApplication application;

    private MobeelizerConnectionManager connectionManager;

    private MobeelizerDatabaseImpl database;

    private MobeelizerInternalDatabase internalDatabase;

    private Context context;

    private MobeelizerDataFileService dataFileService;

    private Intent intent;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.when(Log.class, "i", anyString(), anyString()).thenReturn(0);

        intent = PowerMockito.mock(Intent.class);
        PowerMockito.whenNew(Intent.class).withArguments(anyString()).thenReturn(intent);

        application = mock(MobeelizerApplication.class);
        when(application.checkSyncStatus()).thenReturn(MobeelizerSyncStatus.NONE);
        when(application.getInstance()).thenReturn("instance");
        when(application.getUser()).thenReturn("user");

        dataFileService = mock(MobeelizerDataFileService.class);
        whenNew(MobeelizerDataFileService.class).withArguments(application).thenReturn(dataFileService);

        context = PowerMockito.mock(Context.class);
        when(application.getContext()).thenReturn(context);

        database = mock(MobeelizerDatabaseImpl.class);
        when(application.getDatabase()).thenReturn(database);

        internalDatabase = mock(MobeelizerInternalDatabase.class);
        when(application.getInternalDatabase()).thenReturn(internalDatabase);

        connectionManager = mock(MobeelizerConnectionManager.class);
        when(application.getConnectionManager()).thenReturn(connectionManager);

    }

    // @Test
    // public void shouldInterruptIfRunning() throws Exception {
    // // given
    // when(application.checkSyncStatus()).thenReturn(MobeelizerSyncStatus.FILE_RECEIVED);
    //
    // // when
    // new MobeelizerSyncServicePerformer(application, true).doInBackground();
    //
    // // then
    // verify(database, never()).lockModifiedFlag();
    // }
    //
    // @Test
    // public void shouldSyncWithRequestError() throws Exception {
    // // given
    // when(connectionManager.sendSyncAllRequest()).thenReturn("ticket");
    // when(connectionManager.waitUntilSyncRequestComplete("ticket")).thenReturn(false);
    // when(application.checkSyncStatus()).thenReturn(MobeelizerSyncStatus.NONE, MobeelizerSyncStatus.FILE_CREATED);
    //
    // // when
    // new MobeelizerSyncServicePerformer(application, true).doInBackground();
    //
    // // then
    // InOrder order = Mockito.inOrder(database, connectionManager, application);
    // order.verify(database).lockModifiedFlag();
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.STARTED);
    // order.verify(connectionManager).sendSyncAllRequest();
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.TASK_CREATED);
    // order.verify(connectionManager).waitUntilSyncRequestComplete("ticket");
    // order.verify(database).unlockModifiedFlag();
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.FINISHED_WITH_FAILURE);
    // verify(internalDatabase, never()).setInitialSyncAsNotRequired("instance", "user");
    // }
    //
    // @Test
    // public void shouldSyncWithRequestException() throws Exception {
    // // given
    // when(connectionManager.sendSyncAllRequest()).thenThrow(new ConnectionException("msg"));
    // when(application.checkSyncStatus()).thenReturn(MobeelizerSyncStatus.NONE, MobeelizerSyncStatus.FILE_CREATED);
    //
    // // when
    // new MobeelizerSyncServicePerformer(application, true).doInBackground();
    //
    // // then
    // InOrder order = Mockito.inOrder(database, connectionManager, application);
    // order.verify(database).lockModifiedFlag();
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.STARTED);
    // order.verify(connectionManager).sendSyncAllRequest();
    // order.verify(database).unlockModifiedFlag();
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.FINISHED_WITH_FAILURE);
    // verify(internalDatabase, never()).setInitialSyncAsNotRequired("instance", "user");
    // }
    //
    // @Test
    // public void shouldSyncWithRequestException2() throws Exception {
    // // given
    // PowerMockito.mockStatic(File.class);
    // PowerMockito.when(File.createTempFile(anyString(), anyString(), any(File.class))).thenThrow(new IOException("msg"));
    // when(application.checkSyncStatus()).thenReturn(MobeelizerSyncStatus.NONE, MobeelizerSyncStatus.FILE_CREATED);
    //
    // // when
    // new MobeelizerSyncServicePerformer(application, false).doInBackground();
    //
    // // then
    // InOrder order = Mockito.inOrder(database, connectionManager, application);
    // order.verify(database).lockModifiedFlag();
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.STARTED);
    // order.verify(database).unlockModifiedFlag();
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.FINISHED_WITH_FAILURE);
    // verify(internalDatabase, never()).setInitialSyncAsNotRequired("instance", "user");
    // }
    //
    // @Test
    // public void shouldSyncWithOutputFilePrepareFailure() throws Exception {
    // // given
    // when(dataFileService.prepareOutputFile(any(File.class))).thenReturn(false);
    // when(application.checkSyncStatus()).thenReturn(MobeelizerSyncStatus.NONE, MobeelizerSyncStatus.FILE_CREATED);
    //
    // // when
    // new MobeelizerSyncServicePerformer(application, false).doInBackground();
    //
    // // then
    // InOrder order = Mockito.inOrder(database, connectionManager, application, dataFileService);
    // order.verify(database).lockModifiedFlag();
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.STARTED);
    // order.verify(dataFileService).prepareOutputFile(any(File.class));
    // order.verify(database).unlockModifiedFlag();
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.FINISHED_WITH_FAILURE);
    // verify(internalDatabase, never()).setInitialSyncAsNotRequired("instance", "user");
    // }
    //
    // @Test
    // public void shouldSyncWithInputFilePrepareFailure() throws Exception {
    // // given
    // when(dataFileService.prepareOutputFile(any(File.class))).thenReturn(true);
    // when(connectionManager.sendSyncDiffRequest(any(File.class))).thenReturn("ticket");
    // when(connectionManager.waitUntilSyncRequestComplete("ticket")).thenReturn(true);
    // File file = mock(File.class);
    // when(connectionManager.getSyncData("ticket")).thenReturn(file);
    // when(dataFileService.processInputFile(file, false)).thenReturn(false);
    // when(application.checkSyncStatus()).thenReturn(MobeelizerSyncStatus.NONE, MobeelizerSyncStatus.FILE_CREATED);
    //
    // // when
    // new MobeelizerSyncServicePerformer(application, false).doInBackground();
    //
    // // then
    // InOrder order = Mockito.inOrder(database, connectionManager, application, dataFileService);
    // order.verify(database).lockModifiedFlag();
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.STARTED);
    // order.verify(dataFileService).prepareOutputFile(any(File.class));
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.FILE_CREATED);
    // order.verify(connectionManager).sendSyncDiffRequest(any(File.class));
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.TASK_CREATED);
    // order.verify(connectionManager).waitUntilSyncRequestComplete("ticket");
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.TASK_PERFORMED);
    // order.verify(connectionManager).getSyncData("ticket");
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.FILE_RECEIVED);
    // order.verify(dataFileService).processInputFile(file, false);
    // order.verify(database).unlockModifiedFlag();
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.FINISHED_WITH_FAILURE);
    // verify(internalDatabase, never()).setInitialSyncAsNotRequired("instance", "user");
    // }
    //
    // @Test
    // public void shouldSync() throws Exception {
    // // given
    // when(dataFileService.prepareOutputFile(any(File.class))).thenReturn(true);
    // when(connectionManager.sendSyncDiffRequest(any(File.class))).thenReturn("ticket");
    // when(connectionManager.waitUntilSyncRequestComplete("ticket")).thenReturn(true);
    // File file = mock(File.class);
    // when(connectionManager.getSyncData("ticket")).thenReturn(file);
    // when(dataFileService.processInputFile(file, false)).thenReturn(true);
    //
    // // when
    // new MobeelizerSyncServicePerformer(application, false).doInBackground();
    //
    // // then
    // InOrder order = Mockito.inOrder(database, connectionManager, application, dataFileService, internalDatabase, context,
    // intent);
    // order.verify(database).lockModifiedFlag();
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.STARTED);
    // order.verify(intent).putExtra(Mobeelizer.BROADCAST_SYNC_STATUS_CHANGE_STATUS, MobeelizerSyncStatus.STARTED);
    // order.verify(context).sendBroadcast(intent);
    // order.verify(dataFileService).prepareOutputFile(any(File.class));
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.FILE_CREATED);
    // order.verify(intent).putExtra(Mobeelizer.BROADCAST_SYNC_STATUS_CHANGE_STATUS, MobeelizerSyncStatus.FILE_CREATED);
    // order.verify(context).sendBroadcast(intent);
    // order.verify(connectionManager).sendSyncDiffRequest(any(File.class));
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.TASK_CREATED);
    // order.verify(intent).putExtra(Mobeelizer.BROADCAST_SYNC_STATUS_CHANGE_STATUS, MobeelizerSyncStatus.TASK_CREATED);
    // order.verify(context).sendBroadcast(intent);
    // order.verify(connectionManager).waitUntilSyncRequestComplete("ticket");
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.TASK_PERFORMED);
    // order.verify(intent).putExtra(Mobeelizer.BROADCAST_SYNC_STATUS_CHANGE_STATUS, MobeelizerSyncStatus.TASK_PERFORMED);
    // order.verify(context).sendBroadcast(intent);
    // order.verify(connectionManager).getSyncData("ticket");
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.FILE_RECEIVED);
    // order.verify(intent).putExtra(Mobeelizer.BROADCAST_SYNC_STATUS_CHANGE_STATUS, MobeelizerSyncStatus.FILE_RECEIVED);
    // order.verify(context).sendBroadcast(intent);
    // order.verify(dataFileService).processInputFile(file, false);
    // order.verify(connectionManager).confirmTask("ticket");
    // order.verify(database).clearModifiedFlag();
    // order.verify(internalDatabase).setInitialSyncAsNotRequired("instance", "user");
    // order.verify(database).unlockModifiedFlag();
    // order.verify(application).setSyncStatus(MobeelizerSyncStatus.FINISHED_WITH_SUCCESS);
    // order.verify(intent).putExtra(Mobeelizer.BROADCAST_SYNC_STATUS_CHANGE_STATUS, MobeelizerSyncStatus.FINISHED_WITH_SUCCESS);
    // order.verify(context).sendBroadcast(intent);
    // }

}
