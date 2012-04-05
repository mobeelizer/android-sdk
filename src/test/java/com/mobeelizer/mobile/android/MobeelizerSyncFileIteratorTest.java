// 
// MobeelizerSyncFileIteratorTest.java
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.database.Cursor;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Cursor.class, MobeelizerSyncFileIterator.class, FileInputStream.class, File.class })
public class MobeelizerSyncFileIteratorTest {

    private MobeelizerSyncFileIterator iterator;

    private Cursor cursor;

    @Before
    public void init() {
        cursor = mock(Cursor.class);
        iterator = new MobeelizerSyncFileIterator(cursor);
    }

    @Test
    public void shouldHaveOneElement() throws Exception {
        // given
        when(cursor.moveToNext()).thenReturn(true, false);
        when(cursor.isClosed()).thenReturn(true);
        when(cursor.getColumnIndex("_guid")).thenReturn(12);
        when(cursor.getColumnIndex("_path")).thenReturn(13);
        when(cursor.getString(12)).thenReturn("guid");
        when(cursor.getString(13)).thenReturn("path");

        FileInputStream stream = mock(FileInputStream.class);
        File file = mock(File.class);

        PowerMockito.whenNew(File.class).withArguments("path").thenReturn(file);
        PowerMockito.whenNew(FileInputStream.class).withArguments(file).thenReturn(stream);

        // when
        assertTrue(iterator.hasNext());

        assertEquals("guid", iterator.next());
        assertEquals(stream, iterator.getStream());

        assertFalse(iterator.hasNext());

        iterator.close();

        verify(cursor).close();
    }

    @Test
    public void shouldHaveTwoElements() throws Exception {
        // given
        when(cursor.moveToNext()).thenReturn(true, true, false);
        when(cursor.isClosed()).thenReturn(true);
        when(cursor.getColumnIndex("_guid")).thenReturn(12);
        when(cursor.getColumnIndex("_path")).thenReturn(13);
        when(cursor.getString(12)).thenReturn("guid", "guid2");
        when(cursor.getString(13)).thenReturn("path", "path2");

        FileInputStream stream = mock(FileInputStream.class);
        FileInputStream stream2 = mock(FileInputStream.class);
        File file = mock(File.class);
        File file2 = mock(File.class);

        PowerMockito.whenNew(File.class).withArguments("path").thenReturn(file);
        PowerMockito.whenNew(File.class).withArguments("path2").thenReturn(file2);
        PowerMockito.whenNew(FileInputStream.class).withArguments(file).thenReturn(stream);
        PowerMockito.whenNew(FileInputStream.class).withArguments(file2).thenReturn(stream2);

        // when
        assertTrue(iterator.hasNext());

        assertEquals("guid", iterator.next());
        assertEquals(stream, iterator.getStream());

        assertTrue(iterator.hasNext());

        assertEquals("guid2", iterator.next());
        assertEquals(stream2, iterator.getStream());

        assertFalse(iterator.hasNext());

        iterator.close();

        verify(cursor).close();
    }

    @Test
    public void shouldBeEmpty() throws Exception {
        // given
        when(cursor.moveToNext()).thenReturn(false);
        when(cursor.isClosed()).thenReturn(true);

        // when
        assertFalse(iterator.hasNext());

        iterator.close();

        verify(cursor).close();
    }

}
