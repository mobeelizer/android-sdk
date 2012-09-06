// 
// MobeelizerDevelopmentConnectionManagerTest.java
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
import static org.mockito.Mockito.mock;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class MobeelizerDevelopmentConnectionManagerTest {

    private MobeelizerDevelopmentConnectionManager connectionManager;

    @Before
    public void init() {
        connectionManager = new MobeelizerDevelopmentConnectionManager("role");
    }

    @Test
    public void shouldLogin() throws Exception {
        // when
        MobeelizerLoginResponse login = connectionManager.login();

        // then
        assertEquals("role", login.getRole());
        assertEquals(null, login.getError());
        assertFalse(login.isInitialSyncRequired());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionOnConfirmTask() throws Exception {
        // when
        connectionManager.confirmTask("ticket");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionOnGetSyncData() throws Exception {
        // when
        connectionManager.getSyncData("ticket");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionOnSendSyncAllRequest() throws Exception {
        // when
        connectionManager.sendSyncAllRequest();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionOnSendSyncDiffRequest() throws Exception {
        // given
        File file = mock(File.class);

        // when
        connectionManager.sendSyncDiffRequest(file);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionOnWaitUntilSyncRequestComplete() throws Exception {
        // when
        connectionManager.waitUntilSyncRequestComplete("ticket");
    }
}
