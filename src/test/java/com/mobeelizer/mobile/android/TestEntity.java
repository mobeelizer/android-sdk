// 
// TestEntity.java
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
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import com.mobeelizer.java.api.MobeelizerFile;

public class TestEntity {

	private String guid;

	private String owner;

	private String group;

	private boolean conflicted;

	private boolean deleted;

	private boolean modified;

	private String string;

	private boolean booleanP;

	private Boolean booleanO;

	private Integer integerO;

	private int integerP;

	private Short shortO;

	private short shortP;

	private Long longO;

	private long longP;

	private Byte byteO;

	private byte byteP;

	private Double doubleO;

	private double doubleP;

	private Float floatO;

	private float floatP;

	private BigInteger bigInteger;

	private BigDecimal bigDecimal;

	private Date date;

	private Calendar calendar;

	private MobeelizerFile file;

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public String getGuid() {
		return guid;
	}

	public String getOwner() {
		return owner;
	}

	public boolean isConflicted() {
		return conflicted;
	}

	public boolean isModified() {
		return modified;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public String getString() {
		return string;
	}

	public void setString(final String string) {
		this.string = string;
	}

	public boolean isBooleanP() {
		return booleanP;
	}

	public void setBooleanP(final boolean booleanP) {
		this.booleanP = booleanP;
	}

	public Boolean getBooleanO() {
		return booleanO;
	}

	public void setBooleanO(final Boolean booleanO) {
		this.booleanO = booleanO;
	}

	public Integer getIntegerO() {
		return integerO;
	}

	public void setIntegerO(final Integer integerO) {
		this.integerO = integerO;
	}

	public int getIntegerP() {
		return integerP;
	}

	public void setIntegerP(final int integerP) {
		this.integerP = integerP;
	}

	public Short getShortO() {
		return shortO;
	}

	public void setShortO(final Short shortO) {
		this.shortO = shortO;
	}

	public short getShortP() {
		return shortP;
	}

	public void setShortP(final short shortP) {
		this.shortP = shortP;
	}

	public Long getLongO() {
		return longO;
	}

	public void setLongO(final Long longO) {
		this.longO = longO;
	}

	public long getLongP() {
		return longP;
	}

	public void setLongP(final long longP) {
		this.longP = longP;
	}

	public Byte getByteO() {
		return byteO;
	}

	public void setByteO(final Byte byteO) {
		this.byteO = byteO;
	}

	public byte getByteP() {
		return byteP;
	}

	public void setByteP(final byte byteP) {
		this.byteP = byteP;
	}

	public BigInteger getBigInteger() {
		return bigInteger;
	}

	public void setBigInteger(final BigInteger bigInteger) {
		this.bigInteger = bigInteger;
	}

	public Double getDoubleO() {
		return doubleO;
	}

	public void setDoubleO(final Double doubleO) {
		this.doubleO = doubleO;
	}

	public double getDoubleP() {
		return doubleP;
	}

	public void setDoubleP(final double doubleP) {
		this.doubleP = doubleP;
	}

	public Float getFloatO() {
		return floatO;
	}

	public void setFloatO(final Float floatO) {
		this.floatO = floatO;
	}

	public float getFloatP() {
		return floatP;
	}

	public void setFloatP(final float floatP) {
		this.floatP = floatP;
	}

	public BigDecimal getBigDecimal() {
		return bigDecimal;
	}

	public void setBigDecimal(final BigDecimal bigDecimal) {
		this.bigDecimal = bigDecimal;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(final Calendar calendar) {
		this.calendar = calendar;
	}

	public MobeelizerFile getFile() {
		return file;
	}

	public void setFile(final MobeelizerFile file) {
		this.file = file;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

}
