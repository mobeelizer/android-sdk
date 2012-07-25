// 
// MobeelizerDatabaseImplTest.java
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobeelizer.java.api.MobeelizerModel;
import com.mobeelizer.java.definition.MobeelizerErrorsHolder;
import com.mobeelizer.java.sync.MobeelizerJsonEntity;
import com.mobeelizer.mobile.android.api.MobeelizerCriteriaBuilder;
import com.mobeelizer.mobile.android.model.MobeelizerAndroidModel;
import com.mobeelizer.mobile.android.search.MobeelizerCriteriaBuilderImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MobeelizerDatabaseImpl.class, MobeelizerSyncIterator.class, ContentValues.class,
		MobeelizerCriteriaBuilderImpl.class, MobeelizerSyncFileIterator.class })
public class MobeelizerDatabaseImplTest {

	private MobeelizerDatabaseImpl databaseAdapter;

	private MobeelizerDatabaseHelper databaseHelper;

	private SQLiteDatabase database;

	@SuppressWarnings("rawtypes")
	private Class clazz;

	@SuppressWarnings("rawtypes")
	private Class clazzNotExists;

	private MobeelizerAndroidModel model;

	private Set<MobeelizerAndroidModel> models;

	private ContentValues contentValues;

	@Before
	@SuppressWarnings({ "unchecked" })
	public void init() throws Exception {
		MobeelizerApplication application = mock(MobeelizerApplication.class);
		when(application.getUser()).thenReturn("owner");
		when(application.getGroup()).thenReturn("group");

		contentValues = PowerMockito.mock(ContentValues.class);
		PowerMockito.whenNew(ContentValues.class).withNoArguments().thenReturn(contentValues);

		model = mock(MobeelizerAndroidModel.class);

		clazzNotExists = String.class;
		clazz = TestEntity.class;
		when(model.getMappingClass()).thenReturn(clazz);
		when(model.getName()).thenReturn("modelName");

		models = new HashSet<MobeelizerAndroidModel>();
		models.add(model);

		database = mock(SQLiteDatabase.class);

		databaseHelper = mock(MobeelizerDatabaseHelper.class);
		when(databaseHelper.getWritableDatabase()).thenReturn(database);

		whenNew(MobeelizerDatabaseHelper.class).withArguments(application, models).thenReturn(databaseHelper);

		databaseAdapter = new MobeelizerDatabaseImpl(application, models);

		databaseAdapter.open();
	}

	@Test
	public void shouldGetModel() throws Exception {
		// when
		MobeelizerModel actualModel = databaseAdapter.getModel("modelName");

		// then
		assertSame(model, actualModel);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailGettingModel() throws Exception {
		// when
		databaseAdapter.getModel("notExistingModelName");
	}

	@Test
	public void shouldOpen() throws Exception {
		// given
		databaseAdapter.close();

		// when
		databaseAdapter.open();

		// then
		verify(databaseHelper, times(2)).getWritableDatabase();
	}

	@Test
	public void shouldNotOpenTwice() throws Exception {
		// when
		databaseAdapter.open();

		// then
		verify(databaseHelper, times(1)).getWritableDatabase();
	}

	@Test
	public void shouldClose() throws Exception {
		// when
		databaseAdapter.close();

		// then
		verify(database, times(1)).close();
	}

	@Test
	public void shouldNotCloseTwice() throws Exception {
		// given
		databaseAdapter.close();

		// when
		databaseAdapter.close();

		// then
		verify(database, times(1)).close();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldGet() throws Exception {
		// given
		TestEntity exptectedEntity = mock(TestEntity.class);
		when(model.get(database, "guid")).thenReturn(exptectedEntity);

		// when
		TestEntity entity = (TestEntity) databaseAdapter.get(clazz, "guid");

		// then
		assertSame(exptectedEntity, entity);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldGetNullIfEntityNotExists() throws Exception {
		// given
		when(model.get(database, "guid")).thenReturn(null);

		// when
		TestEntity entity = (TestEntity) databaseAdapter.get(clazz, "guid");

		// then
		assertNull(entity);
	}

	@Test(expected = IllegalStateException.class)
	@SuppressWarnings("unchecked")
	public void shouldGetFailIfModelNotExists() throws Exception {
		// when
		databaseAdapter.get(clazzNotExists, "guid");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldExists() throws Exception {
		// given
		when(model.exists(database, "guid")).thenReturn(true);

		// when
		boolean exists = databaseAdapter.exists(clazz, "guid");

		// then
		assertTrue(exists);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldNotExists() throws Exception {
		// given
		when(model.exists(database, "guid")).thenReturn(false);

		// when
		boolean exists = databaseAdapter.exists(clazz, "guid");

		// then
		assertFalse(exists);
	}

	@Test(expected = IllegalStateException.class)
	@SuppressWarnings("unchecked")
	public void shouldExistsFailIfModelNotExists() throws Exception {
		// when
		databaseAdapter.exists(clazzNotExists, "guid");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldCount() throws Exception {
		// given
		when(model.count(database)).thenReturn(2L);

		// when
		long count = databaseAdapter.count(clazz);

		// then
		assertEquals(2L, count);
	}

	@Test(expected = IllegalStateException.class)
	@SuppressWarnings("unchecked")
	public void shouldCountFailIfModelNotExists() throws Exception {
		// when
		databaseAdapter.count(clazzNotExists);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldSaveFailIfModelNotExists() throws Exception {
		// given
		String entity = "entity";

		// when
		databaseAdapter.save(entity);
	}

	@Test
	public void shouldCreateWhenEntityNotExists() throws Exception {
		// given
		TestEntity entity = new TestEntity();

		when(model.exists(database, entity)).thenReturn(false);

		// when
		MobeelizerErrorsHolder errors = (MobeelizerErrorsHolder) databaseAdapter.save(entity);

		// then
		verify(model).create(database, entity, "owner", "group", errors);
	}

	@Test
	public void shouldUpdate() throws Exception {
		// given
		TestEntity entity = new TestEntity();

		when(model.exists(database, entity)).thenReturn(true);

		// when
		MobeelizerErrorsHolder errors = (MobeelizerErrorsHolder) databaseAdapter.save(entity);

		// then
		verify(model).update(database, entity, errors);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldDeleteWithEntityFailIfModelNotExists() throws Exception {
		// given
		String entity = "entity";

		// when
		databaseAdapter.delete(entity);
	}

	@Test
	public void shouldDeleteWithEntity() throws Exception {
		// given
		TestEntity entity1 = new TestEntity();
		TestEntity entity2 = new TestEntity();

		// when
		databaseAdapter.delete(entity1, entity2);

		// then
		verify(model).delete(database, entity1);
		verify(model).delete(database, entity2);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IllegalStateException.class)
	public void shouldDeleteAllFailIfModelNotExists() throws Exception {
		// when
		databaseAdapter.deleteAll(clazzNotExists);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeleteAll() throws Exception {
		// when
		databaseAdapter.deleteAll(clazz);

		// then
		verify(model).deleteAll(database);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IllegalStateException.class)
	public void shouldDeleteFailIfModelNotExists() throws Exception {
		// when
		databaseAdapter.delete(clazzNotExists, "guid1");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldDelete() throws Exception {
		// when
		databaseAdapter.delete(clazz, "guid1", "guid2");

		// then
		verify(model).deleteByGuid(database, "guid1");
		verify(model).deleteByGuid(database, "guid2");
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IllegalStateException.class)
	public void shouldListFailIfModelNotExists() throws Exception {
		// when
		databaseAdapter.list(clazzNotExists);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void shouldList() throws Exception {
		// given
		List expectedEntities = mock(List.class);
		when(model.list(database)).thenReturn(expectedEntities);

		// when
		List entities = databaseAdapter.list(clazz);

		// then
		assertSame(expectedEntities, entities);
	}

	@Test
	public void shouldLockModifiedFlag() throws Exception {
		// when
		databaseAdapter.lockModifiedFlag();

		// then
		verify(model).lockModifiedFlag(database);
	}

	@Test
	public void shouldUnlockModifiedFlag() throws Exception {
		// when
		databaseAdapter.unlockModifiedFlag();

		// then
		verify(model).unlockModifiedFlag(database);
	}

	@Test
	public void shouldClearModifiedFlag() throws Exception {
		// when
		databaseAdapter.clearModifiedFlag();

		// then
		verify(model).clearModifiedFlag(database);
	}

	@Test
	public void shouldGetEntitiesToSync() throws Exception {
		// given
		MobeelizerSyncIterator expectedIterator = mock(MobeelizerSyncIterator.class);
		PowerMockito.whenNew(MobeelizerSyncIterator.class).withArguments(eq(database), any(Collection.class))
				.thenReturn(expectedIterator);

		// when
		MobeelizerSyncIterator iterator = databaseAdapter.getEntitiesToSync();

		// then
		assertSame(expectedIterator, iterator);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldUpdateEntitiesFromSync() throws Exception {
		// given
		SQLiteDatabase localDatabase = mock(SQLiteDatabase.class);
		when(databaseHelper.getWritableDatabase()).thenReturn(localDatabase);

		MobeelizerJsonEntity entity1 = mock(MobeelizerJsonEntity.class);
		when(entity1.getModel()).thenReturn("modelName");
		MobeelizerJsonEntity entity2 = mock(MobeelizerJsonEntity.class);
		when(entity2.getModel()).thenReturn("modelName");
		Iterator<MobeelizerJsonEntity> entities = mock(Iterator.class);
		when(entities.hasNext()).thenReturn(true, true, false);
		when(entities.next()).thenReturn(entity1, entity2);

		when(model.updateEntityFromSync(eq(localDatabase), eq(entity1))).thenReturn(true);
		when(model.updateEntityFromSync(eq(localDatabase), eq(entity2))).thenReturn(true);

		// when
		boolean success = databaseAdapter.updateEntitiesFromSync(entities, true);

		// then
		verify(localDatabase).beginTransaction();
		verify(localDatabase).setTransactionSuccessful();
		verify(localDatabase).endTransaction();
		verify(model).clearData(localDatabase);
		verify(model).updateEntityFromSync(eq(localDatabase), eq(entity1));
		verify(model).updateEntityFromSync(eq(localDatabase), eq(entity2));
		assertTrue(success);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldUpdateEntitiesFromSyncWithValidationError() throws Exception {
		// given
		SQLiteDatabase localDatabase = mock(SQLiteDatabase.class);
		when(databaseHelper.getWritableDatabase()).thenReturn(localDatabase);

		MobeelizerJsonEntity entity1 = mock(MobeelizerJsonEntity.class);
		when(entity1.getModel()).thenReturn("modelName");
		MobeelizerJsonEntity entity2 = mock(MobeelizerJsonEntity.class);
		when(entity2.getModel()).thenReturn("modelName");
		Iterator<MobeelizerJsonEntity> entities = mock(Iterator.class);
		when(entities.hasNext()).thenReturn(true, true, false);
		when(entities.next()).thenReturn(entity1, entity2);

		when(model.updateEntityFromSync(eq(localDatabase), eq(entity1))).thenReturn(false);

		// when
		boolean success = databaseAdapter.updateEntitiesFromSync(entities, false);

		// then
		verify(localDatabase).beginTransaction();
		verify(localDatabase, never()).setTransactionSuccessful();
		verify(localDatabase).endTransaction();
		verify(model, never()).clearData(localDatabase);
		verify(model).updateEntityFromSync(eq(localDatabase), eq(entity1));
		verify(model, never()).updateEntityFromSync(eq(localDatabase), eq(entity2));
		assertFalse(success);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void shouldCreateCreteriaBuilder() throws Exception {
		// given
		MobeelizerCriteriaBuilderImpl expectedBuilder = mock(MobeelizerCriteriaBuilderImpl.class);
		PowerMockito.whenNew(MobeelizerCriteriaBuilderImpl.class).withArguments(model, database).thenReturn(expectedBuilder);

		// when
		MobeelizerCriteriaBuilder builder = databaseAdapter.find(clazz);

		// then
		assertNotNull(builder);
		assertEquals(expectedBuilder, builder);
	}

	@Test
	public void shouldAddFile() throws Exception {
		// when
		databaseAdapter.addFile("guid", "path");

		// then
		verify(contentValues).put("_guid", "guid");
		verify(contentValues).put("_path", "path");
		verify(contentValues).put("_modified", 1);
		verify(database).insert("_files", null, contentValues);
	}

	@Test
	public void shouldAddFileFromSync() throws Exception {
		// when
		databaseAdapter.addFileFromSync("guid", "path");

		// then
		verify(contentValues).put("_guid", "guid");
		verify(contentValues).put("_path", "path");
		verify(contentValues).put("_modified", 0);
		verify(database).insert("_files", null, contentValues);
	}

	@Test
	public void shouldDeleteFileFromSync() throws Exception {
		// when
		databaseAdapter.deleteFileFromSync("guid");

		// then
		verify(database).delete("_files", "_guid = ?", new String[] { "guid" });
	}

	@Test
	public void shouldGetFilesToSync() throws Exception {
		// given
		Cursor cursor = mock(Cursor.class);
		when(database.query("_files", null, "_modified = 2", null, null, null, null)).thenReturn(cursor);
		MobeelizerSyncFileIterator iterator = mock(MobeelizerSyncFileIterator.class);
		PowerMockito.whenNew(MobeelizerSyncFileIterator.class).withArguments(cursor).thenReturn(iterator);

		// when
		MobeelizerSyncFileIterator filesToSync = databaseAdapter.getFilesToSync();

		// then
		assertEquals(iterator, filesToSync);
	}

	@Test
	public void shouldGetFilePath() throws Exception {
		// given
		Cursor cursor = mock(Cursor.class);
		when(cursor.moveToNext()).thenReturn(true);
		when(cursor.getColumnIndex("_path")).thenReturn(12);
		when(cursor.getString(12)).thenReturn("path");
		when(database.query("_files", new String[] { "_path" }, "_guid = ?", new String[] { "guid" }, null, null, null))
				.thenReturn(cursor);

		// when
		String path = databaseAdapter.getFilePath("guid");

		// then
		assertEquals("path", path);
		verify(cursor).close();
	}

	@Test
	public void shouldGetNullFilePath() throws Exception {
		// given
		Cursor cursor = mock(Cursor.class);
		when(cursor.moveToNext()).thenReturn(false);
		when(database.query("_files", new String[] { "_path" }, "_guid = ?", new String[] { "guid" }, null, null, null))
				.thenReturn(cursor);

		// when
		String path = databaseAdapter.getFilePath("guid");

		// then
		assertNull(path);
		verify(cursor).close();
	}

	@Test
	public void shouldFileNotExists() throws Exception {
		// given
		Cursor cursor = mock(Cursor.class);
		when(cursor.moveToNext()).thenReturn(false);
		when(database.query("_files", null, "_guid = ?", new String[] { "guid" }, null, null, null)).thenReturn(cursor);

		// when
		boolean exists = databaseAdapter.isFileExists("guid");

		// then
		assertFalse(exists);
		verify(cursor).close();
	}

	@Test
	public void shouldFileExists() throws Exception {
		// given
		Cursor cursor = mock(Cursor.class);
		when(cursor.moveToNext()).thenReturn(true);
		when(database.query("_files", null, "_guid = ?", new String[] { "guid" }, null, null, null)).thenReturn(cursor);

		// when
		boolean exists = databaseAdapter.isFileExists("guid");

		// then
		assertTrue(exists);
		verify(cursor).close();
	}
}
