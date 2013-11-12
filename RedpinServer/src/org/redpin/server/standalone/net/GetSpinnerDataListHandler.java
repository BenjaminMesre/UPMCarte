/**
 *  Filename: GetSpinnerDataListHandler.java (in org.redpin.server.standalone.net)
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
package org.redpin.server.standalone.net;

import java.util.List;

import org.redpin.server.standalone.core.SpinnerData;
import org.redpin.server.standalone.db.HomeFactory;
import org.redpin.server.standalone.db.homes.SpinnerDataHome;
import org.redpin.server.standalone.net.Response.Status;
import org.redpin.server.standalone.util.Log;

import com.google.gson.JsonElement;

/**
 * @see IHandler
 * @author Davy Ushaka Ishimwe
 *
 */
public class GetSpinnerDataListHandler implements IHandler {
	
	
	SpinnerDataHome spindataHome;
	
	public GetSpinnerDataListHandler() {
		spindataHome = HomeFactory.getSpinnerDataHome();
	}
		
	/**
	 * @see IHandler#handle(JsonElement)
	 */
	@Override
	public Response handle(JsonElement data) {
		
		Response res;
		
		List<SpinnerData> spindatas = spindataHome.getAll();
		
		if(spindatas.contains(null)) {
			res = new Response(Status.failed, "could not fetch all spindatas", null);
			Log.getLogger().fine("could not fetch all spindatas");
		} else {
			res = new Response(Status.ok, null, spindatas);
			Log.getLogger().finer("fetched "+ spindatas.size()+ " spindatas");
		}
		
		return res;
	}

}
