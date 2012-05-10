// 
// MobeelizerErrorsTest.java
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

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.mobeelizer.java.api.MobeelizerErrorCode;
import com.mobeelizer.java.definition.MobeelizerErrorsHolder;

public class MobeelizerErrorsTest {

    private MobeelizerErrorsHolder errors;

    @Before
    public void init() {
        errors = new MobeelizerErrorsHolder();
    }

    @Test
    public void shouldBeValidOnInit() throws Exception {
        // then
        assertTrue(errors.isValid());
        assertTrue(errors.getErrors().isEmpty());
        assertTrue(errors.isFieldValid("field"));
        assertTrue(errors.getFieldErrors("field").isEmpty());
    }

    @Test
    public void shouldHasFieldRequiredError() throws Exception {
        // when
        errors.addFieldCanNotBeEmpty("field");

        // then
        assertFalse(errors.isValid());
        assertTrue(errors.getErrors().isEmpty());
        assertFalse(errors.isFieldValid("field"));
        assertEquals(1, errors.getFieldErrors("field").size());

        MobeelizerErrorsHolder.MobeelizerErrorImpl error = (MobeelizerErrorsHolder.MobeelizerErrorImpl) errors
                .getFieldErrors("field").get(0);

        assertEquals(MobeelizerErrorCode.EMPTY, error.getCode());
        assertEquals("Value can't be empty.", error.getMessage());
        assertTrue(error.getArgs().isEmpty());
    }

    @Test
    public void shouldHasFieldTooLongError() throws Exception {
        // when
        errors.addFieldIsTooLong("name", 10);

        // then
        assertFalse(errors.isValid());
        assertTrue(errors.getErrors().isEmpty());
        assertFalse(errors.isFieldValid("name"));
        assertEquals(1, errors.getFieldErrors("name").size());

        MobeelizerErrorsHolder.MobeelizerErrorImpl error = (MobeelizerErrorsHolder.MobeelizerErrorImpl) errors.getFieldErrors("name")
                .get(0);

        assertEquals(MobeelizerErrorCode.TOO_LONG, error.getCode());
        assertEquals("Value is too long (maximum is 10 characters).", error.getMessage());
        assertEquals(1, error.getArgs().size());
        assertEquals(10, error.getArgs().get(0));
    }

    @Test
    public void shouldHasFieldGreaterOrEqualsThanMaxValueError() throws Exception {
        // when
        errors.addFieldMustBeLessThan("name", new BigDecimal("10.4"));

        // then
        assertFalse(errors.isValid());
        assertTrue(errors.getErrors().isEmpty());
        assertFalse(errors.isFieldValid("name"));
        assertEquals(1, errors.getFieldErrors("name").size());

        MobeelizerErrorsHolder.MobeelizerErrorImpl error = (MobeelizerErrorsHolder.MobeelizerErrorImpl) errors.getFieldErrors("name")
                .get(0);

        assertEquals(MobeelizerErrorCode.LESS_THAN, error.getCode());
        assertEquals("Value must be less than 10.4.", error.getMessage());
        assertEquals(1, error.getArgs().size());
        assertEquals(new BigDecimal("10.4"), error.getArgs().get(0));
    }

    @Test
    public void shouldHasFieldGreaterThanMaxValueError() throws Exception {
        // when
        errors.addFieldMustBeLessThanOrEqualTo("name", new BigDecimal("10.4"));

        // then
        assertFalse(errors.isValid());
        assertTrue(errors.getErrors().isEmpty());
        assertFalse(errors.isFieldValid("name"));
        assertEquals(1, errors.getFieldErrors("name").size());

        MobeelizerErrorsHolder.MobeelizerErrorImpl error = (MobeelizerErrorsHolder.MobeelizerErrorImpl) errors.getFieldErrors("name")
                .get(0);

        assertEquals(MobeelizerErrorCode.LESS_THAN_OR_EQUAL_TO, error.getCode());
        assertEquals("Value must be less than or equal to 10.4.", error.getMessage());
        assertEquals(1, error.getArgs().size());
        assertEquals(new BigDecimal("10.4"), error.getArgs().get(0));
    }

    @Test
    public void shouldHasFieldGreaterThanMaxValueError2() throws Exception {
        // when
        errors.addFieldMustBeLessThan("name2", 13L);

        // then
        assertFalse(errors.isValid());
        assertTrue(errors.getErrors().isEmpty());
        assertFalse(errors.isFieldValid("name2"));
        assertEquals(1, errors.getFieldErrors("name2").size());

        MobeelizerErrorsHolder.MobeelizerErrorImpl error = (MobeelizerErrorsHolder.MobeelizerErrorImpl) errors
                .getFieldErrors("name2").get(0);

        assertEquals(MobeelizerErrorCode.LESS_THAN, error.getCode());
        assertEquals("Value must be less than 13.", error.getMessage());
        assertEquals(1, error.getArgs().size());
        assertEquals(13L, error.getArgs().get(0));
    }

    @Test
    public void shouldHasFieldLowerThanMinValueError() throws Exception {
        // when
        errors.addFieldMustBeGreaterThan("name3", 11L);

        // then
        assertFalse(errors.isValid());
        assertTrue(errors.getErrors().isEmpty());
        assertFalse(errors.isFieldValid("name3"));
        assertEquals(1, errors.getFieldErrors("name3").size());

        MobeelizerErrorsHolder.MobeelizerErrorImpl error = (MobeelizerErrorsHolder.MobeelizerErrorImpl) errors
                .getFieldErrors("name3").get(0);

        assertEquals(MobeelizerErrorCode.GREATER_THAN, error.getCode());
        assertEquals("Value must be greater than 11.", error.getMessage());
        assertEquals(1, error.getArgs().size());
        assertEquals(11L, error.getArgs().get(0));
    }

    @Test
    public void shouldHasFieldLowerThanMinValueError2() throws Exception {
        // when
        errors.addFieldMustBeGreaterThanOrEqual("name3", new BigDecimal("2.1"));

        // then
        assertFalse(errors.isValid());
        assertTrue(errors.getErrors().isEmpty());
        assertFalse(errors.isFieldValid("name3"));
        assertEquals(1, errors.getFieldErrors("name3").size());

        MobeelizerErrorsHolder.MobeelizerErrorImpl error = (MobeelizerErrorsHolder.MobeelizerErrorImpl) errors
                .getFieldErrors("name3").get(0);

        assertEquals(MobeelizerErrorCode.GREATER_THAN_OR_EQUAL_TO, error.getCode());
        assertEquals("Value must be greater than or equal to 2.1.", error.getMessage());
        assertEquals(1, error.getArgs().size());
        assertEquals(new BigDecimal("2.1"), error.getArgs().get(0));
    }

    @Test
    public void shouldHasFieldLowerThanMinValueError3() throws Exception {
        // when
        errors.addFieldMustBeGreaterThan("name3", new BigDecimal("2.2"));

        // then
        assertFalse(errors.isValid());
        assertTrue(errors.getErrors().isEmpty());
        assertFalse(errors.isFieldValid("name3"));
        assertEquals(1, errors.getFieldErrors("name3").size());

        MobeelizerErrorsHolder.MobeelizerErrorImpl error = (MobeelizerErrorsHolder.MobeelizerErrorImpl) errors
                .getFieldErrors("name3").get(0);

        assertEquals(MobeelizerErrorCode.GREATER_THAN, error.getCode());
        assertEquals("Value must be greater than 2.2.", error.getMessage());
        assertEquals(1, error.getArgs().size());
        assertEquals(new BigDecimal("2.2"), error.getArgs().get(0));
    }

    @Test
    public void shouldHasFieldMissingReferenceError() throws Exception {
        // when
        errors.addFieldMissingReferenceError("name3", "guid");

        // then
        assertFalse(errors.isValid());
        assertTrue(errors.getErrors().isEmpty());
        assertFalse(errors.isFieldValid("name3"));
        assertEquals(1, errors.getFieldErrors("name3").size());

        MobeelizerErrorsHolder.MobeelizerErrorImpl error = (MobeelizerErrorsHolder.MobeelizerErrorImpl) errors
                .getFieldErrors("name3").get(0);

        assertEquals(MobeelizerErrorCode.NOT_FOUND, error.getCode());
        assertEquals("Relation 'guid' must exist.", error.getMessage());
        assertEquals(1, error.getArgs().size());
        assertEquals("guid", error.getArgs().get(0));
    }

    @Test
    public void shouldHaveMoreError() throws Exception {
        // when
        errors.addFieldCanNotBeEmpty("name3");
        errors.addFieldMissingReferenceError("name3", "guid");
        errors.addFieldIsTooLong("aaa", 10);

        // then
        assertFalse(errors.isValid());
        assertTrue(errors.getErrors().isEmpty());
        assertFalse(errors.isFieldValid("name3"));
        assertFalse(errors.isFieldValid("aaa"));
        assertEquals(2, errors.getFieldErrors("name3").size());
        assertEquals(1, errors.getFieldErrors("aaa").size());
    }

}
