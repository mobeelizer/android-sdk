// 
// MobeelizerSyncIteratorTest.java
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobeelizer.mobile.android.model.MobeelizerModelDefinitionImpl;
import com.mobeelizer.mobile.android.sync.MobeelizerJsonEntity;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MobeelizerSyncIterator.class, Cursor.class })
public class MobeelizerSyncIteratorTest {

    private Iterator<MobeelizerModelDefinitionImpl> iterator;

    private SQLiteDatabase database;

    private MobeelizerSyncIterator syncIterator;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        database = mock(SQLiteDatabase.class);
        Collection<MobeelizerModelDefinitionImpl> models = mock(Collection.class);
        iterator = mock(Iterator.class);
        when(models.iterator()).thenReturn(iterator);
        syncIterator = new MobeelizerSyncIterator(database, models);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionOnRemove() throws Exception {
        // when
        syncIterator.remove();
    }

    @Test
    public void shouldReturnNullIfEmpty() throws Exception {
        // when
        assertFalse(syncIterator.hasNext());
    }

    @Test
    public void shouldHaveEntities() throws Exception {
        // given
        MobeelizerModelDefinitionImpl model1 = mock(MobeelizerModelDefinitionImpl.class);
        MobeelizerModelDefinitionImpl model2 = mock(MobeelizerModelDefinitionImpl.class);

        when(iterator.hasNext()).thenReturn(true, true, false);
        when(iterator.next()).thenReturn(model1, model2);

        Cursor cursor1 = PowerMockito.mock(Cursor.class);
        Cursor cursor2 = PowerMockito.mock(Cursor.class);

        when(model1.getEntitiesToSync(database)).thenReturn(cursor1);
        when(model2.getEntitiesToSync(database)).thenReturn(cursor2);

        when(cursor1.moveToNext()).thenReturn(true, true, false);
        when(cursor2.moveToNext()).thenReturn(true, false);

        MobeelizerJsonEntity entity1 = mock(MobeelizerJsonEntity.class);
        MobeelizerJsonEntity entity2 = mock(MobeelizerJsonEntity.class);
        MobeelizerJsonEntity entity3 = mock(MobeelizerJsonEntity.class);

        when(model1.getJsonEntity(cursor1)).thenReturn(entity1, entity2);
        when(model2.getJsonEntity(cursor2)).thenReturn(entity3);

        // when
        assertTrue(syncIterator.hasNext());
        assertSame(entity1, syncIterator.next());
        assertTrue(syncIterator.hasNext());
        assertSame(entity2, syncIterator.next());
        assertTrue(syncIterator.hasNext());
        assertSame(entity3, syncIterator.next());
        assertFalse(syncIterator.hasNext());

        syncIterator.close();

        verify(cursor1).close();
        verify(cursor2).close();
    }

}
