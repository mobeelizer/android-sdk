// 
// MobeelizerErrors.java
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

import java.util.List;

/**
 * Holder for validation errors.
 * 
 * @since 1.0
 */
public interface MobeelizerErrors {

    /**
     * Check if entity is valid - doesn't contain any global or field's errors.
     * 
     * @return true if valid
     * @since 1.0
     */
    boolean isValid();

    /**
     * Return the list of global errors.
     * 
     * @return errors
     * @since 1.0
     */
    List<MobeelizerError> getErrors();

    /**
     * Check if field.
     * 
     * @param field
     *            field
     * @return true if valid
     * @since 1.0
     */
    boolean isFieldValid(final String field);

    /**
     * Return the list of errors for given field.
     * 
     * @param field
     *            field
     * @return errors
     * @since 1.0
     */
    List<MobeelizerError> getFieldErrors(final String field);

}
