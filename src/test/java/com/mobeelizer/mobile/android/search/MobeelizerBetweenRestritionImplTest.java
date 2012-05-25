// 
// MobeelizerBetweenRestritionImplTest.java
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mobeelizer.mobile.android.model.MobeelizerAndroidModel;

public class MobeelizerBetweenRestritionImplTest {

    @Test
    public void shouldAddToQuery() throws Exception {
        // given
        MobeelizerBetweenRestritionImpl restrition = new MobeelizerBetweenRestritionImpl("field", "o1", "o2");
        List<String> selectionArgs = new ArrayList<String>();
        MobeelizerAndroidModel model = mock(MobeelizerAndroidModel.class);

        when(model.convertToDatabaseValue("field", "o1")).thenReturn("converted1");
        when(model.convertToDatabaseValue("field", "o2")).thenReturn("converted2");

        // when
        String query = restrition.addToQuery(selectionArgs, model);

        // then
        assertEquals(2, selectionArgs.size());
        assertEquals("converted1", selectionArgs.get(0));
        assertEquals("converted2", selectionArgs.get(1));
        assertEquals("field between ? and ?", query);
    }

}
