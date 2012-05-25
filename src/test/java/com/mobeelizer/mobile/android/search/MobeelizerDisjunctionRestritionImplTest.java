// 
// MobeelizerDisjunctionRestritionImplTest.java
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

public class MobeelizerDisjunctionRestritionImplTest {

    @Test
    public void shouldAddToQuery() throws Exception {
        // given
        MobeelizerInternalCriterion criterion1 = mock(MobeelizerInternalCriterion.class);
        MobeelizerInternalCriterion criterion2 = mock(MobeelizerInternalCriterion.class);

        MobeelizerDisjunctionRestritionImpl restrition = new MobeelizerDisjunctionRestritionImpl();
        restrition.add(criterion1);
        restrition.add(criterion2);

        MobeelizerAndroidModel model = mock(MobeelizerAndroidModel.class);

        List<String> selectionArgs = new ArrayList<String>();

        when(criterion1.addToQuery(selectionArgs, model)).thenReturn("c1");
        when(criterion2.addToQuery(selectionArgs, model)).thenReturn("c2");

        // when
        String query = restrition.addToQuery(selectionArgs, model);

        // then
        assertEquals(0, selectionArgs.size());
        assertEquals("(c1) or (c2)", query);
    }

    @Test
    public void shouldAddToQueryWithEmpty() throws Exception {
        // given
        MobeelizerDisjunctionRestritionImpl restrition = new MobeelizerDisjunctionRestritionImpl();
        List<String> selectionArgs = new ArrayList<String>();

        MobeelizerAndroidModel model = mock(MobeelizerAndroidModel.class);

        // when
        String query = restrition.addToQuery(selectionArgs, model);

        // then
        assertEquals(0, selectionArgs.size());
        assertEquals("1 = 1", query);
    }

}
