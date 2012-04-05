// 
// MobeelizerFieldDefinitionImplTest.java
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
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
import android.util.Log;

import com.mobeelizer.mobile.android.MobeelizerErrorsImpl;
import com.mobeelizer.mobile.android.TestEntity;
import com.mobeelizer.mobile.android.api.MobeelizerCredential;
import com.mobeelizer.mobile.android.definition.MobeelizerModelFieldCredentialsDefinition;
import com.mobeelizer.mobile.android.definition.MobeelizerModelFieldDefinition;
import com.mobeelizer.mobile.android.types.FieldType;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MobeelizerFieldDefinitionImpl.class, ContentValues.class, Log.class, FieldType.class })
public class MobeelizerFieldDefinitionImplTest {

    private MobeelizerFieldDefinitionImpl definition;

    private ContentValues values;

    private FieldType type;

    private Map<String, String> options;

    private MobeelizerModelFieldDefinition radField;

    private MobeelizerModelFieldCredentialsDefinition credentials;

    @Before
    @SuppressWarnings("unchecked")
    public void init() throws Exception {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.when(Log.class, "i", anyString(), anyString()).thenReturn(0);

        values = mock(ContentValues.class);
        PowerMockito.whenNew(ContentValues.class).withNoArguments().thenReturn(values);

        options = mock(Map.class);

        type = PowerMockito.mock(FieldType.class);
        when(type.name()).thenReturn("typeName");
        Set<Class<?>> accessibleTypes = new HashSet<Class<?>>();
        accessibleTypes.add(String.class);
        accessibleTypes.add(Integer.TYPE);
        when(type.getAccessibleTypes()).thenReturn(accessibleTypes);
        when(type.convertDefaultValue(any(Field.class), eq("11"), any(Map.class))).thenReturn(11);
        when(type.convertDefaultValue(any(Field.class), eq("default"), any(Map.class))).thenReturn("default");

        PowerMockito.mockStatic(FieldType.class);
        PowerMockito.when(FieldType.valueOf("typeName")).thenReturn(type);
        PowerMockito.when(FieldType.valueOf("otherTypeName")).thenThrow(new IllegalArgumentException());

        radField = mock(MobeelizerModelFieldDefinition.class);
        when(radField.getName()).thenReturn("string");
        when(radField.getType()).thenReturn(type);
        when(radField.isRequired()).thenReturn(true);
        when(radField.getDefaultValue()).thenReturn("default");
        when(radField.getOptions()).thenReturn(options);

        credentials = mock(MobeelizerModelFieldCredentialsDefinition.class);
        when(credentials.getCreateAllowed()).thenReturn(MobeelizerCredential.ALL);
        when(credentials.getReadAllowed()).thenReturn(MobeelizerCredential.ALL);
        when(credentials.getUpdateAllowed()).thenReturn(MobeelizerCredential.ALL);

        definition = new MobeelizerFieldDefinitionImpl(TestEntity.class, radField, credentials);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfFieldNotFound() throws Exception {
        // given
        when(radField.getName()).thenReturn("notExistingFields");

        // when
        new MobeelizerFieldDefinitionImpl(TestEntity.class, radField, credentials);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfFieldHasWrongType() throws Exception {
        // given
        when(radField.getName()).thenReturn("booleanP");

        // when
        new MobeelizerFieldDefinitionImpl(TestEntity.class, radField, credentials);
    }

    @Test
    public void shouldGetName() throws Exception {
        // when
        String name = definition.getName();

        // then
        assertEquals("string", name);
    }

    @Test
    public void shouldGetDefaultValue() throws Exception {
        // when
        Object defaultValue = definition.getDefaultValue();

        // then
        assertEquals("default", defaultValue);
    }

    @Test
    public void shouldBeRequired() throws Exception {
        // when
        boolean required = definition.isRequired();

        // then
        assertTrue(required);
    }

    @Test
    public void shouldNotBeRequired() throws Exception {
        // given
        when(radField.isRequired()).thenReturn(false);
        definition = new MobeelizerFieldDefinitionImpl(TestEntity.class, radField, credentials);

        // when
        boolean required = definition.isRequired();

        // then
        assertFalse(required);
    }

    @Test
    public void shouldSetEntityValue() throws Exception {
        // given
        TestEntity entity = new TestEntity();
        entity.setString("nameValue");
        MobeelizerErrorsImpl errors = mock(MobeelizerErrorsImpl.class);

        // when
        definition.setValueFromEntityToDatabase(values, entity, errors);

        // then
        verify(type).setValueFromEntityToDatabase(eq(values), eq(entity), any(Field.class), eq(true), eq(options), eq(errors));
    }

    @Test
    public void shouldSetEntityValueFromMap() throws Exception {
        // given
        MobeelizerErrorsImpl errors = mock(MobeelizerErrorsImpl.class);

        Map<String, String> map = new HashMap<String, String>();
        map.put("string", "nameValue");

        // when
        definition.setValueFromMapToDatabase(values, map, errors);

        // then
        verify(type).setValueFromMapToDatabase(eq(values), eq(map), any(Field.class), eq(true), eq(options), eq(errors));
    }

    @Test
    public void shouldSetEntityOtherValue() throws Exception {
        // given
        when(radField.getName()).thenReturn("integerP");
        when(radField.isRequired()).thenReturn(false);
        when(radField.getDefaultValue()).thenReturn("11");

        MobeelizerFieldDefinitionImpl definition = new MobeelizerFieldDefinitionImpl(TestEntity.class, radField, credentials);

        TestEntity entity = new TestEntity();
        entity.setIntegerP(12);

        MobeelizerErrorsImpl errors = mock(MobeelizerErrorsImpl.class);

        // when
        definition.setValueFromEntityToDatabase(values, entity, errors);

        // then
        verify(type).setValueFromEntityToDatabase(eq(values), eq(entity), any(Field.class), eq(false), eq(options), eq(errors));
    }

    @Test
    public void shouldSetDatabaseValue() throws Exception {
        // given
        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);

        // when
        definition.setValueFromDatabaseToEntity(cursor, entity);

        // then
        verify(type).setValueFromDatabaseToEntity(eq(cursor), eq(entity), any(Field.class), eq(options));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetDatabaseValueToMap() throws Exception {
        // given
        Cursor cursor = mock(Cursor.class);

        Map<String, String> map = mock(Map.class);

        // when
        definition.setValueFromDatabaseToMap(cursor, map);

        // then
        verify(type).setValueFromDatabaseToMap(eq(cursor), eq(map), any(Field.class), eq(options));
    }

    @Test
    public void shouldGetDefinition() throws Exception {
        // given
        String[] expectedDefinition = new String[2];
        when(type.getDefinition(any(Field.class), eq(true), eq("default"), eq(options))).thenReturn(expectedDefinition);

        // when
        String[] actualDefinition = definition.getDefinition();

        // then
        assertSame(expectedDefinition, actualDefinition);
    }

    @Test
    public void shouldGetOtherDefinition() throws Exception {
        // given
        when(radField.getName()).thenReturn("integerP");
        when(radField.isRequired()).thenReturn(false);
        when(radField.getDefaultValue()).thenReturn("11");

        MobeelizerFieldDefinitionImpl definition = new MobeelizerFieldDefinitionImpl(TestEntity.class, radField, credentials);

        String[] expectedDefinition = new String[2];
        when(type.getDefinition(any(Field.class), eq(false), eq(11), eq(options))).thenReturn(expectedDefinition);

        // when
        String[] actualDefinition = definition.getDefinition();

        // then
        assertSame(expectedDefinition, actualDefinition);
    }

}
