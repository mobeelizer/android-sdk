// 
// MobeelizerRestrictionsTest.java
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

package com.mobeelizer.mobile.android.search;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.mobeelizer.mobile.android.TestEntity;
import com.mobeelizer.mobile.android.api.MobeelizerCriterion;
import com.mobeelizer.mobile.android.api.MobeelizerRestrictions;
import com.mobeelizer.mobile.android.api.MobeelizerRestrictions.MatchMode;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MobeelizerRestrictions.class)
public class MobeelizerRestrictionsTest {

    @Test
    public void shouldCreateOr() throws Exception {
        // given
        MobeelizerCriterion firstCriterion = mock(MobeelizerCriterion.class);
        MobeelizerCriterion secondCriterion = mock(MobeelizerCriterion.class);
        MobeelizerCriterion otherCriteria1 = mock(MobeelizerCriterion.class);
        MobeelizerCriterion otherCriteria2 = mock(MobeelizerCriterion.class);

        MobeelizerDisjunctionRestritionImpl expectedRestriction = mock(MobeelizerDisjunctionRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerDisjunctionRestritionImpl.class).withNoArguments().thenReturn(expectedRestriction);
        when(expectedRestriction.add(any(MobeelizerCriterion.class))).thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.or(firstCriterion, secondCriterion, otherCriteria1, otherCriteria2);

        // then
        assertSame(expectedRestriction, order);
        verify(expectedRestriction).add(firstCriterion);
        verify(expectedRestriction).add(secondCriterion);
        verify(expectedRestriction).add(otherCriteria1);
        verify(expectedRestriction).add(otherCriteria2);
    }

    @Test
    public void shouldCreateAnd() throws Exception {
        // given
        MobeelizerCriterion firstCriterion = mock(MobeelizerCriterion.class);
        MobeelizerCriterion secondCriterion = mock(MobeelizerCriterion.class);
        MobeelizerCriterion otherCriteria1 = mock(MobeelizerCriterion.class);
        MobeelizerCriterion otherCriteria2 = mock(MobeelizerCriterion.class);

        MobeelizerConjunctionRestritionImpl expectedRestriction = mock(MobeelizerConjunctionRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerConjunctionRestritionImpl.class).withNoArguments().thenReturn(expectedRestriction);
        when(expectedRestriction.add(any(MobeelizerCriterion.class))).thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.and(firstCriterion, secondCriterion, otherCriteria1, otherCriteria2);

        // then
        assertSame(expectedRestriction, order);
        verify(expectedRestriction).add(firstCriterion);
        verify(expectedRestriction).add(secondCriterion);
        verify(expectedRestriction).add(otherCriteria1);
        verify(expectedRestriction).add(otherCriteria2);
    }

    @Test
    public void shouldCreateNot() throws Exception {
        // given
        MobeelizerCriterion criterion = mock(MobeelizerCriterion.class);

        MobeelizerNotRestritionImpl expectedRestriction = mock(MobeelizerNotRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerNotRestritionImpl.class).withArguments(criterion).thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.not(criterion);

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateIdEq() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("_guid", "=", "guid")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.guidEq("guid");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateIdEqWithNull() throws Exception {
        // given
        MobeelizerNullRestritionImpl expectedRestriction = mock(MobeelizerNullRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerNullRestritionImpl.class).withArguments("_guid", true).thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.guidEq(null);

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateIdNe() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("_guid", "!=", "guid")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.guidNe("guid");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateIdNeWithNull() throws Exception {
        // given
        MobeelizerNullRestritionImpl expectedRestriction = mock(MobeelizerNullRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerNullRestritionImpl.class).withArguments("_guid", false).thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.guidNe(null);

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateOwnerEq() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("_owner", "=", "owner")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.ownerEq("owner");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateOwnerEqWithNull() throws Exception {
        // given
        MobeelizerNullRestritionImpl expectedRestriction = mock(MobeelizerNullRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerNullRestritionImpl.class).withArguments("_owner", true).thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.ownerEq(null);

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateOwnerNe() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("_owner", "!=", "owner")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.ownerNe("owner");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateOwnerNeWithNull() throws Exception {
        // given
        MobeelizerNullRestritionImpl expectedRestriction = mock(MobeelizerNullRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerNullRestritionImpl.class).withArguments("_owner", false).thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.ownerNe(null);

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateIsConflicted() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("_conflicted", "=", 1)
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.isConflicted();

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateIsNotConflicted() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("_conflicted", "=", 0)
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.isNotConflicted();

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateAllEq() throws Exception {
        // given
        MobeelizerConjunctionRestritionImpl expectedRestriction = mock(MobeelizerConjunctionRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerConjunctionRestritionImpl.class).withNoArguments().thenReturn(expectedRestriction);

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("f1", "v1");
        values.put("f2", null);
        values.put("f3", "v3");

        MobeelizerOperatorRestritionImpl criterion1 = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("f1", "=", "v1").thenReturn(criterion1);

        MobeelizerNullRestritionImpl criterion2 = mock(MobeelizerNullRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerNullRestritionImpl.class).withArguments("f2", true).thenReturn(criterion2);

        MobeelizerOperatorRestritionImpl criterion3 = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("f3", "=", "v3").thenReturn(criterion3);

        // when
        MobeelizerCriterion order = MobeelizerRestrictions.allEq(values);

        // then
        assertSame(expectedRestriction, order);
        verify(expectedRestriction).add(criterion1);
        verify(expectedRestriction).add(criterion2);
        verify(expectedRestriction).add(criterion3);
    }

    @Test
    public void shouldCreateLike() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("field", "like", "v%a_l*u?e")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.like("field", "v%a_l*u?e");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateLike2() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("field", "like", "*v\\%a\\_l\\*u\\?e*")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.like("field", "v%a_l*u?e", MatchMode.ANYWHERE);

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateLike3() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("field", "like", "v\\%a\\_l\\*u\\?e*")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.like("field", "v%a_l*u?e", MatchMode.END);

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateLike4() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("field", "like", "*v\\%a\\_l\\*u\\?e")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.like("field", "v%a_l*u?e", MatchMode.START);

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateLike5() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("field", "like", "v\\%a\\_l\\*u\\?e")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.like("field", "v%a_l*u?e", MatchMode.EXACT);

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateLike6() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("field", "like", "v%a_l*u?e")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.like("field", "v%a_l*u?e", null);

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateLe() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("field", "<=", "value")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.le("field", "value");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateLt() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("field", "<", "value")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.lt("field", "value");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateGe() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("field", ">=", "value")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.ge("field", "value");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateGt() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("field", ">", "value")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.gt("field", "value");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateEq() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("field", "=", "value")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.eq("field", "value");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateEqWithNull() throws Exception {
        // given
        MobeelizerNullRestritionImpl expectedRestriction = mock(MobeelizerNullRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerNullRestritionImpl.class).withArguments("field", true).thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.eq("field", null);

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateNe() throws Exception {
        // given
        MobeelizerOperatorRestritionImpl expectedRestriction = mock(MobeelizerOperatorRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerOperatorRestritionImpl.class).withArguments("field", "!=", "value")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.ne("field", "value");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateNeWithNull() throws Exception {
        // given
        MobeelizerNullRestritionImpl expectedRestriction = mock(MobeelizerNullRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerNullRestritionImpl.class).withArguments("field", false).thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.ne("field", null);

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateLeField() throws Exception {
        // given
        MobeelizerFieldRestritionImpl expectedRestriction = mock(MobeelizerFieldRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerFieldRestritionImpl.class).withArguments("field", "<=", "field2")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.leField("field", "field2");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateLtField() throws Exception {
        // given
        MobeelizerFieldRestritionImpl expectedRestriction = mock(MobeelizerFieldRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerFieldRestritionImpl.class).withArguments("field", "<", "field2")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.ltField("field", "field2");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateGeField() throws Exception {
        // given
        MobeelizerFieldRestritionImpl expectedRestriction = mock(MobeelizerFieldRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerFieldRestritionImpl.class).withArguments("field", ">=", "field2")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.geField("field", "field2");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateGtField() throws Exception {
        // given
        MobeelizerFieldRestritionImpl expectedRestriction = mock(MobeelizerFieldRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerFieldRestritionImpl.class).withArguments("field", ">", "field2")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.gtField("field", "field2");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateEqField() throws Exception {
        // given
        MobeelizerFieldRestritionImpl expectedRestriction = mock(MobeelizerFieldRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerFieldRestritionImpl.class).withArguments("field", "=", "field2")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.eqField("field", "field2");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateNeField() throws Exception {
        // given
        MobeelizerFieldRestritionImpl expectedRestriction = mock(MobeelizerFieldRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerFieldRestritionImpl.class).withArguments("field", "!=", "field2")
                .thenReturn(expectedRestriction);
        // when
        MobeelizerCriterion order = MobeelizerRestrictions.neField("field", "field2");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateIsNull() throws Exception {
        // given
        MobeelizerNullRestritionImpl expectedRestriction = mock(MobeelizerNullRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerNullRestritionImpl.class).withArguments("field", true).thenReturn(expectedRestriction);

        // when
        MobeelizerCriterion order = MobeelizerRestrictions.isNull("field");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateIsNotNull() throws Exception {
        // given
        MobeelizerNullRestritionImpl expectedRestriction = mock(MobeelizerNullRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerNullRestritionImpl.class).withArguments("field", false).thenReturn(expectedRestriction);

        // when
        MobeelizerCriterion order = MobeelizerRestrictions.isNotNull("field");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateBetween() throws Exception {
        // given
        MobeelizerBetweenRestritionImpl expectedRestriction = mock(MobeelizerBetweenRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerBetweenRestritionImpl.class).withArguments("field", "v1", "v2")
                .thenReturn(expectedRestriction);

        // when
        MobeelizerCriterion order = MobeelizerRestrictions.between("field", "v1", "v2");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateIn() throws Exception {
        // given
        List<Object> values = new ArrayList<Object>();
        values.add("v1");
        values.add("v2");
        values.add("v3");

        MobeelizerInRestritionImpl expectedRestriction = mock(MobeelizerInRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerInRestritionImpl.class).withArguments(eq("field"), eq(values))
                .thenReturn(expectedRestriction);

        // when
        MobeelizerCriterion order = MobeelizerRestrictions.in("field", "v1", "v2", "v3");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateIn2() throws Exception {
        // given
        List<Object> values = new ArrayList<Object>();
        values.add("v1");
        values.add("v2");
        values.add("v3");

        MobeelizerInRestritionImpl expectedRestriction = mock(MobeelizerInRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerInRestritionImpl.class).withArguments(eq("field"), eq(values))
                .thenReturn(expectedRestriction);

        // when
        MobeelizerCriterion order = MobeelizerRestrictions.in("field", values);

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateBelongsTo() throws Exception {
        // given
        Class<?> clazz = String.class;
        MobeelizerBelongsToRestritionImpl expectedRestriction = mock(MobeelizerBelongsToRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerBelongsToRestritionImpl.class).withArguments("field", clazz, "guid")
                .thenReturn(expectedRestriction);

        // when
        MobeelizerCriterion order = MobeelizerRestrictions.belongsTo("field", clazz, "guid");

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateBelongsTo2() throws Exception {
        // given
        TestEntity entity = new TestEntity();
        entity.setGuid("guid");

        MobeelizerBelongsToRestritionImpl expectedRestriction = mock(MobeelizerBelongsToRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerBelongsToRestritionImpl.class).withArguments("field", TestEntity.class, "guid")
                .thenReturn(expectedRestriction);

        // when
        MobeelizerCriterion order = MobeelizerRestrictions.belongsTo("field", entity);

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateDisjunction() throws Exception {
        // given
        MobeelizerDisjunctionRestritionImpl expectedRestriction = mock(MobeelizerDisjunctionRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerDisjunctionRestritionImpl.class).withNoArguments().thenReturn(expectedRestriction);

        // when
        MobeelizerCriterion order = MobeelizerRestrictions.disjunction();

        // then
        assertSame(expectedRestriction, order);
    }

    @Test
    public void shouldCreateConjunction() throws Exception {
        // given
        MobeelizerConjunctionRestritionImpl expectedRestriction = mock(MobeelizerConjunctionRestritionImpl.class);
        PowerMockito.whenNew(MobeelizerConjunctionRestritionImpl.class).withNoArguments().thenReturn(expectedRestriction);

        // when
        MobeelizerCriterion order = MobeelizerRestrictions.conjunction();

        // then
        assertSame(expectedRestriction, order);
    }
}
