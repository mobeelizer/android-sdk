// 
// MobeelizerInputDataIterable.java
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

package com.mobeelizer.mobile.android.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.json.JSONException;

public class MobeelizerInputDataIterable implements Iterable<MobeelizerJsonEntity> {

    private final MobeelizerInputData inputData;

    public MobeelizerInputDataIterable(final MobeelizerInputData inputData) {
        this.inputData = inputData;
    }

    @Override
    public Iterator<MobeelizerJsonEntity> iterator() {
        return new InputDataIterator(inputData.getDataInputStream());
    }

    class InputDataIterator implements Iterator<MobeelizerJsonEntity> {

        private final BufferedReader reader;

        private String currentLine;

        public InputDataIterator(final InputStream inputData) {
            try {
                reader = new BufferedReader(new InputStreamReader(inputData, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public boolean hasNext() {
            return readLine() != null;
        }

        @Override
        public MobeelizerJsonEntity next() {
            try {
                return new MobeelizerJsonEntity(getCurrentLine());
            } catch (JSONException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private String readLine() {
            try {
                if (currentLine == null) {
                    currentLine = reader.readLine();
                }
                return currentLine;
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        private String getCurrentLine() {
            String line = readLine();
            if (line == null) {
                throw new IllegalStateException("Unexpected end of file");
            }
            currentLine = null;
            return line;
        }

    }

}
