// 
// MobeelizerErrorCode.java
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

package com.mobeelizer.mobile.android.api;

/**
 * Code of the validation error.
 * 
 * @since 1.0
 */
public enum MobeelizerErrorCode {

    /**
     * Value of the field can't be empty.
     * 
     * @since 1.0
     */
    EMPTY("Value can't be empty."),

    /**
     * Value of the field is too long. Max long is given as first error argument.
     * 
     * @since 1.0
     */
    TOO_LONG("Value is too long (maximum is %d characters)."),

    /**
     * Value of the field is too low. Minimum value, exclusively, is given as first error argument.
     * 
     * @since 1.0
     */
    GREATER_THAN("Value must be greater than %s."),

    /**
     * Value of the field is too low. Minimum value, inclusively, is given as first error argument.
     * 
     * @since 1.0
     */
    GREATER_THAN_OR_EQUAL_TO("Value must be greater than or equal to %s."),

    /**
     * Value of the field is too high. Maximum value, exclusively, is given as first error argument.
     * 
     * @since 1.0
     */
    LESS_THAN("Value must be less than %s."),

    /**
     * Value of the field is too high. Maximum value, inclusively, is given as first error argument.
     * 
     * @since 1.0
     */
    LESS_THAN_OR_EQUAL_TO("Value must be less than or equal to %s."),

    /**
     * Value of the field points to not existing entity. Guid of that entity is given as first error argument.
     * 
     * @since 1.0
     */
    NOT_FOUND("Relation '%s' must exist.");

    private final String message;

    private MobeelizerErrorCode(final String message) {
        this.message = message;
    }

    /**
     * Return the message for the error with arguments placeholders.
     * 
     * @return message
     * @see String#format(String, Object...)
     * @since 1.0
     */
    public String getMessage() {
        return message;
    }
}