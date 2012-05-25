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

import com.mobeelizer.java.api.MobeelizerErrors;
import com.mobeelizer.java.definition.MobeelizerErrorsHolder;
import com.mobeelizer.java.sync.MobeelizerJsonEntity;
import com.mobeelizer.mobile.android.api.MobeelizerCriteriaBuilder;
import com.mobeelizer.mobile.android.api.MobeelizerDatabase;
import com.mobeelizer.mobile.android.model.MobeelizerAndroidModel;
import com.mobeelizer.mobile.android.search.MobeelizerCriteriaBuilderImpl;

public class MobeelizerDatabaseImpl implements MobeelizerDatabase {

    public static final String _FILE_TABLE_NAME = "_files";

    public static final String _FILE_GUID = "_guid";

    public static final String _FILE_PATH = "_path";

    public static final String _FILE_MODIFIED = "_modified";

    private SQLiteDatabase database;

    private final MobeelizerDatabaseHelper databaseHelper;

    private final Map<Class<?>, MobeelizerAndroidModel> modelsByClass;

    private final Map<String, MobeelizerAndroidModel> modelsByName;

    private final MobeelizerApplication application;

    public MobeelizerDatabaseImpl(final MobeelizerApplication application, final Set<MobeelizerAndroidModel> models) {
        this.application = application;
        this.databaseHelper = new MobeelizerDatabaseHelper(application, models);

        Map<Class<?>, MobeelizerAndroidModel> modelsByClass = new HashMap<Class<?>, MobeelizerAndroidModel>();
        Map<String, MobeelizerAndroidModel> modelsByName = new HashMap<String, MobeelizerAndroidModel>();

        for (MobeelizerAndroidModel model : models) {
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
        return save(getModel(entity.getClass()), entity);
    }

    @Override
    public MobeelizerErrors save(final Map<String, Object> entity) {
        return save(getModelFromMap(entity), entity);
    }

    private <T> MobeelizerErrors save(final MobeelizerAndroidModel model, final T entity) {
        MobeelizerErrorsHolder errors = new MobeelizerErrorsHolder();
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
    public long count(final String model) {
        return getModel(model).count(database);
    }

    @Override
    public <T> T get(final Class<T> clazz, final String guid) {
        return getModel(clazz).get(database, guid);
    }

    @Override
    public Map<String, Object> getAsMap(final String model, final String guid) {
        return getModel(model).getAsMap(database, guid);
    }

    @Override
    public <T> boolean exists(final Class<T> clazz, final String guid) {
        return getModel(clazz).exists(database, guid);
    }

    @Override
    public boolean exists(final String model, final String guid) {
        return getModel(model).exists(database, guid);
    }

    @Override
    public <T> void delete(final T entity, final T... otherEntities) {
        MobeelizerAndroidModel model = getModel(entity.getClass());
        model.delete(database, entity);
        for (T otherEntity : otherEntities) {
            model.delete(database, otherEntity);
        }
    }

    @Override
    public void deleteMap(final Map<String, Object> entity, final Map<String, Object>... otherEntities) {
        getModelFromMap(entity).delete(database, entity);
        for (Map<String, Object> otherEntity : otherEntities) {
            getModelFromMap(otherEntity).delete(database, otherEntity);
        }
    }

    @Override
    public <T> void delete(final Class<T> clazz, final String... guids) {
        MobeelizerAndroidModel model = getModel(clazz);
        for (String guid : guids) {
            model.deleteByGuid(database, guid);
        }
    }

    @Override
    public void delete(final String model, final String... guids) {
        MobeelizerAndroidModel androidModel = getModel(model);
        for (String guid : guids) {
            androidModel.deleteByGuid(database, guid);
        }
    }

    @Override
    public <T> void deleteAll(final Class<T> clazz) {
        getModel(clazz).deleteAll(database);
    }

    @Override
    public void deleteAll(final String model) {
        getModel(model).deleteAll(database);
    }

    @Override
    public <T> List<T> list(final Class<T> clazz) {
        return getModel(clazz).list(database);
    }

    @Override
    public List<Map<String, Object>> listAsMaps(final String model) {
        return getModel(model).listOfMaps(database);
    }

    @Override
    public <T> MobeelizerCriteriaBuilder<T> find(final Class<T> clazz) {
        return new MobeelizerCriteriaBuilderImpl<T>(getModel(clazz), database);
    }

    @Override
    public MobeelizerCriteriaBuilder<Map<String, Object>> find(final String model) {
        return new MobeelizerCriteriaBuilderImpl<Map<String, Object>>(getModel(model), database);
    }

    @Override
    public MobeelizerAndroidModel getModel(final String name) {
        if (!modelsByName.containsKey(name)) {
            throw new IllegalStateException("Cannot find model '" + name + "'");
        }
        return modelsByName.get(name);
    }

    private MobeelizerAndroidModel getModel(final Class<? extends Object> clazz) {
        if (!modelsByClass.containsKey(clazz)) {
            throw new IllegalStateException("Cannot find model for class " + clazz.getCanonicalName());
        }
        return modelsByClass.get(clazz);
    }

    private MobeelizerAndroidModel getModelFromMap(final Map<String, Object> entity) {
        Object modelName = entity.get("model");
        if (modelName == null) {
            throw new IllegalStateException("Field 'model' is required");
        }
        if (!(modelName instanceof String)) {
            throw new IllegalStateException("Field 'model' must be string");
        }
        return getModel((String) entity.get("model"));
    }

    void lockModifiedFlag() {
        for (MobeelizerAndroidModel model : modelsByName.values()) {
            model.lockModifiedFlag(database);
        }

        ContentValues values = new ContentValues();
        values.put(_FILE_MODIFIED, 2);
        database.update(_FILE_TABLE_NAME, values, _FILE_MODIFIED + " = 1", null);
    }

    void unlockModifiedFlag() {
        for (MobeelizerAndroidModel model : modelsByName.values()) {
            model.unlockModifiedFlag(database);
        }

        ContentValues values = new ContentValues();
        values.put(_FILE_MODIFIED, 1);
        database.update(_FILE_TABLE_NAME, values, _FILE_MODIFIED + " = 2", null);
    }

    void clearModifiedFlag() {
        for (MobeelizerAndroidModel model : modelsByName.values()) {
            model.clearModifiedFlag(database);
        }

        ContentValues values = new ContentValues();
        values.put(_FILE_MODIFIED, 0);
        database.update(_FILE_TABLE_NAME, values, _FILE_MODIFIED + " = 2", null);
    }

    MobeelizerSyncIterator getEntitiesToSync() {
        return new MobeelizerSyncIterator(database, modelsByName.values());
    }

    boolean updateEntitiesFromSync(final Iterator<MobeelizerJsonEntity> entities, final boolean clearData) {
        SQLiteDatabase localDatabase = databaseHelper.getWritableDatabase();
        localDatabase.beginTransaction();

        boolean isTransactionSuccessful = true;

        if (clearData) {
            for (MobeelizerAndroidModel model : modelsByName.values()) {
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
