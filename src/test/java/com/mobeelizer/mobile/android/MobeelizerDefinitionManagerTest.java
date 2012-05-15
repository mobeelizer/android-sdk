// 
// MobeelizerDefinitionConverterTest.java
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

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.mobeelizer.java.api.MobeelizerCredential;
import com.mobeelizer.java.api.MobeelizerField;
import com.mobeelizer.java.api.MobeelizerModel;
import com.mobeelizer.java.definition.MobeelizerApplicationDefinition;
import com.mobeelizer.java.definition.MobeelizerDefinitionConverter;
import com.mobeelizer.java.definition.MobeelizerModelCredentialsDefinition;
import com.mobeelizer.java.definition.MobeelizerModelDefinition;
import com.mobeelizer.java.definition.MobeelizerModelFieldCredentialsDefinition;
import com.mobeelizer.java.definition.MobeelizerModelFieldDefinition;
import com.mobeelizer.java.definition.MobeelizerRoleDefinition;
import com.mobeelizer.java.model.MobeelizerFieldImpl;
import com.mobeelizer.java.model.MobeelizerModelImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MobeelizerDefinitionConverter.class, Class.class })
public class MobeelizerDefinitionManagerTest {

    private MobeelizerDefinitionConverter definitionManager;

    private MobeelizerApplicationDefinition definition;

    private Method modelHasAccess;

    private Method fieldHasAccess;

    @Before
    public void init() {
        definitionManager = PowerMockito.spy(new MobeelizerDefinitionConverter());

        MobeelizerRoleDefinition role = mock(MobeelizerRoleDefinition.class);
        when(role.resolveName()).thenReturn("role");

        definition = mock(MobeelizerApplicationDefinition.class);
        when(definition.getModels()).thenReturn(Collections.<MobeelizerModelDefinition> emptySet());
        when(definition.getRoles()).thenReturn(Collections.<MobeelizerRoleDefinition> singleton(role));

        modelHasAccess = PowerMockito.method(MobeelizerDefinitionConverter.class, "hasAccess", MobeelizerModelDefinition.class,
                String.class);
        fieldHasAccess = PowerMockito.method(MobeelizerDefinitionConverter.class, "hasAccess",
                MobeelizerModelFieldDefinition.class, String.class);
    }

    @Test
    public void shouldReturnEmptyModels() throws Exception {
        // when
        Set<MobeelizerModel> convert = definitionManager.convert(definition, "com.mobeelizer.mobile.android", "role");

        // then
        assertTrue(convert.isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldReturnFailIsGroupNotFound() throws Exception {
        // when
        definitionManager.convert(definition, "com.mobeelizer.mobile.android", "notExistingRole");
    }

    @Test
    public void shouldReturnNullIfEmptyFieldCredentials() throws Exception {
        // given
        MobeelizerModelFieldDefinition field = mock(MobeelizerModelFieldDefinition.class);
        Set<MobeelizerModelFieldCredentialsDefinition> credentials = new HashSet<MobeelizerModelFieldCredentialsDefinition>();
        when(field.getCredentials()).thenReturn(credentials);

        // when
        MobeelizerModelFieldCredentialsDefinition actualCredentials = (MobeelizerModelFieldCredentialsDefinition) fieldHasAccess
                .invoke(definitionManager, field, "role");

        // then
        assertNull(actualCredentials);
    }

    @Test
    public void shouldReturnNullIfNoRoleFieldCredentials() throws Exception {
        checkFieldHasAccess("otherRole", MobeelizerCredential.ALL, MobeelizerCredential.ALL, MobeelizerCredential.ALL, false);
    }

    @Test
    public void shouldReturnFieldCredentials() throws Exception {
        for (MobeelizerCredential createAllowed : MobeelizerCredential.values()) {
            for (MobeelizerCredential updateAllowed : MobeelizerCredential.values()) {
                for (MobeelizerCredential readAllowed : MobeelizerCredential.values()) {
                    boolean hasAccess = createAllowed != MobeelizerCredential.NONE || updateAllowed != MobeelizerCredential.NONE
                            || readAllowed != MobeelizerCredential.NONE;
                    checkFieldHasAccess("role", createAllowed, updateAllowed, readAllowed, hasAccess);
                }
            }
        }
    }

    @Test
    public void shouldReturnNullIfEmptyCredentials() throws Exception {
        // given
        MobeelizerModelDefinition model = mock(MobeelizerModelDefinition.class);
        Set<MobeelizerModelCredentialsDefinition> credentials = new HashSet<MobeelizerModelCredentialsDefinition>();
        when(model.getCredentials()).thenReturn(credentials);

        // when
        MobeelizerModelCredentialsDefinition actualCredentials = (MobeelizerModelCredentialsDefinition) modelHasAccess.invoke(
                definitionManager, model, "role");

        // then
        assertNull(actualCredentials);
    }

    @Test
    public void shouldReturnNullIfNoRoleCredentials() throws Exception {
        checkModelHasAccess("otherRole", MobeelizerCredential.ALL, MobeelizerCredential.ALL, MobeelizerCredential.ALL,
                MobeelizerCredential.ALL, false);
    }

    @Test
    public void shouldReturnCredentials() throws Exception {
        for (MobeelizerCredential createAllowed : MobeelizerCredential.values()) {
            for (MobeelizerCredential updateAllowed : MobeelizerCredential.values()) {
                for (MobeelizerCredential readAllowed : MobeelizerCredential.values()) {
                    for (MobeelizerCredential deleteAllowed : MobeelizerCredential.values()) {
                        boolean hasAccess = createAllowed != MobeelizerCredential.NONE
                                || updateAllowed != MobeelizerCredential.NONE || readAllowed != MobeelizerCredential.NONE
                                || deleteAllowed != MobeelizerCredential.NONE;
                        checkModelHasAccess("role", createAllowed, updateAllowed, readAllowed, deleteAllowed, hasAccess);
                    }
                }
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void shouldReturnModels() throws Exception {
        // given
        MobeelizerModelDefinition model1 = mock(MobeelizerModelDefinition.class);
        MobeelizerModelCredentialsDefinition modelCredentials1 = mock(MobeelizerModelCredentialsDefinition.class);
        when(modelCredentials1.getRole()).thenReturn("role");
        when(modelCredentials1.getReadAllowed()).thenReturn(MobeelizerCredential.ALL);
        when(model1.getName()).thenReturn("model1");
        when(model1.getCredentials()).thenReturn(Collections.singleton(modelCredentials1));
        MobeelizerModelFieldDefinition field11 = mock(MobeelizerModelFieldDefinition.class);
        MobeelizerModelFieldDefinition field12 = mock(MobeelizerModelFieldDefinition.class);
        MobeelizerModelFieldCredentialsDefinition fieldCredentials12 = mock(MobeelizerModelFieldCredentialsDefinition.class);
        when(fieldCredentials12.getRole()).thenReturn("role");
        when(fieldCredentials12.getReadAllowed()).thenReturn(MobeelizerCredential.ALL);
        when(field12.getCredentials()).thenReturn(Collections.singleton(fieldCredentials12));
        MobeelizerModelFieldDefinition field13 = mock(MobeelizerModelFieldDefinition.class);
        MobeelizerModelFieldCredentialsDefinition fieldCredentials13 = mock(MobeelizerModelFieldCredentialsDefinition.class);
        when(fieldCredentials13.getRole()).thenReturn("role");
        when(fieldCredentials13.getReadAllowed()).thenReturn(MobeelizerCredential.ALL);
        when(field13.getCredentials()).thenReturn(Collections.singleton(fieldCredentials13));
        Set<MobeelizerModelFieldDefinition> fields1 = new HashSet<MobeelizerModelFieldDefinition>();
        fields1.add(field11);
        fields1.add(field12);
        fields1.add(field13);
        when(model1.getFields()).thenReturn(fields1);

        MobeelizerModelDefinition model2 = mock(MobeelizerModelDefinition.class);
        MobeelizerModelCredentialsDefinition modelCredentials2 = mock(MobeelizerModelCredentialsDefinition.class);
        when(modelCredentials2.getRole()).thenReturn("role");
        when(modelCredentials2.getReadAllowed()).thenReturn(MobeelizerCredential.ALL);
        when(model2.getName()).thenReturn("modelName2");
        when(model2.getCredentials()).thenReturn(Collections.singleton(modelCredentials2));
        MobeelizerModelFieldDefinition field21 = mock(MobeelizerModelFieldDefinition.class);
        Set<MobeelizerModelFieldDefinition> fields2 = new HashSet<MobeelizerModelFieldDefinition>();
        fields2.add(field21);
        when(model2.getFields()).thenReturn(fields2);

        MobeelizerModelDefinition model3 = mock(MobeelizerModelDefinition.class);
        when(model3.getName()).thenReturn("model3");
        MobeelizerModelFieldDefinition field31 = mock(MobeelizerModelFieldDefinition.class);
        MobeelizerModelFieldCredentialsDefinition fieldCredentials31 = mock(MobeelizerModelFieldCredentialsDefinition.class);
        when(fieldCredentials31.getRole()).thenReturn("role");
        when(fieldCredentials31.getReadAllowed()).thenReturn(MobeelizerCredential.ALL);
        when(field31.getCredentials()).thenReturn(Collections.singleton(fieldCredentials31));
        Set<MobeelizerModelFieldDefinition> fields3 = new HashSet<MobeelizerModelFieldDefinition>();
        fields3.add(field31);
        when(model3.getFields()).thenReturn(fields3);

        Set<MobeelizerModelDefinition> models = new HashSet<MobeelizerModelDefinition>();
        models.add(model1);
        models.add(model2);
        models.add(model3);
        when(definition.getModels()).thenReturn(models);

        MobeelizerModelImpl modelDefinition1 = mock(MobeelizerModelImpl.class);
        MobeelizerModelImpl modelDefinition2 = mock(MobeelizerModelImpl.class);
        MobeelizerFieldImpl fieldDefinition1 = mock(MobeelizerFieldImpl.class);
        MobeelizerFieldImpl fieldDefinition2 = mock(MobeelizerFieldImpl.class);

        Class clazz1 = String.class;
        Class clazz2 = Boolean.class;

        PowerMockito.mockStatic(Class.class);

        PowerMockito.when(Class.forName("com.mobeelizer.mobile.android.Model1")).thenReturn(clazz1);
        PowerMockito.when(Class.forName("com.mobeelizer.mobile.android.ModelName2")).thenReturn(clazz2);

        Set<MobeelizerFieldImpl> fieldDefinitions = new HashSet<MobeelizerFieldImpl>();
        fieldDefinitions.add(fieldDefinition1);
        fieldDefinitions.add(fieldDefinition2);

        whenNew(MobeelizerFieldImpl.class).withArguments(clazz1, field12, fieldCredentials12).thenReturn(fieldDefinition1);
        whenNew(MobeelizerFieldImpl.class).withArguments(clazz1, field13, fieldCredentials13).thenReturn(fieldDefinition2);

        whenNew(MobeelizerModelImpl.class).withArguments(eq(clazz1), eq("model1"), eq(modelCredentials1), eq(fieldDefinitions))
                .thenReturn(modelDefinition1);
        whenNew(MobeelizerModelImpl.class).withArguments(eq(clazz2), eq("modelName2"), eq(modelCredentials2),
                eq(Collections.<MobeelizerField> emptySet())).thenReturn(modelDefinition2);

        // when
        Set<MobeelizerModel> convert = definitionManager.convert(definition, "com.mobeelizer.mobile.android", "role");

        // then
        PowerMockito.verifyNew(MobeelizerFieldImpl.class, Mockito.times(2)).withArguments(any(Class.class),
                any(MobeelizerModelFieldDefinition.class), any(MobeelizerModelFieldCredentialsDefinition.class));
        PowerMockito.verifyNew(MobeelizerModelImpl.class, Mockito.times(2)).withArguments(any(Class.class), any(String.class),
                any(MobeelizerModelCredentialsDefinition.class), any(Set.class));
        assertEquals(2, convert.size());
        assertTrue(convert.contains(modelDefinition1));
        assertTrue(convert.contains(modelDefinition2));
    }

    private void checkModelHasAccess(final String role, final MobeelizerCredential createAllowed,
            final MobeelizerCredential updateAllowed, final MobeelizerCredential readAllowed,
            final MobeelizerCredential deleteAllowed, final boolean hasAccess) {
        MobeelizerModelDefinition model = mock(MobeelizerModelDefinition.class);
        MobeelizerModelCredentialsDefinition credentials1 = mock(MobeelizerModelCredentialsDefinition.class);
        when(credentials1.getRole()).thenReturn(role);
        when(credentials1.getCreateAllowed()).thenReturn(createAllowed);
        when(credentials1.getUpdateAllowed()).thenReturn(updateAllowed);
        when(credentials1.getReadAllowed()).thenReturn(readAllowed);
        when(credentials1.getDeleteAllowed()).thenReturn(deleteAllowed);
        Set<MobeelizerModelCredentialsDefinition> credentials = new HashSet<MobeelizerModelCredentialsDefinition>();
        credentials.add(credentials1);
        when(model.getCredentials()).thenReturn(credentials);

        // when
        MobeelizerModelCredentialsDefinition actualCredentials;
        try {
            actualCredentials = (MobeelizerModelCredentialsDefinition) modelHasAccess.invoke(definitionManager, model, "role");
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        // then
        if (hasAccess) {
            assertSame(credentials1, actualCredentials);
        } else {
            assertNull(actualCredentials);
        }
    }

    private void checkFieldHasAccess(final String role, final MobeelizerCredential createAllowed,
            final MobeelizerCredential updateAllowed, final MobeelizerCredential readAllowed, final boolean hasAccess) {
        MobeelizerModelFieldDefinition field = mock(MobeelizerModelFieldDefinition.class);
        MobeelizerModelFieldCredentialsDefinition credentials1 = mock(MobeelizerModelFieldCredentialsDefinition.class);
        when(credentials1.getRole()).thenReturn(role);
        when(credentials1.getCreateAllowed()).thenReturn(createAllowed);
        when(credentials1.getUpdateAllowed()).thenReturn(updateAllowed);
        when(credentials1.getReadAllowed()).thenReturn(readAllowed);
        Set<MobeelizerModelFieldCredentialsDefinition> credentials = new HashSet<MobeelizerModelFieldCredentialsDefinition>();
        credentials.add(credentials1);
        when(field.getCredentials()).thenReturn(credentials);

        // when
        MobeelizerModelFieldCredentialsDefinition actualCredentials;
        try {
            actualCredentials = (MobeelizerModelFieldCredentialsDefinition) fieldHasAccess.invoke(definitionManager, field,
                    "role");
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        // then
        if (hasAccess) {
            assertSame(credentials1, actualCredentials);
        } else {
            assertNull(actualCredentials);
        }
    }

}
