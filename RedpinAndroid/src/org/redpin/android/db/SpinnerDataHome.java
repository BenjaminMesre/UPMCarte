/**
 *  Filename: SpinnerDataHome.java (in org.repin.android.core)
 *  This file is part of the Redpin project.
 * 
 *  Redpin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  Redpin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Redpin. If not, see <http://www.gnu.org/licenses/>.
 *
 *  (c) Copyright Polytech Paris-UPMC, Benjamin Mesre, Davy Ushaka Ishimwe, 2012, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */

package org.redpin.android.db;

import java.util.List;

import org.redpin.android.core.Map;
import org.redpin.android.core.SpinnerData;
import org.redpin.android.provider.RedpinContract;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * {@link EntityHome} for {@link SpinnerData}s
 * @see EntityHome
 * @author Davy Ushaka Ishimwe 
 * 
 */
public class SpinnerDataHome extends EntityHome<SpinnerData> {

	/**
	 * @see EntityHome#EntityHome()
	 */
	public SpinnerDataHome() {
		super();
	}

	/**
	 * @see EntityHome#EntityHome(ContentResolver)
	 */
	public SpinnerDataHome(ContentResolver resolver) {
		super(resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Uri contentUri() {
		return RedpinContract.SpinnerData.CONTENT_URI;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SpinnerData fromCursorRow(Cursor cursor) {
		SpinnerData res = new SpinnerData();
		res.setLocalId(cursor.getLong(cursor
				.getColumnIndex(RedpinContract.SpinnerData._ID)));
		res.setRemoteId(cursor.getInt(cursor
				.getColumnIndex(RedpinContract.SpinnerData.REMOTE_ID)));
		res.setBuildingName(cursor.getString(cursor
				.getColumnIndex(RedpinContract.SpinnerData.BUILDING_NAME)));

		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ContentValues toContentValues(SpinnerData e) {
		ContentValues v = new ContentValues();
		if (e == null) {
			return v;
		}
		v.put(RedpinContract.SpinnerData.REMOTE_ID, e.getRemoteId());
		v.put(RedpinContract.SpinnerData.BUILDING_NAME, e.getBuildingName());

		return v;
	}
	
	/**
	 * Gets the {@link SpinnerData} by its remote id
	 * 
	 * @param id remote id of entity
	 * @return {@link SpinnerData} for specified remote id if available, otherwise <code>null</code>
	 */
	public SpinnerData getByRemoteId(Integer id) {
		Uri uri = ContentUris.appendId(contentUri().buildUpon(), id)
				.appendQueryParameter(RedpinContract.REMOTE_PARAMETER, "1")
				.build();

		List<SpinnerData> res = fromCursor(resolver.query(uri, null, null, null, null));
		if (res.size() < 1) {
			return null;
		}
		return res.get(0);
	}

}
