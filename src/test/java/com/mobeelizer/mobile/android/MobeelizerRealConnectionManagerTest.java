// 
// MobeelizerRealConnectionManagerTest.java
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
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.ByteArrayInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.util.Log;

import com.mobeelizer.java.connection.MobeelizerConnectionServiceImpl;
import com.mobeelizer.mobile.android.api.MobeelizerLoginStatus;

// TODO MASZ fix test
@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({ MobeelizerRealConnectionManager.class, Log.class, DefaultHttpClient.class, HttpGet.class, HttpPost.class,
        Proxy.class, MobeelizerConnectionServiceImpl.class })
public class MobeelizerRealConnectionManagerTest {

    private MobeelizerRealConnectionManager connectionManager;

    private MobeelizerApplication application;

    private HttpResponse httpResponse;

    private HttpPost httpPost;

    private HttpGet httpGet;

    private DefaultHttpClient httpClient;

    private HttpEntity httpEntity;

    private StatusLine httpStatusLine;

    private NetworkInfo networkWifiInfo;

    private MobeelizerInternalDatabase database;

    private ClientConnectionManager httpConnectionManager;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.when(Log.class, "i", anyString(), anyString()).thenReturn(0);
        PowerMockito.when(Log.class, "e", anyString(), anyString()).thenReturn(0);
        PowerMockito.when(Log.class, "e", anyString(), anyString(), any(Throwable.class)).thenReturn(0);

        PowerMockito.mockStatic(Proxy.class);
        PowerMockito.when(Proxy.class, "getHost", any(Context.class)).thenReturn(null);
        PowerMockito.when(Proxy.class, "getPort", any(Context.class)).thenReturn(-1);

        httpClient = PowerMockito.mock(DefaultHttpClient.class);
        whenNew(DefaultHttpClient.class).withArguments(any(ClientConnectionManager.class), any(BasicHttpParams.class))
                .thenReturn(httpClient);

        httpConnectionManager = PowerMockito.mock(ClientConnectionManager.class);
        when(httpClient.getConnectionManager()).thenReturn(httpConnectionManager);

        httpGet = PowerMockito.mock(HttpGet.class);
        whenNew(HttpGet.class).withArguments(anyString()).thenReturn(httpGet);

        httpPost = PowerMockito.mock(HttpPost.class);
        whenNew(HttpPost.class).withArguments(anyString()).thenReturn(httpPost);

        httpResponse = PowerMockito.mock(HttpResponse.class);
        when(httpClient.execute(httpGet)).thenReturn(httpResponse);
        when(httpClient.execute(httpPost)).thenReturn(httpResponse);

        httpEntity = PowerMockito.mock(HttpEntity.class);
        when(httpResponse.getEntity()).thenReturn(httpEntity);

        httpStatusLine = PowerMockito.mock(StatusLine.class);
        when(httpResponse.getStatusLine()).thenReturn(httpStatusLine);
        when(httpStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        application = mock(MobeelizerApplication.class);
        when(application.getVendor()).thenReturn("vendor");
        when(application.getApplication()).thenReturn("application");
        when(application.getInstance()).thenReturn("instance");
        when(application.getDevice()).thenReturn("device");
        when(application.getDeviceIdentifier()).thenReturn("deviceIdentifier");
        when(application.getUser()).thenReturn("user");
        when(application.getPassword()).thenReturn("password");
        when(application.getUrl()).thenReturn("http://url/app");

        Context context = PowerMockito.mock(Context.class);
        when(application.getContext()).thenReturn(context);

        ConnectivityManager connectivityService = PowerMockito.mock(ConnectivityManager.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityService);

        networkWifiInfo = PowerMockito.mock(NetworkInfo.class);
        when(connectivityService.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).thenReturn(networkWifiInfo);
        when(networkWifiInfo.isConnected()).thenReturn(true);

        NetworkInfo networkMobileInfo = PowerMockito.mock(NetworkInfo.class);
        when(connectivityService.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).thenReturn(networkMobileInfo);

        database = mock(MobeelizerInternalDatabase.class);
        when(application.getInternalDatabase()).thenReturn(database);

        connectionManager = new MobeelizerRealConnectionManager(application);
    }

    @Test
    public void shouldAuthenticateAndReturnRole() throws Exception {
        // given
        when(httpEntity.getContent()).thenReturn(
                new ByteArrayInputStream("{'content':{'role':'role','instanceGuid':'0000'},'status':'OK'}".replaceAll("'", "\"")
                        .getBytes()));

        // when
        MobeelizerLoginResponse response = connectionManager.login();

        // then
        verifyNew(HttpGet.class).withArguments("http://url/app/authenticate");

        verify(httpGet).setHeader("content-type", "application/json");
        verify(httpGet).setHeader("mas-vendor-name", "vendor");
        verify(httpGet).setHeader("mas-application-name", "application");
        verify(httpGet).setHeader("mas-application-instance-name", "instance");
        verify(httpGet).setHeader("mas-device-name", "device");
        verify(httpGet).setHeader("mas-device-identifier", "deviceIdentifier");
        verify(httpGet).setHeader("mas-user-name", "user");
        verify(httpGet).setHeader("mas-user-password", "password");
        verify(httpConnectionManager).shutdown();

        verify(database).setRoleAndInstanceGuid("instance", "user", "password", "role", "0000");

        assertEquals("role", response.getRole());
        assertEquals("0000", response.getInstanceGuid());
        assertEquals(MobeelizerLoginStatus.OK, response.getStatus());
    }

    @Test
    public void shouldReturnAuthenticateFailure() throws Exception {
        // given
        when(httpEntity.getContent()).thenReturn(
                new ByteArrayInputStream(
                        "{'content':{'message':'Authentication failure','arguments':null,'messageCode':'authenticationFailure'},'status':'ERROR'}"
                                .replaceAll("'", "\"").getBytes()));

        // when
        MobeelizerLoginResponse response = connectionManager.login();

        // then
        verify(database).clearRoleAndInstanceGuid("instance", "user");

        assertNull(response.getRole());
        assertEquals(MobeelizerLoginStatus.AUTHENTICATION_FAILURE, response.getStatus());
    }

    @Test
    public void shouldReturnOtherFailure() throws Exception {
        // given
        when(httpEntity.getContent()).thenReturn(
                new ByteArrayInputStream(
                        "{'content':{'message':'XXX','arguments':null,'messageCode':'vendorNotFound'},'status':'ERROR'}"
                                .replaceAll("'", "\"").getBytes()));
        when(database.getRoleAndInstanceGuid("instance", "user", "password")).thenReturn(new String[2]);

        // when
        MobeelizerLoginResponse response = connectionManager.login();

        // then
        assertNull(response.getRole());
        assertNull(response.getInstanceGuid());
        assertEquals(MobeelizerLoginStatus.CONNECTION_FAILURE, response.getStatus());
    }

    @Test
    public void shouldReturnConnectionFailure() throws Exception {
        // given
        when(httpStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_NOT_FOUND);
        when(database.getRoleAndInstanceGuid("instance", "user", "password")).thenReturn(new String[2]);

        // when
        MobeelizerLoginResponse response = connectionManager.login();

        // then
        assertNull(response.getRole());
        assertNull(response.getInstanceGuid());
        assertEquals(MobeelizerLoginStatus.CONNECTION_FAILURE, response.getStatus());
    }

    @Test
    public void shouldReturnRoleWithConnectionFailure() throws Exception {
        // given
        when(httpStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_NOT_FOUND);
        when(database.getRoleAndInstanceGuid("instance", "user", "password")).thenReturn(new String[] { "role", "0000" });

        // when
        MobeelizerLoginResponse response = connectionManager.login();

        // then
        assertEquals("role", response.getRole());
        assertEquals("0000", response.getInstanceGuid());
        assertEquals(MobeelizerLoginStatus.OK, response.getStatus());
    }

    @Test
    public void shouldReturnRoleWithMissingConnection() throws Exception {
        // given
        when(networkWifiInfo.isConnected()).thenReturn(false);
        when(database.getRoleAndInstanceGuid("instance", "user", "password")).thenReturn(new String[] { "role", "0000" });

        // when
        MobeelizerLoginResponse response = connectionManager.login();

        // then
        assertEquals("role", response.getRole());
        assertEquals("0000", response.getInstanceGuid());
        assertEquals(MobeelizerLoginStatus.OK, response.getStatus());
    }

    @Test
    public void shouldReturnMissingConnection() throws Exception {
        // given
        when(networkWifiInfo.isConnected()).thenReturn(false);
        when(database.getRoleAndInstanceGuid("instance", "user", "password")).thenReturn(new String[2]);

        // when
        MobeelizerLoginResponse response = connectionManager.login();

        // then
        assertNull(response.getRole());
        assertNull(response.getInstanceGuid());
        assertEquals(MobeelizerLoginStatus.MISSING_CONNECTION_FAILURE, response.getStatus());
    }

}
