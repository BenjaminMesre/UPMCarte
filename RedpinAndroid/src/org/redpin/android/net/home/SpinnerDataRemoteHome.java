/**
 *  Filename: SpinnerDataRemoteHome.java (in org.repin.android.net.home)
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
package org.redpin.android.net.home;

import java.util.HashMap;
import java.util.List;

import org.redpin.android.core.SpinnerData;
import org.redpin.android.db.EntityHomeFactory;
import org.redpin.android.db.SpinnerDataHome;
import org.redpin.android.net.Request;
import org.redpin.android.net.Response;
import org.redpin.android.net.Request.RequestType;

import android.util.Log;

/**
 * RemoteEntityHome for {@link SpinnerData}s
 * 
 * @author  Davy Ushaka Ishimwe
 * 
 */
public class SpinnerDataRemoteHome implements IRemoteEntityHome {

	protected SpinnerDataHome spindataHome = EntityHomeFactory.getSpinnerDataHome();

	private static final String TAG = SpinnerDataRemoteHome.class.getName();

	/**
	 * Performs an getSpinnerDataList request without callback
	 */
	public static void getSpinnerDataList() {
		RemoteEntityHome.performRequest(RequestType.getSpinnerDataList);
	}

	/**
	 * Performs an getSpinnerDataList request with callback
	 * 
	 * @param callback
	 *            {@link RemoteEntityHomeCallback}
	 */
	public static void getSpinnerDataList(RemoteEntityHomeCallback callback) {
		RemoteEntityHome.performRequest(RequestType.getSpinnerDataList, callback);
	}

	/**
	 * Performs an setSpinnerData request without callback
	 * 
	 * @param spinnerdata
	 *            {@link SpinnerData} to be added
	 */
	public static void setSpinnerData(SpinnerData spinnerdata) {
		RemoteEntityHome.performRequest(RequestType.setSpinnerData, spinnerdata);
	}

	/**
	 * Performs an setSpinnerData request with callback
	 * 
	 * @param spinnerdata
	 *            {@link SpinnerData} to be added
	 * @param callback
	 *            {@link RemoteEntityHomeCallback}
	 */
	public static void setSpinnerData(SpinnerData spinnerdata, RemoteEntityHomeCallback callback) {
		RemoteEntityHome.performRequest(RequestType.setSpinnerData, spinnerdata, callback);
	}

	/**
	 * Performs an removeSpinnerData request without callback
	 * 
	 * @param spinnerdata
	 *            {@link SpinnerData} to be removed
	 * @return <code>true</code> if request can be performed, <code>false</code>
	 *         if the {@link SpinnerData} has no remote id
	 */
	public static boolean removeSpinnerData(SpinnerData spinnerdata) {
		return removeSpinnerData(spinnerdata, null);
	}

	/**
	 * Performs an removeSpinnerData request with callback
	 * 
	 * @param spinnerdata
	 *            {@link SpinnerData} to be removed
	 * @param callback
	 *            {@link RemoteEntityHomeCallback}
	 * @return <code>true</code> if request can be performed, <code>false</code>
	 *         if the {@link SpinnerData} has no remote id
	 */
	public static boolean removeSpinnerData(SpinnerData spinnerdata, RemoteEntityHomeCallback callback) {
		if (spinnerdata.getRemoteId() == null || spinnerdata.getRemoteId() < 0) {
			Log.i(TAG, "spinnerdata can't be removed because no remote id is present");
			return false;
		}
		RemoteEntityHome.performRequest(RequestType.removeSpinnerData, spinnerdata, callback);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void onRequestPerformed(Request<?> request, Response<?> response,
			RemoteEntityHome rHome) {

		switch (request.getAction()) {
		case getSpinnerDataList:
			getSpinnerDataListPerformed((Request<Void>) request,
					(Response<List<SpinnerData>>) response);
			break;

		case setSpinnerData:
			setSpinnerDataPerformed((Request<SpinnerData>) request, (Response<SpinnerData>) response);
			break;

		case removeSpinnerData:
			removeSpinnerDataPerformed((Request<SpinnerData>) request,
					(Response<Void>) response);
			break;

		default:
			throw new IllegalArgumentException(getClass().getName()
					+ " can't handle action " + request.getAction());
		}

	}

	/**
	 * Removes the {@link SpinnerData} from the local database after it was removed on
	 * the server
	 * 
	 * @param request
	 *            {@link Request} performed
	 * @param response
	 *            {@link Response} received
	 */
	private void removeSpinnerDataPerformed(Request<SpinnerData> request,
			Response<Void> response) {

		boolean success = spindataHome.remove(request.getData());

		if (!success) {
			Log.i(TAG, "removal of spinnerdata " + request.getData() + " failed");
		}

	}

	/**
	 * Synchronizes the local {@link SpinnerData} database
	 * 
	 * @param request
	 *            {@link Request} performed
	 * @param response
	 *            {@link Response} received
	 */
	private void getSpinnerDataListPerformed(Request<Void> request,
			Response<List<SpinnerData>> response) {
		List<SpinnerData> dbList = spindataHome.getAll();
		HashMap<Integer, SpinnerData> spinnerdata = new HashMap<Integer, SpinnerData>();
		for (SpinnerData m : dbList) {
			spinnerdata.put(m.getRemoteId(), m);
		}

		List<SpinnerData> remote = response.getData();

		for (SpinnerData m : remote) {
			if (spinnerdata.containsKey(m.getRemoteId())) {
				SpinnerData dbSpinnerData = spinnerdata.get(m.getRemoteId());
				m.setLocalId(dbSpinnerData.getLocalId());

				if (!m.equals(dbSpinnerData)) {
					if (!spindataHome.update(m)) {
						Log.w(TAG, "update of spinnerdata " + m + " failed");
					}
				}
			} else {
				spindataHome.add(m);
			}
		}

		spinnerdata.clear();
		for (SpinnerData m : remote) {
			spinnerdata.put(m.getRemoteId(), m);
		}

		for (SpinnerData m : dbList) {
			if (!spinnerdata.containsKey(m.getRemoteId())) {
				spindataHome.remove(m);
			}
		}

	}

	/**
	 * Adds the {@link SpinnerData} to the local database after it was added on the
	 * server
	 * 
	 * @param request
	 *            {@link Request} performed
	 * @param response
	 *            {@link Response} received
	 */
	private void setSpinnerDataPerformed(Request<SpinnerData> request, Response<SpinnerData> response) {
		SpinnerData m = response.getData();
		spindataHome.add(m);

	}

}
