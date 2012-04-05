// 
// MobeelizerDatabaseImpl.java
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobeelizer.mobile.android.api.MobeelizerCriteriaBuilder;
import com.mobeelizer.mobile.android.api.MobeelizerDatabase;
import com.mobeelizer.mobile.android.api.MobeelizerErrors;
import com.mobeelizer.mobile.android.model.MobeelizerModelDefinitionImpl;
import com.mobeelizer.mobile.android.search.MobeelizerCriteriaBuilderImpl;
import com.mobeelizer.mobile.android.sync.MobeelizerJsonEntity;

public class MobeelizerDatabaseImpl implements MobeelizerDatabase {

    public static final String _FILE_TABLE_NAME = "_files";

    public static final String _FILE_GUID = "_guid";

    public static final String _FILE_PATH = "_path";

    public static final String _FILE_MODIFIED = "_modified";

    private SQLiteDatabase database;

    private final MobeelizerDatabaseHelper databaseHelper;

    private final Map<Class<?>, MobeelizerModelDefinitionImpl> modelsByClass;

    private final Map<String, MobeelizerModelDefinitionImpl> modelsByName;

    private final MobeelizerApplication application;

    public MobeelizerDatabaseImpl(final MobeelizerApplication application, final Set<MobeelizerModelDefinitionImpl> models) {
        this.application = application;
        this.databaseHelper = new MobeelizerDatabaseHelper(application, models);

        Map<Class<?>, MobeelizerModelDefinitionImpl> modelsByClass = new HashMap<Class<?>, MobeelizerModelDefinitionImpl>();
        Map<String, MobeelizerModelDefinitionImpl> modelsByName = new HashMap<String, MobeelizerModelDefinitionImpl>();

        for (MobeelizerModelDefinitionImpl model : models) {
            modelsByClass.put(model.getMappingClass(), model);
            modelsByName.put(model.getName(), model);
        }

        this.modelsByClass = modelsByClass;
        this.modelsByName = modelsByName;
    }

    void open() {
        if (database == null) {
            database = databaseHelper.getWritableDatabase();
        }
    }

    void close() {
        if (database != null) {
            database.close();
            database = null;
        }
    }

    @Override
    public <T> MobeelizerErrors save(final T entity) {
        MobeelizerModelDefinitionImpl model = getModel(entity.getClass());
        MobeelizerErrorsImpl errors = new MobeelizerErrorsImpl();

        if (model.exists(database, entity)) {
            model.update(database, entity, errors);
        } else {
            model.create(database, entity, application.getUser(), errors);
        }

        return errors;
    }

    @Override
    public <T> long count(final Class<T> clazz) {
        return getModel(clazz).count(database);
    }

    @Override
    public <T> T get(final Class<T> clazz, final String guid) {
        return getModel(clazz).get(database, guid);
    }

    @Override
    public <T> boolean exists(final Class<T> clazz, final String guid) {
        return getModel(clazz).exists(database, guid);
    }

    @Override
    public <T> void delete(final T entity, final T... otherEntities) {
        MobeelizerModelDefinitionImpl model = getModel(entity.getClass());

        model.delete(database, entity);
        for (T otherEntity : otherEntities) {
            model.delete(database, otherEntity);
        }
    }

    @Override
    public <T> void delete(final Class<T> clazz, final String... guids) {
        MobeelizerModelDefinitionImpl model = getModel(clazz);

        for (String guid : guids) {
            model.deleteByGuid(database, guid);
        }
    }

    @Override
    public <T> void deleteAll(final Class<T> clazz) {
        getModel(clazz).deleteAll(database);
    }

    @Override
    public <T> List<T> list(final Class<T> clazz) {
        return getModel(clazz).list(database);
    }

    @Override
    public <T> MobeelizerCriteriaBuilder<T> find(final Class<T> clazz) {
        return new MobeelizerCriteriaBuilderImpl<T>(getModel(clazz), database);
    }

    @Override
    public MobeelizerModelDefinitionImpl getModel(final String name) {
        if (!modelsByName.containsKey(name)) {
            throw new IllegalStateException("Cannot find model '" + name + "'");
        }
        return modelsByName.get(name);
    }

    private MobeelizerModelDefinitionImpl getModel(final Class<? extends Object> clazz) {
        if (!modelsByClass.containsKey(clazz)) {
            throw new IllegalStateException("Cannot find model for class " + clazz.getCanonicalName());
        }
        return modelsByClass.get(clazz);
    }

    void lockModifiedFlag() {
        for (MobeelizerModelDefinitionImpl model : modelsByClass.values()) {
            model.lockModifiedFlag(database);
        }

        ContentValues values = new ContentValues();
        values.put(_FILE_MODIFIED, 2);
        database.update(_FILE_TABLE_NAME, values, _FILE_MODIFIED + " = 1", null);
    }

    void unlockModifiedFlag() {
        for (MobeelizerModelDefinitionImpl model : modelsByClass.values()) {
            model.unlockModifiedFlag(database);
        }

        ContentValues values = new ContentValues();
        values.put(_FILE_MODIFIED, 1);
        database.update(_FILE_TABLE_NAME, values, _FILE_MODIFIED + " = 2", null);
    }

    void clearModifiedFlag() {
        for (MobeelizerModelDefinitionImpl model : modelsByClass.values()) {
            model.clearModifiedFlag(database);
        }

        ContentValues values = new ContentValues();
        values.put(_FILE_MODIFIED, 0);
        database.update(_FILE_TABLE_NAME, values, _FILE_MODIFIED + " = 2", null);
    }

    MobeelizerSyncIterator getEntitiesToSync() {
        return new MobeelizerSyncIterator(database, modelsByClass.values());
    }

    boolean updateEntitiesFromSync(final Iterator<MobeelizerJsonEntity> entities, final boolean clearData) {
        SQLiteDatabase localDatabase = databaseHelper.getWritableDatabase();
        localDatabase.beginTransaction();

        boolean isTransactionSuccessful = true;

        if (clearData) {
            for (MobeelizerModelDefinitionImpl model : modelsByClass.values()) {
                model.clearData(localDatabase);
            }
        }

        while (entities.hasNext()) {
            MobeelizerJsonEntity entity = entities.next();
            isTransactionSuccessful = getModel(entity.getModel()).updateEntityFromSync(localDatabase, entity);
            if (!isTransactionSuccessful) {
                break;
            }
        }

        if (isTransactionSuccessful) {
            localDatabase.setTransactionSuccessful();
        }

        localDatabase.endTransaction();

        return isTransactionSuccessful;
    }

    void addFile(final String guid, final String path) {
        ContentValues values = new ContentValues();
        values.put(_FILE_GUID, guid);
        values.put(_FILE_PATH, path);
        values.put(_FILE_MODIFIED, 1);

        database.insert(_FILE_TABLE_NAME, null, values);
    }

    void addFileFromSync(final String guid, final String path) {
        ContentValues values = new ContentValues();
        values.put(_FILE_GUID, guid);
        values.put(_FILE_PATH, path);
        values.put(_FILE_MODIFIED, 0);

        database.insert(_FILE_TABLE_NAME, null, values);
    }

    void deleteFileFromSync(final String guid) {
        database.delete(_FILE_TABLE_NAME, _FILE_GUID + " = ?", new String[] { guid });
    }

    MobeelizerSyncFileIterator getFilesToSync() {
        Cursor cursor = database.query(_FILE_TABLE_NAME, null, _FILE_MODIFIED + " = 2", null, null, null, null);
        return new MobeelizerSyncFileIterator(cursor);
    }

    String getFilePath(final String guid) {
        Cursor cursor = database.query(_FILE_TABLE_NAME, new String[] { _FILE_PATH }, _FILE_GUID + " = ?", new String[] { guid },
                null, null, null);

        String path = null;

        if (cursor.moveToNext()) {
            path = cursor.getString(cursor.getColumnIndex(_FILE_PATH));
        }

        cursor.close();

        return path;
    }

    boolean isFileExists(final String guid) {
        Cursor cursor = database.query(_FILE_TABLE_NAME, null, _FILE_GUID + " = ?", new String[] { guid }, null, null, null);

        boolean exists = cursor.moveToNext();

        cursor.close();

        return exists;
    }

}
