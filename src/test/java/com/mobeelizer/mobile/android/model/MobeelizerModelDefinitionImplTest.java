// 
// MobeelizerModelDefinitionImplTest.java
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

package com.mobeelizer.mobile.android.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobeelizer.java.api.MobeelizerCredential;
import com.mobeelizer.java.api.MobeelizerField;
import com.mobeelizer.java.definition.MobeelizerErrorsHolder;
import com.mobeelizer.java.definition.MobeelizerModelCredentialsDefinition;
import com.mobeelizer.java.model.MobeelizerFieldImpl;
import com.mobeelizer.java.model.MobeelizerModelImpl;
import com.mobeelizer.java.sync.MobeelizerJsonEntity;
import com.mobeelizer.java.sync.MobeelizerJsonEntity.ConflictState;
import com.mobeelizer.mobile.android.TestEntity;
import com.mobeelizer.mobile.android.TestSimpleEntity;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MobeelizerAndroidModel.class, ContentValues.class, Log.class, DatabaseUtils.class, HashMap.class,
        MobeelizerAndroidField.class, MobeelizerErrorsHolder.class, MobeelizerFieldImpl.class })
public class MobeelizerModelDefinitionImplTest {

    private MobeelizerFieldImpl field;

    private MobeelizerAndroidField field2;

    private MobeelizerAndroidModel definition;

    private MobeelizerAndroidModel simpleDefinition;

    private SQLiteDatabase database;

    private ContentValues values;

    private UUID uuid;

    private ContentValues deletedValues;

    private Set<MobeelizerField> fields;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.when(Log.class, "i", anyString(), anyString()).thenReturn(0);

        field = PowerMockito.mock(MobeelizerFieldImpl.class);
        when(field.getName()).thenReturn("field");

        field2 = mock(MobeelizerAndroidField.class);
        when(field2.getName()).thenReturn("field");

        PowerMockito.whenNew(MobeelizerAndroidField.class).withArguments(field).thenReturn(field2);

        fields = new HashSet<MobeelizerField>();
        fields.add(field);

        database = mock(SQLiteDatabase.class);

        deletedValues = mock(ContentValues.class);
        values = mock(ContentValues.class);
        PowerMockito.whenNew(ContentValues.class).withNoArguments().thenReturn(deletedValues, values);

        MobeelizerModelCredentialsDefinition credentials = mock(MobeelizerModelCredentialsDefinition.class);
        when(credentials.getCreateAllowed()).thenReturn(MobeelizerCredential.ALL);
        when(credentials.getUpdateAllowed()).thenReturn(MobeelizerCredential.ALL);
        when(credentials.getDeleteAllowed()).thenReturn(MobeelizerCredential.ALL);
        when(credentials.getReadAllowed()).thenReturn(MobeelizerCredential.ALL);

        definition = new MobeelizerAndroidModel(new MobeelizerModelImpl(TestEntity.class, "testentity", credentials, fields));
        simpleDefinition = new MobeelizerAndroidModel(new MobeelizerModelImpl(TestSimpleEntity.class, "simpleModelName",
                credentials, fields));

        uuid = UUID.randomUUID();

        PowerMockito.mockStatic(UUID.class);
        PowerMockito.when(UUID.randomUUID()).thenReturn(uuid);
    }

    @Test
    public void shouldGetName() throws Exception {
        // when
        String name = definition.getName();

        // then
        assertEquals("testentity", name);
    }

    @Test
    public void shouldGetTableName() throws Exception {
        // when
        String name = definition.getTableName();

        // then
        assertEquals("testentity", name);
    }

    @Test
    public void shouldGetFields() throws Exception {
        // when
        Set<MobeelizerField> actualFields = definition.getFields();

        // then
        assertEquals(fields, actualFields);
    }

    @Test
    public void shouldGetClass() throws Exception {
        // when
        Class<?> clazz = definition.getMappingClass();

        // then
        assertEquals(TestEntity.class, clazz);
    }

    @Test
    public void shouldExists() throws Exception {
        // given
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(true);

        whenGetByGuid("testentity", "guid").thenReturn(cursor);

        // when
        boolean exists = definition.exists(database, "guid");

        // then
        assertTrue(exists);
        verify(cursor).close();
    }

    @Test
    public void shouldEntityExists() throws Exception {
        // given
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(true);

        whenGetByGuid("testentity", "guid").thenReturn(cursor);

        TestEntity entity = new TestEntity();
        entity.setGuid("guid");

        // when
        boolean exists = definition.exists(database, entity);

        // then
        assertTrue(exists);
        verify(cursor).close();
    }

    @Test
    public void shouldNotExists() throws Exception {
        // given
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(false);

        whenGetByGuid("testentity", "guid").thenReturn(cursor);

        // when
        boolean exists = definition.exists(database, "guid");

        // then
        assertFalse(exists);
        verify(cursor).close();
    }

    @Test
    public void shouldEntityNotExists() throws Exception {
        // given
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(false);

        whenGetByGuid("testentity", "guid").thenReturn(cursor);

        TestEntity entity = new TestEntity();
        entity.setGuid("guid");

        // when
        boolean exists = definition.exists(database, entity);

        // then
        assertFalse(exists);
        verify(cursor).close();
    }

    @Test
    public void shouldEntityWithoutGuidNotExists() throws Exception {
        // given
        TestEntity entity = new TestEntity();

        // when
        boolean exists = definition.exists(database, entity);

        // then
        assertFalse(exists);
    }

    @Test
    public void shouldUpdate() throws Exception {
        // given
        TestEntity entity = new TestEntity();
        entity.setGuid("guid");

        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);
        when(errors.isValid()).thenReturn(true);

        // when
        definition.update(database, entity, errors);

        // then
        verify(values, times(2)).put("_modified", 1);
        verify(field2).setValueFromEntityToDatabase(values, entity, errors);
        verify(database).update("testentity", values, "_guid = ?", new String[] { "guid" });
        assertTrue(entity.isModified());
    }

    @Test
    public void shouldNotUpdate() throws Exception {
        // given
        TestEntity entity = new TestEntity();
        entity.setGuid("guid");

        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);
        when(errors.isValid()).thenReturn(false);

        // when
        definition.update(database, entity, errors);

        // then
        verify(field2).setValueFromEntityToDatabase(values, entity, errors);
        verify(database, never()).update("testentity", values, "_guid = ?", new String[] { "guid" });
        assertFalse(entity.isModified());
    }

    @Test
    public void shouldCreate() throws Exception {
        // given
        TestEntity entity = new TestEntity();

        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);
        when(errors.isValid()).thenReturn(true);

        // when
        definition.create(database, entity, "owner", errors);

        // then
        assertEquals(uuid.toString(), entity.getGuid());
        assertEquals("owner", entity.getOwner());
        verify(values).put("_guid", uuid.toString());
        verify(values, times(2)).put("_modified", 1);
        verify(values).put("_owner", "owner");
        verify(values).put("_conflicted", 0);
        verify(values).put("_deleted", 0);
        verify(field2).setValueFromEntityToDatabase(values, entity, errors);
        verify(database).insert("testentity", null, values);
        assertTrue(entity.isModified());
    }

    @Test
    public void shouldNotCreate() throws Exception {
        // given
        TestEntity entity = new TestEntity();

        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);
        when(errors.isValid()).thenReturn(false);

        // when
        definition.create(database, entity, "owner", errors);

        // then
        assertNull(entity.getGuid());
        assertNull(entity.getOwner());
        verify(field2).setValueFromEntityToDatabase(values, entity, errors);
        verify(database, never()).insert("testentity", null, values);
        assertFalse(entity.isModified());
    }

    @Test
    public void shouldGetNullWhenNotExists() throws Exception {
        // given
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(false);

        whenGetByGuid("testentity", "guid").thenReturn(cursor);

        // when
        TestEntity entity = definition.get(database, "guid");

        // then
        assertNull(entity);
        verify(cursor).close();
    }

    @Test
    public void shouldGet() throws Exception {
        // given
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(true);

        when(cursor.getColumnIndex("_guid")).thenReturn(1);
        when(cursor.getString(1)).thenReturn("guid");

        when(cursor.getColumnIndex("_owner")).thenReturn(2);
        when(cursor.getString(2)).thenReturn("owner");

        when(cursor.getColumnIndex("_conflicted")).thenReturn(3);
        when(cursor.getInt(3)).thenReturn(1);

        when(cursor.getColumnIndex("_modified")).thenReturn(4);
        when(cursor.getInt(4)).thenReturn(1);

        when(cursor.getColumnIndex("_deleted")).thenReturn(5);
        when(cursor.getInt(5)).thenReturn(1);

        whenGetByGuid("testentity", "guid").thenReturn(cursor);

        // when
        TestEntity entity = definition.get(database, "guid");

        // then
        assertNotNull(entity);
        assertEquals("guid", entity.getGuid());
        assertEquals("owner", entity.getOwner());
        assertTrue(entity.isConflicted());
        assertTrue(entity.isModified());
        assertTrue(entity.isDeleted());
        verify(field2).setValueFromDatabaseToEntity(cursor, entity);
        verify(cursor).close();
    }

    @Test
    public void shouldGetJson() throws Exception {
        // given
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(true);

        when(cursor.getColumnIndex("_guid")).thenReturn(1);
        when(cursor.getString(1)).thenReturn("guid");

        when(cursor.getColumnIndex("_owner")).thenReturn(2);
        when(cursor.getString(2)).thenReturn("owner");

        when(cursor.getColumnIndex("_deleted")).thenReturn(3);
        when(cursor.getInt(3)).thenReturn(1);

        HashMap<String, String> map = new HashMap<String, String>();
        PowerMockito.whenNew(HashMap.class).withNoArguments().thenReturn(map);

        // when
        MobeelizerJsonEntity entity = definition.getJsonEntity(cursor);

        // then
        assertNotNull(entity);
        assertEquals("guid", entity.getGuid());
        assertEquals("owner", entity.getOwner());
        assertEquals("testentity", entity.getModel());
        assertTrue(entity.isDeleted());
        assertSame(map, entity.getFields());
        verify(field2).setValueFromDatabaseToMap(cursor, map);
    }

    @Test
    public void shouldSimpleGetJson() throws Exception {
        // given
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(true);

        when(cursor.getColumnIndex("_guid")).thenReturn(1);
        when(cursor.getString(1)).thenReturn("guid");

        when(cursor.getColumnIndex("_owner")).thenReturn(2);
        when(cursor.getString(2)).thenReturn("owner");

        when(cursor.getColumnIndex("_deleted")).thenReturn(3);
        when(cursor.getInt(3)).thenReturn(1);

        HashMap<String, String> map = new HashMap<String, String>();
        PowerMockito.whenNew(HashMap.class).withNoArguments().thenReturn(map);

        // when
        MobeelizerJsonEntity entity = simpleDefinition.getJsonEntity(cursor);

        // then
        assertNotNull(entity);
        assertEquals("guid", entity.getGuid());
        assertEquals("owner", entity.getOwner());
        assertEquals("simpleModelName", entity.getModel());
        assertTrue(entity.isDeleted());
        assertSame(map, entity.getFields());
        verify(field2).setValueFromDatabaseToMap(cursor, map);
    }

    @Test
    public void shouldDelete() throws Exception {
        // given
        TestEntity entity = new TestEntity();
        entity.setGuid("guid");

        // when
        definition.delete(database, entity);

        // then
        verify(deletedValues).put("_modified", 1);
        verify(deletedValues).put("_deleted", 1);
        verify(database).update("testentity", deletedValues, "_guid = ? AND _deleted = 0", new String[] { "guid" });
    }

    @Test
    public void shouldCount() throws Exception {
        // given
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(true);
        when(cursor.getLong(0)).thenReturn(12L);

        when(
                database.query("testentity", new String[] { "count(*)" }, MobeelizerAndroidModel._DELETED + " = 0", null, null,
                        null, null)).thenReturn(cursor);

        // when
        long count = definition.count(database);

        // then
        assertEquals(12L, count);
        verify(cursor).close();
    }

    @Test
    public void shouldDeleteByGuid() throws Exception {
        // when
        definition.deleteByGuid(database, "guid");

        // then
        verify(deletedValues).put("_modified", 1);
        verify(deletedValues).put("_deleted", 1);
        verify(database).update("testentity", deletedValues, "_guid = ? AND _deleted = 0", new String[] { "guid" });
    }

    @Test
    public void shouldDeleteAll() throws Exception {
        // when
        definition.deleteAll(database);

        // then
        verify(deletedValues).put("_modified", 1);
        verify(deletedValues).put("_deleted", 1);
        verify(database).update("testentity", deletedValues, "_deleted = 0", null);
    }

    @Test
    public void shouldList() throws Exception {
        // given
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(true, true, false);

        when(cursor.getColumnIndex("_guid")).thenReturn(1);
        when(cursor.getString(1)).thenReturn("guid1", "guid2");

        when(cursor.getColumnIndex("_owner")).thenReturn(2);
        when(cursor.getString(2)).thenReturn("owner1", "owner2");

        when(cursor.getColumnIndex("_conflicted")).thenReturn(3);
        when(cursor.getInt(3)).thenReturn(1, 0);

        when(database.query("testentity", null, "_deleted = 0", null, null, null, null)).thenReturn(cursor);

        // when
        List<TestEntity> entities = definition.list(database);

        // then
        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertEquals("guid1", entities.get(0).getGuid());
        assertEquals("owner1", entities.get(0).getOwner());
        assertTrue(entities.get(0).isConflicted());
        verify(field2).setValueFromDatabaseToEntity(cursor, entities.get(0));
        assertEquals("guid2", entities.get(1).getGuid());
        assertEquals("owner2", entities.get(1).getOwner());
        assertFalse(entities.get(1).isConflicted());
        verify(field2).setValueFromDatabaseToEntity(cursor, entities.get(1));
        verify(cursor).close();
    }

    @Test
    public void shouldListEmpty() throws Exception {
        // given
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext()).thenReturn(false);

        when(database.query("testentity", null, "_deleted = 0", null, null, null, null)).thenReturn(cursor);

        // when
        List<TestEntity> entities = definition.list(database);

        // then
        assertNotNull(entities);
        assertTrue(entities.isEmpty());
    }

    @Test
    public void shouldOnCreate() throws Exception {
        // given
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE testentity (");
        sql.append("_guid TEXT(36) PRIMARY KEY, ");
        sql.append("_owner TEXT(255) NOT NULL, ");
        sql.append("_deleted INTEGER(1) NOT NULL DEFAULT 0, ");
        sql.append("_modified INTEGER(1) NOT NULL DEFAULT 0, ");
        sql.append("_conflicted INTEGER(1) NOT NULL DEFAULT 0, ");
        sql.append("field1_definition, ");
        sql.append("field2_definition);");

        when(field2.getDefinition()).thenReturn(new String[] { "field1_definition", "field2_definition" });

        // when
        definition.onCreate(database);

        // then
        verify(database).execSQL(sql.toString());
    }

    @Test
    public void shouldOnUpdate() throws Exception {
        // given
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE testentity (");
        sql.append("_guid TEXT(36) PRIMARY KEY, ");
        sql.append("_owner TEXT(255) NOT NULL, ");
        sql.append("_deleted INTEGER(1) NOT NULL DEFAULT 0, ");
        sql.append("_modified INTEGER(1) NOT NULL DEFAULT 0, ");
        sql.append("_conflicted INTEGER(1) NOT NULL DEFAULT 0, ");
        sql.append("field1_definition, ");
        sql.append("field2_definition);");

        when(field2.getDefinition()).thenReturn(new String[] { "field1_definition", "field2_definition" });

        // when
        definition.onUpgrade(database);

        // then
        verify(database).execSQL("DROP TABLE IF EXISTS testentity");
        verify(database).execSQL(sql.toString());
    }

    @Test
    public void shouldLockModifiedFlag() throws Exception {
        // when
        definition.lockModifiedFlag(database);

        // then
        verify(values).put("_modified", 2);
        verify(database).update("testentity", values, "_modified = 1", null);
    }

    @Test
    public void shouldUnlockModifiedFlag() throws Exception {
        // when
        definition.unlockModifiedFlag(database);

        // then
        verify(values, times(2)).put("_modified", 1);
        verify(database).update("testentity", values, "_modified = 2", null);
    }

    @Test
    public void shouldClearModifiedFlag() throws Exception {
        // when
        definition.clearModifiedFlag(database);

        // then
        verify(values).put("_modified", 0);
        verify(database).update("testentity", values, "_modified = 2", null);
    }

    @Test
    public void shouldClearData() throws Exception {
        // when
        definition.clearData(database);

        // then
        verify(database).delete("testentity", null, null);
    }

    @Test
    public void shouldGetEntitiesToSync() throws Exception {
        // given
        Cursor expectedCursor = mock(Cursor.class);

        when(database.query("testentity", null, "_modified = 2", null, null, null, null)).thenReturn(expectedCursor);

        // when
        Cursor cursor = definition.getEntitiesToSync(database);

        // then
        assertSame(expectedCursor, cursor);
    }

    @Test
    public void shouldIgnoreSyncUpdateWhenUserModifiedEntity() throws Exception {
        // given
        MobeelizerJsonEntity entity = new MobeelizerJsonEntity();
        entity.setGuid("guid");
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("s_deleted", "false");
        entity.setFields(fields);

        Cursor cursor = mock(Cursor.class);
        whenGetByGuidWithDeleted("testentity", "guid").thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(true);
        when(cursor.getColumnIndex("_modified")).thenReturn(12);
        when(cursor.getInt(12)).thenReturn(1);

        // when
        boolean isTransactionSuccessful = definition.updateEntityFromSync(database, entity);

        // then
        verify(cursor).close();
        verify(database, never()).insert(eq("testentity"), eq((String) null), any(ContentValues.class));
        verify(database, never()).update(eq("testentity"), any(ContentValues.class), any(String.class), any(String[].class));
        assertTrue(isTransactionSuccessful);
    }

    @Test
    public void shouldIgnoreSyncUpdateWhenNewEntityIsDeleted() throws Exception {
        // given
        MobeelizerJsonEntity entity = new MobeelizerJsonEntity();
        entity.setGuid("guid");
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("s_deleted", "true");
        entity.setFields(fields);

        Cursor cursor = mock(Cursor.class);
        whenGetByGuidWithDeleted("testentity", "guid").thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(false);

        // when
        boolean isTransactionSuccessful = definition.updateEntityFromSync(database, entity);

        // then
        verify(cursor).close();
        verify(database, never()).insert(eq("testentity"), eq((String) null), any(ContentValues.class));
        verify(database, never()).update(eq("testentity"), any(ContentValues.class), any(String.class), any(String[].class));
        assertTrue(isTransactionSuccessful);
    }

    @Test
    public void shouldInterruptSyncUpdateWhenEntityIsNotValid() throws Exception {
        // given
        MobeelizerJsonEntity entity = new MobeelizerJsonEntity();
        entity.setGuid("guid");
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("s_deleted", "false");
        entity.setFields(fields);

        Cursor cursor = mock(Cursor.class);
        whenGetByGuidWithDeleted("testentity", "guid").thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(false);

        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);
        PowerMockito.whenNew(MobeelizerErrorsHolder.class).withNoArguments().thenReturn(errors);
        when(errors.isValid()).thenReturn(false);

        // when
        boolean isTransactionSuccessful = definition.updateEntityFromSync(database, entity);

        // then
        verify(cursor).close();
        verify(database, never()).insert(eq("testentity"), eq((String) null), any(ContentValues.class));
        verify(database, never()).update(eq("testentity"), any(ContentValues.class), any(String.class), any(String[].class));
        assertFalse(isTransactionSuccessful);
    }

    @Test
    public void shouldChangeConflictFlagOnlyWhenConflictIsBecauseOfCurrentUser() throws Exception {
        // given
        MobeelizerJsonEntity entity = new MobeelizerJsonEntity();
        entity.setGuid("guid");
        entity.setOwner("owner");
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("s_deleted", "false");
        entity.setFields(fields);
        entity.setConflictState(ConflictState.IN_CONFLICT_BECAUSE_OF_YOU);

        Cursor cursor = mock(Cursor.class);
        whenGetByGuidWithDeleted("testentity", "guid").thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(true);
        when(cursor.getColumnIndex("_modified")).thenReturn(12);
        when(cursor.getInt(12)).thenReturn(2);

        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        // when
        boolean isTransactionSuccessful = definition.updateEntityFromSync(database, entity);

        // then
        verify(cursor).close();
        verify(values).put("_conflicted", 1);
        verify(values).put("_modified", 0);
        verify(values, never()).put("_guid", "guid");
        verify(values).put(eq("_deleted"), anyInt());
        verify(values, never()).put("_owner", "owner");
        verify(field2, never()).setValueFromMapToDatabase(values, fields, errors);
        verify(database).update(eq("testentity"), eq(values), eq("_guid = ?"), eq(new String[] { "guid" }));
        verify(database, never()).insert(eq("testentity"), eq((String) null), any(ContentValues.class));
        assertTrue(isTransactionSuccessful);
    }

    @Test
    public void shouldUpdateEntityFromSync() throws Exception {
        // given
        MobeelizerJsonEntity entity = new MobeelizerJsonEntity();
        entity.setGuid("guid");
        entity.setOwner("owner");
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("s_deleted", "true");
        entity.setFields(fields);
        entity.setConflictState(ConflictState.IN_CONFLICT);

        Cursor cursor = mock(Cursor.class);
        whenGetByGuidWithDeleted("testentity", "guid").thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(true);
        when(cursor.getColumnIndex("_modified")).thenReturn(12);
        when(cursor.getInt(12)).thenReturn(2);

        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);
        PowerMockito.whenNew(MobeelizerErrorsHolder.class).withNoArguments().thenReturn(errors);
        when(errors.isValid()).thenReturn(true);

        // when
        boolean isTransactionSuccessful = definition.updateEntityFromSync(database, entity);

        // then
        verify(cursor).close();
        verify(values, never()).put("_guid", "guid");
        verify(values).put("_conflicted", 1);
        verify(values).put("_modified", 0);
        verify(values, times(2)).put("_deleted", 1);
        verify(values).put("_owner", "owner");
        verify(field2).setValueFromMapToDatabase(values, fields, errors);
        verify(database).update(eq("testentity"), eq(values), eq("_guid = ?"), eq(new String[] { "guid" }));
        verify(database, never()).insert(eq("testentity"), eq((String) null), any(ContentValues.class));
        assertTrue(isTransactionSuccessful);
    }

    @Test
    public void shouldCreateEntityFromSync() throws Exception {
        // given
        MobeelizerJsonEntity entity = new MobeelizerJsonEntity();
        entity.setGuid("guid");
        entity.setOwner("owner");
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("s_deleted", "false");
        entity.setFields(fields);
        entity.setConflictState(ConflictState.NO_IN_CONFLICT);

        Cursor cursor = mock(Cursor.class);
        whenGetByGuidWithDeleted("testentity", "guid").thenReturn(cursor);
        when(cursor.moveToNext()).thenReturn(false);

        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);
        PowerMockito.whenNew(MobeelizerErrorsHolder.class).withNoArguments().thenReturn(errors);
        when(errors.isValid()).thenReturn(true);

        // when
        boolean isTransactionSuccessful = definition.updateEntityFromSync(database, entity);

        // then
        verify(cursor).close();
        verify(values).put("_conflicted", 0);
        verify(values).put("_owner", "owner");
        verify(values).put("_modified", 0);
        verify(values).put("_deleted", 0);
        verify(values).put("_guid", "guid");
        verify(field2).setValueFromMapToDatabase(values, fields, errors);
        verify(database, never()).update(eq("testentity"), any(ContentValues.class), any(String.class), any(String[].class));
        verify(database).insert(eq("testentity"), eq((String) null), eq(values));
        assertTrue(isTransactionSuccessful);
    }

    private OngoingStubbing<Cursor> whenGetByGuid(final String entity, final String guid) {
        return when(database.query(entity, null, "_guid = ? AND _deleted = 0", new String[] { guid }, null, null, null));
    }

    private OngoingStubbing<Cursor> whenGetByGuidWithDeleted(final String entity, final String guid) {
        return when(database.query(entity, null, "_guid = ?", new String[] { guid }, null, null, null));
    }

}
