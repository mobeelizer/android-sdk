// 
// MobeelizerNullRestritionImplTest.java
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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class MobeelizerNullRestritionImplTest {

    @Test
    public void shouldAddToQueryNotNull() throws Exception {
        // given
        MobeelizerNullRestritionImpl restrition = new MobeelizerNullRestritionImpl("field", true);
        List<String> selectionArgs = new ArrayList<String>();

        // when
        String query = restrition.addToQuery(selectionArgs);

        // then
        assertEquals(0, selectionArgs.size());
        assertEquals("field is null", query);
    }

    @Test
    public void shouldAddToQueryIsNotNull() throws Exception {
        // given
        MobeelizerNullRestritionImpl restrition = new MobeelizerNullRestritionImpl("field", false);
        List<String> selectionArgs = new ArrayList<String>();

        // when
        String query = restrition.addToQuery(selectionArgs);

        // then
        assertEquals(0, selectionArgs.size());
        assertEquals("field is not null", query);
    }

}
