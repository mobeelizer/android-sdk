// 
// MobeelizerErrorsImpl.java
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mobeelizer.mobile.android.api.MobeelizerError;
import com.mobeelizer.mobile.android.api.MobeelizerErrorCode;
import com.mobeelizer.mobile.android.api.MobeelizerErrors;

public class MobeelizerErrorsImpl implements MobeelizerErrors {

    public static class MobeelizerErrorImpl implements MobeelizerError {

        private final MobeelizerErrorCode code;

        private final String message;

        private final List<Object> args;

        private MobeelizerErrorImpl(final MobeelizerErrorCode code, final String message, final Object... args) {
            this.code = code;
            this.message = message;
            this.args = Arrays.asList(args);
        }

        @Override
        public MobeelizerErrorCode getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public List<Object> getArgs() {
            return args;
        }

    }

    private final Map<String, List<MobeelizerError>> errors = new HashMap<String, List<MobeelizerError>>();

    public void addFieldCanNotBeEmpty(final String field) {
        addError(field, MobeelizerErrorCode.EMPTY, MobeelizerErrorCode.EMPTY.getMessage());
    }

    public void addFieldIsTooLong(final String field, final int maxLength) {
        addError(field, MobeelizerErrorCode.TOO_LONG, String.format(MobeelizerErrorCode.TOO_LONG.getMessage(), maxLength),
                maxLength);
    }

    public void addFieldMustBeLessThan(final String field, final Long maxValue) {
        addError(field, MobeelizerErrorCode.LESS_THAN,
                String.format(MobeelizerErrorCode.LESS_THAN.getMessage(), Long.toString(maxValue)), maxValue);
    }

    public void addFieldMustBeGreaterThan(final String field, final Long minValue) {
        addError(field, MobeelizerErrorCode.GREATER_THAN,
                String.format(MobeelizerErrorCode.GREATER_THAN.getMessage(), Long.toString(minValue)), minValue);
    }

    public void addFieldMustBeLessThan(final String field, final BigDecimal maxValue) {
        addError(field, MobeelizerErrorCode.LESS_THAN,
                String.format(MobeelizerErrorCode.LESS_THAN.getMessage(), maxValue.toPlainString()), maxValue);
    }

    public void addFieldMustBeGreaterThanOrEqual(final String field, final BigDecimal minValue) {
        addError(field, MobeelizerErrorCode.GREATER_THAN_OR_EQUAL_TO,
                String.format(MobeelizerErrorCode.GREATER_THAN_OR_EQUAL_TO.getMessage(), minValue.toPlainString()), minValue);
    }

    public void addFieldMissingReferenceError(final String field, final String uuid) {
        addError(field, MobeelizerErrorCode.NOT_FOUND, String.format(MobeelizerErrorCode.NOT_FOUND.getMessage(), uuid), uuid);
    }

    public void addFieldMustBeLessThanOrEqualTo(final String field, final BigDecimal maxValue) {
        addError(field, MobeelizerErrorCode.LESS_THAN_OR_EQUAL_TO,
                String.format(MobeelizerErrorCode.LESS_THAN_OR_EQUAL_TO.getMessage(), maxValue.toPlainString()), maxValue);
    }

    public void addFieldMustBeGreaterThan(final String field, final BigDecimal minValue) {
        addError(field, MobeelizerErrorCode.GREATER_THAN,
                String.format(MobeelizerErrorCode.GREATER_THAN.getMessage(), minValue.toPlainString()), minValue);
    }

    @Override
    public boolean isFieldValid(final String field) {
        return !errors.containsKey(field);
    }

    @Override
    public List<MobeelizerError> getFieldErrors(final String field) {
        if (errors.containsKey(field)) {
            return errors.get(field);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<MobeelizerError> getErrors() {
        return getFieldErrors(null);
    }

    @Override
    public boolean isValid() {
        return errors.isEmpty();
    }

    private void addError(final String field, final MobeelizerErrorCode code, final String message, final Object... args) {
        if (!errors.containsKey(field)) {
            errors.put(field, new ArrayList<MobeelizerError>());
        }

        errors.get(field).add(new MobeelizerErrorImpl(code, message, args));
    }

}
