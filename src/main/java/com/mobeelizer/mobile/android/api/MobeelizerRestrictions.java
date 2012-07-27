// 
// MobeelizerRestrictions.java
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

import static com.mobeelizer.java.model.MobeelizerReflectionUtil.getField;
import static com.mobeelizer.java.model.MobeelizerReflectionUtil.getValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.mobeelizer.java.model.ReflectionMobeelizerFieldAccessor;
import com.mobeelizer.mobile.android.model.MobeelizerAndroidModel;
import com.mobeelizer.mobile.android.search.MobeelizerBelongsToRestritionImpl;
import com.mobeelizer.mobile.android.search.MobeelizerBetweenRestritionImpl;
import com.mobeelizer.mobile.android.search.MobeelizerConjunctionRestritionImpl;
import com.mobeelizer.mobile.android.search.MobeelizerDisjunctionRestritionImpl;
import com.mobeelizer.mobile.android.search.MobeelizerFieldRestritionImpl;
import com.mobeelizer.mobile.android.search.MobeelizerInRestritionImpl;
import com.mobeelizer.mobile.android.search.MobeelizerNotRestritionImpl;
import com.mobeelizer.mobile.android.search.MobeelizerNullRestritionImpl;
import com.mobeelizer.mobile.android.search.MobeelizerOperatorRestritionImpl;

/**
 * Utility with factory methods for {@link MobeelizerCriterion}.
 * 
 * @since 1.0
 */
public final class MobeelizerRestrictions {

	private MobeelizerRestrictions() {
	}

	/**
	 * Match mode for "like" criterion.
	 * 
	 * @since 1.0
	 */
	public static enum MatchMode {

		/**
		 * Match anywhere.
		 * 
		 * @since 1.0
		 */
		ANYWHERE("%", "%"),

		/**
		 * Match at the end.
		 * 
		 * @since 1.0
		 */
		END("%", ""),

		/**
		 * Match exact value.
		 * 
		 * @since 1.0
		 */
		EXACT("", ""),

		/**
		 * Match at the beginning.
		 * 
		 * @since 1.0
		 */
		START("", "%");

		private final String prefix;

		private final String sufix;

		private MatchMode(final String prefix, final String sufix) {
			this.prefix = prefix;
			this.sufix = sufix;
		}

		private String prepareValue(final String value) {
			if (value == null) {
				return null;
			}
			return prefix + value.replaceAll("([\\?_%*])", "\\\\$1") + sufix;
		}

	}

	/**
	 * Create restriction that joins the given restrictions with "OR" operator.
	 * 
	 * @param firstCriterion
	 *            first criterion
	 * @param secondCriterion
	 *            second criterion
	 * @param otherCriteria
	 *            other criteria
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion or(final MobeelizerCriterion firstCriterion, final MobeelizerCriterion secondCriterion,
			final MobeelizerCriterion... otherCriteria) {
		MobeelizerDisjunction disjunction = disjunction().add(firstCriterion).add(secondCriterion);

		for (MobeelizerCriterion criterion : otherCriteria) {
			disjunction.add(criterion);
		}

		return disjunction;
	}

	/**
	 * Create restriction that joins the given restrictions with "AND" operator.
	 * 
	 * @param firstCriterion
	 *            first criterion
	 * @param secondCriterion
	 *            second criterion
	 * @param otherCriteria
	 *            other criteria
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion and(final MobeelizerCriterion firstCriterion, final MobeelizerCriterion secondCriterion,
			final MobeelizerCriterion... otherCriteria) {
		MobeelizerConjunction conjunction = conjunction().add(firstCriterion).add(secondCriterion);

		for (MobeelizerCriterion criterion : otherCriteria) {
			conjunction.add(criterion);
		}

		return conjunction;
	}

	/**
	 * Wrap given criterion with "not" operator.
	 * 
	 * @param criterion
	 *            criterion
	 * @return negated criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion not(final MobeelizerCriterion criterion) {
		return new MobeelizerNotRestritionImpl(criterion);
	}

	/**
	 * Create criterion that checks if guid is equal to the given value.
	 * 
	 * @param guid
	 *            guid
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion guidEq(final String guid) {
		if (guid == null) {
			return isNull(MobeelizerAndroidModel._GUID);
		}
		return new MobeelizerOperatorRestritionImpl(MobeelizerAndroidModel._GUID, "=", guid);
	}

	/**
	 * Create criterion that checks if owner is equal to the given value.
	 * 
	 * @param owner
	 *            owner
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion ownerEq(final String owner) {
		if (owner == null) {
			return isNull(MobeelizerAndroidModel._OWNER);
		}
		return new MobeelizerOperatorRestritionImpl(MobeelizerAndroidModel._OWNER, "=", owner);
	}

	/**
	 * Create criterion that checks if group is equal to the given value.
	 * 
	 * @param group
	 *            group
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion groupEq(final String group) {
		if (group == null) {
			return isNull(MobeelizerAndroidModel._GROUP);
		}
		return new MobeelizerOperatorRestritionImpl(MobeelizerAndroidModel._GROUP, "=", group);
	}

	/**
	 * Create criterion that checks if guid isn't equal to the given value.
	 * 
	 * @param guid
	 *            guid
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion guidNe(final String guid) {
		if (guid == null) {
			return isNotNull(MobeelizerAndroidModel._GUID);
		}
		return new MobeelizerOperatorRestritionImpl(MobeelizerAndroidModel._GUID, "!=", guid);
	}

	/**
	 * Create criterion that checks if owner isn't equal to the given value.
	 * 
	 * @param owner
	 *            owner
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion ownerNe(final String owner) {
		if (owner == null) {
			return isNotNull(MobeelizerAndroidModel._OWNER);
		}
		return new MobeelizerOperatorRestritionImpl(MobeelizerAndroidModel._OWNER, "!=", owner);
	}

	/**
	 * Create criterion that checks if owner isn't equal to the given value.
	 * 
	 * @param group
	 *            group
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion groupNe(final String group) {
		if (group == null) {
			return isNotNull(MobeelizerAndroidModel._GROUP);
		}
		return new MobeelizerOperatorRestritionImpl(MobeelizerAndroidModel._GROUP, "!=", group);
	}

	/**
	 * Create criterion that checks if entity is conflicted.
	 * 
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion isConflicted() {
		return new MobeelizerOperatorRestritionImpl(MobeelizerAndroidModel._CONFLICTED, "=", 1);
	}

	/**
	 * Create criterion that checks if entity is not conflicted.
	 * 
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion isNotConflicted() {
		return new MobeelizerOperatorRestritionImpl(MobeelizerAndroidModel._CONFLICTED, "=", 0);
	}

	/**
	 * Create criterion that checks if all given fields match given values.
	 * 
	 * @param values
	 *            map - the key is the field's name and the value is the
	 *            expected value
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion allEq(final Map<String, Object> values) {
		MobeelizerConjunction conjunction = conjunction();

		for (Map.Entry<String, Object> value : values.entrySet()) {
			if (value.getValue() == null) {
				conjunction.add(isNull(value.getKey()));
			} else {
				conjunction.add(eq(value.getKey(), value.getValue()));
			}
		}

		return conjunction;
	}

	/**
	 * Create criterion that checks if field is equal (using "like" operator) to
	 * the given value.
	 * 
	 * @param field
	 *            field
	 * @param value
	 *            value
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion like(final String field, final String value) {
		return like(field, value, null);
	}

	/**
	 * Create criterion that checks if field is equal (using "like" operator) to
	 * the given value.
	 * 
	 * @param field
	 *            field
	 * @param mode
	 *            match mode
	 * @param value
	 *            value
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion like(final String field, final String value, final MatchMode mode) {
		return new MobeelizerOperatorRestritionImpl(field, "like", mode == null ? value : mode.prepareValue(value));
	}

	/**
	 * Create criterion that checks if field is less than or equal to the given
	 * value.
	 * 
	 * @param field
	 *            field
	 * @param value
	 *            value
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion le(final String field, final Object value) {
		return new MobeelizerOperatorRestritionImpl(field, "<=", value);
	}

	/**
	 * Create criterion that checks if field is less than given value.
	 * 
	 * @param field
	 *            field
	 * @param value
	 *            value
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion lt(final String field, final Object value) {
		return new MobeelizerOperatorRestritionImpl(field, "<", value);
	}

	/**
	 * Create criterion that checks if field is greater than or equal to the
	 * given value.
	 * 
	 * @param field
	 *            field
	 * @param value
	 *            value
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion ge(final String field, final Object value) {
		return new MobeelizerOperatorRestritionImpl(field, ">=", value);
	}

	/**
	 * Create criterion that checks if field is greater than given value.
	 * 
	 * @param field
	 *            field
	 * @param value
	 *            value
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion gt(final String field, final Object value) {
		return new MobeelizerOperatorRestritionImpl(field, ">", value);
	}

	/**
	 * Create criterion that checks if field isn't equal to the given value.
	 * 
	 * @param field
	 *            field
	 * @param value
	 *            value
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion ne(final String field, final Object value) {
		if (value == null) {
			return isNotNull(field);
		}
		return new MobeelizerOperatorRestritionImpl(field, "!=", value);
	}

	/**
	 * Create criterion that checks if field is equal to the given value.
	 * 
	 * @param field
	 *            field
	 * @param value
	 *            value
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion eq(final String field, final Object value) {
		if (value == null) {
			return isNull(field);
		}
		return new MobeelizerOperatorRestritionImpl(field, "=", value);
	}

	/**
	 * Create criterion that checks if field is less than or equal to other
	 * field.
	 * 
	 * @param field
	 *            field
	 * @param otherField
	 *            other field
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion leField(final String field, final String otherField) {
		return new MobeelizerFieldRestritionImpl(field, "<=", otherField);
	}

	/**
	 * Create criterion that checks if field is less than other field.
	 * 
	 * @param field
	 *            field
	 * @param otherField
	 *            other field
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion ltField(final String field, final String otherField) {
		return new MobeelizerFieldRestritionImpl(field, "<", otherField);
	}

	/**
	 * Create criterion that checks if field is greater than or equal to other
	 * field.
	 * 
	 * @param field
	 *            field
	 * @param otherField
	 *            other field
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion geField(final String field, final String otherField) {
		return new MobeelizerFieldRestritionImpl(field, ">=", otherField);
	}

	/**
	 * Create criterion that checks if field is greater than other field.
	 * 
	 * @param field
	 *            field
	 * @param otherField
	 *            other field
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion gtField(final String field, final String otherField) {
		return new MobeelizerFieldRestritionImpl(field, ">", otherField);
	}

	/**
	 * Create criterion that checks if field isn't equal to other field.
	 * 
	 * @param field
	 *            field
	 * @param otherField
	 *            other field
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion neField(final String field, final String otherField) {
		return new MobeelizerFieldRestritionImpl(field, "!=", otherField);
	}

	/**
	 * Create criterion that checks if field is equal to other field.
	 * 
	 * @param field
	 *            field
	 * @param otherField
	 *            other field
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion eqField(final String field, final String otherField) {
		return new MobeelizerFieldRestritionImpl(field, "=", otherField);
	}

	/**
	 * Create criterion that checks if field is not null.
	 * 
	 * @param field
	 *            field
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion isNotNull(final String field) {
		return new MobeelizerNullRestritionImpl(field, false);
	}

	/**
	 * Create criterion that checks if field is null.
	 * 
	 * @param field
	 *            field
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion isNull(final String field) {
		return new MobeelizerNullRestritionImpl(field, true);
	}

	/**
	 * Create criterion that checks if field is between the given values.
	 * 
	 * @param field
	 *            field
	 * @param lo
	 *            low value
	 * @param hi
	 *            high value
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion between(final String field, final Object lo, final Object hi) {
		return new MobeelizerBetweenRestritionImpl(field, lo, hi);
	}

	/**
	 * Create criterion that checks if field is in the given values.
	 * 
	 * @param field
	 *            field
	 * @param values
	 *            values
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion in(final String field, final Collection<Object> values) {
		return new MobeelizerInRestritionImpl(field, values);
	}

	/**
	 * Create criterion that checks if field is in the given values.
	 * 
	 * @param field
	 *            field
	 * @param values
	 *            values
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion in(final String field, final Object... values) {
		return new MobeelizerInRestritionImpl(field, Arrays.asList(values));
	}

	/**
	 * Create criterion that checks if field is equal to the entity for the
	 * given class and guid.
	 * 
	 * @param field
	 *            field
	 * @param clazz
	 *            class
	 * @param guid
	 *            guid
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion belongsTo(final String field, final Class<?> clazz, final String guid) {
		return new MobeelizerBelongsToRestritionImpl(field, clazz, guid);
	}

	public static MobeelizerCriterion belongsTo(final String field, final String model, final String guid) {
		return new MobeelizerBelongsToRestritionImpl(field, model, guid);
	}

	/**
	 * Create criterion that checks if field is equal to the given entity.
	 * 
	 * @param field
	 *            field
	 * @param entity
	 *            entity
	 * @return criterion
	 * @since 1.0
	 */
	public static MobeelizerCriterion belongsTo(final String field, final Object entity) {
		return new MobeelizerBelongsToRestritionImpl(field, entity.getClass(), (String) getValue(
				new ReflectionMobeelizerFieldAccessor(getField(entity.getClass(), "guid", String.class)), entity));
	}

	/**
	 * Create the disjunction - (... OR ... OR ...).
	 * 
	 * @return disjunction
	 * @since 1.0
	 */
	public static MobeelizerDisjunction disjunction() {
		return new MobeelizerDisjunctionRestritionImpl();
	}

	/**
	 * Create the conjunction - (... AND ... AND ...).
	 * 
	 * @return conjunction
	 * @since 1.0
	 */
	public static MobeelizerConjunction conjunction() {
		return new MobeelizerConjunctionRestritionImpl();
	}

	// /**
	// * Create criterion that checks if field is equal (using case-insensitive
	// "like" operator) to the given value.
	// *
	// * @param field
	// * field
	// * @param value
	// * value
	// * @return criterion
	// */
	// public static MobeelizerCriterion ilike(final String field, final String
	// value) {
	// return like(field, value, null, false);
	// }
	//
	// /**
	// * Create criterion that checks if field is equal (using case-insensitive
	// "like" operator) to the given value.
	// *
	// * @param field
	// * field
	// * @param mode
	// * match mode
	// * @param value
	// * value
	// * @return criterion
	// */
	// public static MobeelizerCriterion ilike(final String field, final String
	// value, final MatchMode mode) {
	// return like(field, value, mode, false);
	// }

	// /**
	// * Create criterion that checks if "collection" field's size is equal to
	// given size.
	// *
	// * @param field
	// * field
	// * @param size
	// * size
	// * @return criterion
	// */
	// public static MobeelizerCriterion sizeEq(final String field, final int
	// size) {
	// return null;
	// }
	//
	// /**
	// * Create criterion that checks if "collection" field's size is less than
	// or equal to given size.
	// *
	// * @param field
	// * field
	// * @param size
	// * size
	// * @return criterion
	// */
	// public static MobeelizerCriterion sizeLe(final String field, final int
	// size) {
	// return null;
	// }
	//
	// /**
	// * Create criterion that checks if "collection" field's size is less than
	// given size.
	// *
	// * @param field
	// * field
	// * @param size
	// * size
	// * @return criterion
	// */
	// public static MobeelizerCriterion sizeLt(final String field, final int
	// size) {
	// return null;
	// }
	//
	// /**
	// * Create criterion that checks if "collection" field's size is greater
	// than or equal to given size.
	// *
	// * @param field
	// * field
	// * @param size
	// * size
	// * @return criterion
	// */
	// public static MobeelizerCriterion sizeGe(final String field, final int
	// size) {
	// return null;
	// }
	//
	// /**
	// * Create criterion that checks if "collection" field's size is greater
	// than given size.
	// *
	// * @param field
	// * field
	// * @param size
	// * size
	// * @return criterion
	// */
	// public static MobeelizerCriterion sizeGt(final String field, final int
	// size) {
	// return null;
	// }
	//
	// /**
	// * Create criterion that checks if "collection" field's size isn't equal
	// to given size.
	// *
	// * @param field
	// * field
	// * @param size
	// * size
	// * @return criterion
	// */
	// public static MobeelizerCriterion sizeNe(final String field, final int
	// size) {
	// return null;
	// }
	//
	// /**
	// * Create criterion that checks if "collection" field's size is empty.
	// *
	// * @param field
	// * field
	// * @return criterion
	// */
	// public static MobeelizerCriterion isEmpty(final String field) {
	// return null;
	// }
	//
	// /**
	// * Create criterion that checks if "collection" field's size isn't empty.
	// *
	// * @param field
	// * field
	// * @return criterion
	// */
	// public static MobeelizerCriterion isNotEmpty(final String field) {
	// return null;
	// }

}
