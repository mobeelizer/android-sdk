// 
// FieldTypeTest.java
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

import static junit.framework.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
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

import com.mobeelizer.java.definition.MobeelizerErrorsHolder;
import com.mobeelizer.java.definition.MobeelizerFieldType;
import com.mobeelizer.java.definition.type.helpers.MobeelizerFieldTypeHelper;
import com.mobeelizer.java.model.MobeelizerFieldAccessor;
import com.mobeelizer.mobile.android.TestEntity;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FieldType.class, MobeelizerFieldType.class, ContentValues.class })
public class FieldTypeTest {

    private FieldTypeHelper helper;

    private MobeelizerFieldTypeHelper helper2;

    @Before
    public void init() throws Exception {
        helper2 = mock(MobeelizerFieldTypeHelper.class);
        Field helper2Field = MobeelizerFieldType.TEXT.getClass().getDeclaredField("helper");
        helper2Field.setAccessible(true);
        helper2Field.set(MobeelizerFieldType.TEXT, helper2);

        helper = mock(FieldTypeHelper.class);
        Field helperField = FieldType.TEXT.getClass().getDeclaredField("helper");
        helperField.setAccessible(true);
        helperField.set(FieldType.TEXT, helper);

        when(helper.getType()).thenReturn(MobeelizerFieldType.TEXT);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldGetDefinition() throws Exception {
        // given
        String[] expectedDefinition = new String[2];
        MobeelizerFieldAccessor field = PowerMockito.mock(MobeelizerFieldAccessor.class);
        Map<String, String> options = mock(Map.class);

        when(helper.getDefinition(field, true, "default", options)).thenReturn(expectedDefinition);

        // when
        String[] definition = FieldType.TEXT.getDefinition(field, true, "default", options);

        // then
        assertSame(expectedDefinition, definition);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnAccessibleTypes() throws Exception {
        // given
        Set<Class<?>> expectedTypes = mock(Set.class);

        when(helper2.getAccessibleTypes()).thenReturn(expectedTypes);

        // when
        Set<Class<?>> types = FieldType.TEXT.getType().getAccessibleTypes();

        // then
        assertSame(expectedTypes, types);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetValueFromDatabaseToEntity() throws Exception {
        // given
        Cursor cursor = mock(Cursor.class);
        TestEntity entity = mock(TestEntity.class);
        MobeelizerFieldAccessor field = PowerMockito.mock(MobeelizerFieldAccessor.class);
        Map<String, String> options = mock(Map.class);

        // when
        FieldType.TEXT.setValueFromDatabaseToEntity(cursor, entity, field, options);

        // then
        verify(helper).setValueFromDatabaseToEntity(cursor, entity, field, options);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetValueFromEntityToDatabase() throws Exception {
        // given
        TestEntity entity = mock(TestEntity.class);
        MobeelizerFieldAccessor field = PowerMockito.mock(MobeelizerFieldAccessor.class);
        Map<String, String> options = mock(Map.class);
        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        // when
        FieldType.TEXT.setValueFromEntityToDatabase(values, entity, field, false, options, errors);

        // then
        verify(helper).setValueFromEntityToDatabase(values, entity, field, false, options, errors);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldConvertDefaultValue() throws Exception {
        // given
        Object expectedDefaultValue = new Object();
        MobeelizerFieldAccessor field = PowerMockito.mock(MobeelizerFieldAccessor.class);
        Map<String, String> options = mock(Map.class);

        when(helper2.convertDefaultValue(field, "otherDefault", options)).thenReturn(expectedDefaultValue);

        // when
        Object defaultValue = FieldType.TEXT.getType().convertDefaultValue(field, "otherDefault", options);

        // then
        assertSame(expectedDefaultValue, defaultValue);
    }
}
