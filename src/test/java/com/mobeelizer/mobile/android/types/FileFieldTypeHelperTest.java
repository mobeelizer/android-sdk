// 
// FileFieldTypeHelperTest.java
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

package com.mobeelizer.mobile.android.types;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.mobeelizer.mobile.android.MobeelizerErrorsImpl;
import com.mobeelizer.mobile.android.MobeelizerFileImpl;
import com.mobeelizer.mobile.android.TestEntity;
import com.mobeelizer.mobile.android.api.MobeelizerFile;
import com.mobeelizer.mobile.android.model.MobeelizerReflectionUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FileFieldTypeHelper.class, ContentValues.class, DatabaseUtils.class, MobeelizerFileImpl.class })
public class FileFieldTypeHelperTest {

    private Field fieldFile;

    private MobeelizerFile file;

    @Before
    public void init() {
        file = mock(MobeelizerFile.class);
        when(file.getGuid()).thenReturn("guid");
        when(file.getName()).thenReturn("name");

        fieldFile = MobeelizerReflectionUtil.getField(TestEntity.class, "file",
                new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { MobeelizerFile.class })));
    }

    @Test
    public void shouldInsertIntoEnum() throws Exception {
        // given
        Field helperField = FieldType.FILE.getClass().getDeclaredField("helper");
        helperField.setAccessible(true);
        Object helper = helperField.get(FieldType.FILE);

        // then
        assertTrue(helper instanceof FileFieldTypeHelper);
    }

    @Test
    public void shouldGetAccessibleTypes() throws Exception {
        // when
        Set<Class<?>> types = FieldType.FILE.getAccessibleTypes();

        // then
        assertEquals(1, types.size());
        assertTrue(types.contains(MobeelizerFile.class));
    }

    @Test
    public void shouldSetValueFromEntityToDatabase() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsImpl errors = mock(MobeelizerErrorsImpl.class);

        TestEntity entity = new TestEntity();
        entity.setFile(file);

        // when
        FieldType.FILE.setValueFromEntityToDatabase(values, entity, fieldFile, true, options, errors);

        // then
        verify(values).put("file_guid", "guid");
        verify(values).put("file_name", "name");
    }

    @Test
    public void shouldSetErrorWhileSetNullValueFromEntityToDatabase() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsImpl errors = mock(MobeelizerErrorsImpl.class);

        TestEntity entity = new TestEntity();

        // when
        FieldType.FILE.setValueFromEntityToDatabase(values, entity, fieldFile, true, options, errors);

        // then
        verify(values, never()).put(eq("file"), anyInt());
        verify(errors).addFieldCanNotBeEmpty("file");
    }

    @Test
    public void shouldSetValueFromDatabaseToEntity() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("file_guid")).thenReturn(13);
        when(cursor.getColumnIndex("file_name")).thenReturn(14);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getString(13)).thenReturn("guid");
        when(cursor.getString(14)).thenReturn("name");

        MobeelizerFileImpl file = mock(MobeelizerFileImpl.class);

        PowerMockito.whenNew(MobeelizerFileImpl.class).withArguments("name", "guid").thenReturn(file);

        // when
        FieldType.FILE.setValueFromDatabaseToEntity(cursor, entity, fieldFile, options);

        // then
        assertEquals(file, entity.getFile());
    }

    @Test
    public void shouldSetNullValueFromDatabaseToEntity() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("file_guid")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(true);

        // when
        FieldType.FILE.setValueFromDatabaseToEntity(cursor, entity, fieldFile, options);

        // then
        assertNull(entity.getFile());
    }

    @Test
    public void shouldGetDefinition() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        // when
        String[] definition = FieldType.FILE.getDefinition(fieldFile, true, null, options);

        // then
        assertEquals(2, definition.length);
        assertEquals("file_guid TEXT(36) NOT NULL REFERENCES _files(_guid)", definition[0]);
        assertEquals("file_name TEXT(255) NOT NULL", definition[1]);
    }

    @Test
    public void shouldGetDefinition2() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        // when
        String[] definition = FieldType.FILE.getDefinition(fieldFile, false, null, options);

        // then
        assertEquals(2, definition.length);
        assertEquals("file_guid TEXT(36) REFERENCES _files(_guid)", definition[0]);
        assertEquals("file_name TEXT(255)", definition[1]);
    }

    @Test
    public void shouldConvertNullDefaultValue() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.FILE.convertDefaultValue(fieldFile, null, options);

        // then
        assertNull(defaultValue);
    }

    @Test
    public void shouldConvertDefaultValue2() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.FILE.convertDefaultValue(fieldFile, "value", options);

        // then
        assertNull(defaultValue);
    }

    @Test
    public void shouldSetNullValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsImpl errors = mock(MobeelizerErrorsImpl.class);

        TestEntity entity = new TestEntity();

        // when
        FieldType.FILE.setValueFromEntityToDatabase(values, entity, fieldFile, false, options, errors);

        // then
        verify(values).put("file_guid", (String) null);
        verify(values).put("file_name", (String) null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetNullValueFromDatabaseToMap() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("file_guid")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(true);

        Map<String, String> map = mock(Map.class);
        // when
        FieldType.FILE.setValueFromDatabaseToMap(cursor, map, fieldFile, options);

        // then
        verify(map).put("file", null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetNotNullValueFromDatabaseToMap() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("file_guid")).thenReturn(13);
        when(cursor.getColumnIndex("file_name")).thenReturn(14);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getString(13)).thenReturn("guid");
        when(cursor.getString(14)).thenReturn("name");

        Map<String, String> map = mock(Map.class);

        // when
        FieldType.FILE.setValueFromDatabaseToMap(cursor, map, fieldFile, options);

        // then
        verify(map).put("file", "{'guid':'guid','filename':'name'}".replaceAll("'", "\""));
    }

    @Test
    public void shouldSetNullValueFromMapToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsImpl errors = mock(MobeelizerErrorsImpl.class);

        Map<String, String> map = new HashMap<String, String>();

        // when
        FieldType.FILE.setValueFromMapToDatabase(values, map, fieldFile, false, options, errors);

        // then
        verify(values).put("file_guid", (String) null);
        verify(values).put("file_name", (String) null);
    }

    @Test
    public void shouldSetNotNullValueFromMapToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsImpl errors = mock(MobeelizerErrorsImpl.class);

        Map<String, String> map = new HashMap<String, String>();
        map.put("file", "{'guid':'guid','filename':'name'}".replaceAll("'", "\""));

        // when
        FieldType.FILE.setValueFromMapToDatabase(values, map, fieldFile, false, options, errors);

        // then
        verify(values).put("file_guid", "guid");
        verify(values).put("file_name", "name");
    }

}
