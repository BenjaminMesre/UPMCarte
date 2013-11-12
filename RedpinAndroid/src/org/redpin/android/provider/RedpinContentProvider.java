/**
 *  Filename: RedpinContentProvider.java (in org.repin.android.provider)
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
 *  (c) Copyright ETH Zurich, Pascal Brogle, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.android.provider;

import org.redpin.android.core.Location;
import org.redpin.android.core.Map;
import org.redpin.android.core.SpinnerData;
import org.redpin.android.net.home.SpinnerDataRemoteHome;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Redpin {@link ContentProvider} for {@link Map}s and {@link Location}s
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class RedpinContentProvider extends ContentProvider {

	private SQLiteDatabase mDB;
	private DatabaseHelper dbHelper;

	private static final String TAG = RedpinContentProvider.class
			.getSimpleName();
	private static final String DATABASE_NAME = "redpin.db";
	private static final int DATABASE_VERSION = 1;

	private static final String MAP_TABLE = "map";
	private static final String LOCATION_TABLE = "location";
	private static final String SPINDATA_TABLE = "spinnerdata";
	

	private static final int MAP = 1;
	private static final int MAP_ID = 2;
	private static final int MAP_LOCATIONS = 3;
	private static final int MAP_LOCATIONS_ID = 4;
	private static final int LOCATION = 5;
	private static final int LOCATION_ID = 6;
	private static final int SPINNERDATA = 7;
	private static final int SPINNERDATA_ID = 8;

	private static final UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);

	private static final String REMOTE_PARAMETER_DISALOWED = "remote parameter not allowed in URL ";

	static {
		URI_MATCHER.addURI(RedpinContract.AUTHORITY,
				RedpinContract.Map.PATH_SEGMENT, MAP);
		URI_MATCHER.addURI(RedpinContract.AUTHORITY,
				RedpinContract.Map.PATH_SEGMENT + "/#", MAP_ID);
		URI_MATCHER.addURI(RedpinContract.AUTHORITY,
				RedpinContract.Map.PATH_SEGMENT + "/#/"
						+ RedpinContract.Location.PATH_SEGMENT, MAP_LOCATIONS);
		URI_MATCHER.addURI(RedpinContract.AUTHORITY,
				RedpinContract.Map.PATH_SEGMENT + "/#/"
						+ RedpinContract.Location.PATH_SEGMENT + "/#",
				MAP_LOCATIONS_ID);
		URI_MATCHER.addURI(RedpinContract.AUTHORITY,
				RedpinContract.Location.PATH_SEGMENT, LOCATION);
		URI_MATCHER.addURI(RedpinContract.AUTHORITY,
				RedpinContract.Location.PATH_SEGMENT + "/#", LOCATION_ID);
		URI_MATCHER.addURI(RedpinContract.AUTHORITY,
				RedpinContract.SpinnerData.PATH_SEGMENT, SPINNERDATA);
		URI_MATCHER.addURI(RedpinContract.AUTHORITY,
				RedpinContract.SpinnerData.PATH_SEGMENT + "/#", SPINNERDATA_ID);
	}

	/**
	 * {@link SQLiteOpenHelper} that handles database creation and version
	 * management
	 * 
	 * @see SQLiteOpenHelper
	 * 
	 * @author Pascal Brogle (broglep@student.ethz.ch)
	 * 
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		/**
		 * Creates the Helper
		 * 
		 * @param context
		 *            to use to open or create the database
		 */
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + MAP_TABLE + " ("
					+ RedpinContract.Map._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ RedpinContract.Map.REMOTE_ID + " INTEGER,"
					+ RedpinContract.Map.NAME + " TEXT,"
					+ RedpinContract.Map.URL + " TEXT );");
			db.execSQL("CREATE TABLE " + LOCATION_TABLE + " ("
					+ RedpinContract.Location._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ RedpinContract.Location.REMOTE_ID + " INTEGER,"
					+ RedpinContract.Location._MAP_ID + " INTEGER,"
					+ RedpinContract.Location.SYMBOLIC_ID + " TEXT,"
					+ RedpinContract.Location.BUILDING_NAME + " TEXT,"
					+ RedpinContract.Location.TYPEROOM_NAME + " TEXT,"					
					+ RedpinContract.Location.ROOM_NAME + " TEXT,"		
					+ RedpinContract.Location.OPENING_TIME + " TEXT,"
					+ RedpinContract.Location.CLOSING_TIME + " TEXT,"
					
					+ RedpinContract.Location.X + " FLOAT,"
					+ RedpinContract.Location.Y + " FLOAT,"
					+ RedpinContract.Location.Z + " FLOAT );");
			//***********************************************************
			db.execSQL("CREATE TABLE " + SPINDATA_TABLE + " ("
					+ RedpinContract.SpinnerData._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ RedpinContract.SpinnerData.REMOTE_ID + " INTEGER,"
					+ RedpinContract.SpinnerData.BUILDING_NAME + " TEXT );");
			db.execSQL("INSERT INTO "+ SPINDATA_TABLE + "( buildingName) VALUES ('Ajoutez')" );
			
//			SpinnerData dataspin = new SpinnerData();
//			dataspin.setRemoteId(1);
//			dataspin.setBuildingName(" ");
//			SpinnerDataRemoteHome.setSpinnerData(dataspin);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + MAP_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + SPINDATA_TABLE);
			
			onCreate(db);
		}

	}

	/**
	 * Opens or creates the database
	 * 
	 * @return <code>true</code> if successful
	 */
	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		mDB = dbHelper.getWritableDatabase();
		return mDB != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count;
		String segment;

		int match = URI_MATCHER.match(uri);
		boolean remote = uri.getQueryParameter(RedpinContract.REMOTE_PARAMETER) != null;

		switch (match) {
		case MAP:

			count = mDB.delete(MAP_TABLE, selection, selectionArgs);
			count += mDB.delete(LOCATION_TABLE, "1", null);
			Log.d(TAG, "deleting all maps and locations");

			break;

		case MAP_ID:
			if (remote) {
				throw new IllegalArgumentException(REMOTE_PARAMETER_DISALOWED
						+ uri);
			}

			segment = uri.getPathSegments().get(1);

			count = mDB.delete(MAP_TABLE, RedpinContract.Map._ID
					+ "="
					+ segment
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			count += mDB.delete(LOCATION_TABLE, RedpinContract.Location._MAP_ID
					+ "=" + segment, null);

			Log.d(TAG, "deleting map #" + segment
					+ " and corresponding locations");

			break;

		case MAP_LOCATIONS:
			if (remote) {
				throw new IllegalArgumentException(REMOTE_PARAMETER_DISALOWED
						+ uri);
			}
			segment = uri.getPathSegments().get(1);

			count = mDB.delete(LOCATION_TABLE, RedpinContract.Location._MAP_ID
					+ "="
					+ segment
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			Log.d(TAG, "deleting all locations of map #" + segment);
			break;
		case MAP_LOCATIONS_ID:
			segment = uri.getPathSegments().get(3);

			count = mDB.delete(LOCATION_TABLE,
					(remote ? RedpinContract.Location.REMOTE_ID
							: RedpinContract.Location._ID)
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			Log.d(TAG, "deleting location #" + segment);
			break;
		case LOCATION:
			count = mDB.delete(LOCATION_TABLE, selection, selectionArgs);
			Log.d(TAG, "deleting all locations and databuildings");
			break;
		case LOCATION_ID:
			segment = uri.getPathSegments().get(1);

			count = mDB.delete(LOCATION_TABLE,
					(remote ? RedpinContract.Location.REMOTE_ID
							: RedpinContract.Location._ID)
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			Log.d(TAG, "deleting location #" + segment);
			break;
		case SPINNERDATA:
			count = mDB.delete(SPINDATA_TABLE, "1", null);
			Log.d(TAG, "deleting all databuildings");
			break;
		case SPINNERDATA_ID:
			if (remote) {
				throw new IllegalArgumentException(REMOTE_PARAMETER_DISALOWED + uri);
			}

			segment = uri.getPathSegments().get(1);

			count = mDB.delete(SPINDATA_TABLE, RedpinContract.SpinnerData._ID
					+ "="
					+ segment
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);

			Log.d(TAG, "deleting spinnerdata #" + segment);
			break;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType(Uri uri) {
		int match = URI_MATCHER.match(uri);
		switch (match) {
		case MAP:
			return RedpinContract.Map.LIST_TYPE; // "vnd.android.cursor.dir/vnd.redpin.maps";
		case MAP_ID:
			return RedpinContract.Map.ITEM_TYPE; // "vnd.android.cursor.item/vnd.redpin.maps";
		case MAP_LOCATIONS:
			return RedpinContract.Location.LIST_TYPE; // "vnd.android.cursor.dir/vnd.redpin.locations";
		case MAP_LOCATIONS_ID:
			return RedpinContract.Location.ITEM_TYPE; // "vnd.android.cursor.item/vnd.redpin.locations";
		case LOCATION:
			return RedpinContract.Location.LIST_TYPE; // "vnd.android.cursor.dir/vnd.redpin.locations";
		case LOCATION_ID:
			return RedpinContract.Location.ITEM_TYPE; // "vnd.android.cursor.item/vnd.redpin.locations";
		case SPINNERDATA:
			return RedpinContract.SpinnerData.LIST_TYPE; // "vnd.android.cursor.dir/vnd.redpin.spinnerdatas";
		case SPINNERDATA_ID:
			return RedpinContract.SpinnerData.ITEM_TYPE; // "vnd.android.cursor.item/vnd.redpin.spinnerdatas";
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int match = URI_MATCHER.match(uri);
		boolean remote = uri.getQueryParameter(RedpinContract.REMOTE_PARAMETER) != null;
		switch (match) {
		case MAP:
			return insertMap(values);

		case MAP_LOCATIONS:
			if (remote) {
				throw new IllegalArgumentException(REMOTE_PARAMETER_DISALOWED
						+ uri);
			}
			values.put(RedpinContract.Location._MAP_ID, uri.getPathSegments()
					.get(1));
			
			return insertLocation(values);
		case SPINNERDATA:
			return insertSpinnerData(values);
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	/**
	 * Insert an {@link Map} into the database
	 * 
	 * @param initialValues
	 *            {@link ContentValues} representing a {@link Map}
	 * @return {@link Uri} of the inserted {@link Map}
	 */
	public Uri insertMap(ContentValues initialValues) {
		long rowID;
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		Resources r = Resources.getSystem();

		if (values.containsKey(RedpinContract.Map.REMOTE_ID) == false) {
			values.put(RedpinContract.Map.REMOTE_ID, -1);
		}

		if (values.containsKey(RedpinContract.Map.NAME) == false) {
			values.put(RedpinContract.Map.NAME, r
					.getString(android.R.string.untitled));
		}

		if (values.containsKey(RedpinContract.Map.URL) == false) {
			values.put(RedpinContract.Map.URL, "");
		}

		rowID = mDB.insert(MAP_TABLE, RedpinContract.Map.NAME, values);
		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(
					RedpinContract.Map.CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row");
	}

	/**
	 * Insert an {@link Location} into the database
	 * 
	 * @param initialValues
	 *            {@link ContentValues} representing a {@link Location}
	 * @return {@link Uri} of the inserted {@link Map}
	 */
	public Uri insertLocation(ContentValues values) {
		long rowID;
		Resources r = Resources.getSystem();

		if (values.containsKey(RedpinContract.Location.REMOTE_ID) == false) {
			values.put(RedpinContract.Location.REMOTE_ID, -1);
		}

		if (values.containsKey(RedpinContract.Location.X) == false
				|| values.containsKey(RedpinContract.Location.Y) == false
				|| values.containsKey(RedpinContract.Location.Z) == false) {
			Log
					.e(TAG,
							"the x and y and z need to be set in order to insert a point.");
		}

		//************************************************************************
		if (values.containsKey(RedpinContract.Location.BUILDING_NAME) == false) {
			values.put(RedpinContract.Location.BUILDING_NAME, r
					.getString(android.R.string.untitled));
		}
		
		if (values.containsKey(RedpinContract.Location.TYPEROOM_NAME) == false) {
			values.put(RedpinContract.Location.TYPEROOM_NAME, r
					.getString(android.R.string.untitled));
		}
		
		if (values.containsKey(RedpinContract.Location.ROOM_NAME) == false) {
			values.put(RedpinContract.Location.ROOM_NAME, r
					.getString(android.R.string.untitled));
		}
		
		if (values.containsKey(RedpinContract.Location.OPENING_TIME) == false) {
			values.put(RedpinContract.Location.OPENING_TIME, r
					.getString(android.R.string.untitled));
		}
		if (values.containsKey(RedpinContract.Location.CLOSING_TIME) == false) {
			values.put(RedpinContract.Location.CLOSING_TIME, r
					.getString(android.R.string.untitled));
		}
		//************************************************************************
		if (values.containsKey(RedpinContract.Location.SYMBOLIC_ID) == false) {
			values.put(RedpinContract.Location.SYMBOLIC_ID, r
					.getString(android.R.string.untitled));
		}
		
		rowID = mDB.insert(LOCATION_TABLE, RedpinContract.Location.SYMBOLIC_ID,
				values);
		if (rowID > 0) {
			Uri uri = ContentUris
					.appendId(
							RedpinContract.Map.CONTENT_URI
									.buildUpon()
									.appendEncodedPath(
											values
													.getAsString(RedpinContract.Location._MAP_ID))
									.appendEncodedPath(
											RedpinContract.Location.PATH_SEGMENT),
							rowID).build();

			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row");
	}
	
	/**
	 * Insert an {@link SpinnerData} into the database
	 * 
	 * @param initialValues
	 *            {@link ContentValues} representing a {@link SpinnerData}
	 * @return {@link Uri} of the inserted {@link SpinnerData}
	 */
	public Uri insertSpinnerData(ContentValues initialValues) {
		long rowID;
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		Resources r = Resources.getSystem();

		if (values.containsKey(RedpinContract.SpinnerData.REMOTE_ID) == false) {
			values.put(RedpinContract.SpinnerData.REMOTE_ID, -1);
		}

		if (values.containsKey(RedpinContract.SpinnerData.BUILDING_NAME) == false) {
			values.put(RedpinContract.SpinnerData.BUILDING_NAME, r
					.getString(android.R.string.untitled));
		}

		rowID = mDB.insert(SPINDATA_TABLE, RedpinContract.SpinnerData.BUILDING_NAME, values);
		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(
					RedpinContract.SpinnerData.CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row");
	}

	
	
	//***************************************************
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		boolean remote = uri.getQueryParameter(RedpinContract.REMOTE_PARAMETER) != null;

		switch (URI_MATCHER.match(uri)) {
		case MAP:
			qb.setTables(MAP_TABLE);

			break;
		case MAP_ID:
			qb.setTables(MAP_TABLE);
			qb.appendWhere((remote ? RedpinContract.Map.REMOTE_ID
					: RedpinContract.Map._ID)
					+ "=" + uri.getPathSegments().get(1));
			break;
		case MAP_LOCATIONS:
			if (remote) {
				throw new IllegalArgumentException(REMOTE_PARAMETER_DISALOWED
						+ uri);
			}
			qb.setTables(LOCATION_TABLE);
			qb.appendWhere(RedpinContract.Location._MAP_ID + "="
					+ uri.getPathSegments().get(1));

			break;
		case MAP_LOCATIONS_ID:
			if (remote) {
				throw new IllegalArgumentException(REMOTE_PARAMETER_DISALOWED
						+ uri);
			}

			qb.setTables(LOCATION_TABLE);
			qb.appendWhere(RedpinContract.Location._MAP_ID + "="
					+ uri.getPathSegments().get(1));
			qb.appendWhere(" AND " + RedpinContract.Location._ID + "="
					+ uri.getPathSegments().get(3));
			break;
		case LOCATION:
			qb.setTables(LOCATION_TABLE);
			break;
		case LOCATION_ID:
			qb.setTables(LOCATION_TABLE);

			qb.appendWhere((remote ? RedpinContract.Map.REMOTE_ID
					: RedpinContract.Map._ID)
					+ "=" + uri.getPathSegments().get(1));

			break;
		case SPINNERDATA:
			qb.setTables(SPINDATA_TABLE);
			break;
		case SPINNERDATA_ID:
			qb.setTables(SPINDATA_TABLE);

			qb.appendWhere((remote ? RedpinContract.SpinnerData.REMOTE_ID
					: RedpinContract.SpinnerData._ID)
					+ "=" + uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}

		Cursor c = qb.query(mDB, projection, selection, selectionArgs, null,
				null, null);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		String segment;
		int count;
		int match = URI_MATCHER.match(uri);
		boolean remote = uri.getQueryParameter(RedpinContract.REMOTE_PARAMETER) != null;
		switch (match) {
		case MAP_ID:
			segment = uri.getPathSegments().get(1);

			count = mDB.update(MAP_TABLE, values,
					(remote ? RedpinContract.Map.REMOTE_ID
							: RedpinContract.Map._ID)
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;

		case MAP_LOCATIONS_ID:
			segment = uri.getPathSegments().get(3);

			count = mDB.update(LOCATION_TABLE, values,
					(remote ? RedpinContract.Location.REMOTE_ID
							: RedpinContract.Location._ID)
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		case LOCATION_ID:
			segment = uri.getPathSegments().get(1);

			count = mDB.update(LOCATION_TABLE, values,
					(remote ? RedpinContract.Location.REMOTE_ID
							: RedpinContract.Location._ID)
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		case SPINNERDATA_ID:
			segment = uri.getPathSegments().get(1);

			count = mDB.update(SPINDATA_TABLE, values,
					(remote ? RedpinContract.SpinnerData.REMOTE_ID
							: RedpinContract.SpinnerData._ID)
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
