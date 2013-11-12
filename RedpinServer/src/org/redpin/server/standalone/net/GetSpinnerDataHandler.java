/**
 *  Filename: SpinnerData.java (in org.repin.android.core)
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
package org.redpin.server.standalone.net;

import java.util.logging.Logger;

import org.redpin.server.standalone.net.Response.Status;
import org.redpin.server.standalone.util.Log;

import com.google.gson.JsonElement;


public class GetSpinnerDataHandler implements IHandler {

	
	private Logger log;	
	
	public GetSpinnerDataHandler() {
		log = Log.getLogger();
	}
	
	
//	/**
//	 * @see IHandler#handle(JsonElement)
//	 */
//	@Override
	public Response handle(JsonElement data) {
		Response res;
		//SpinnerData spindata;
		
		//Measurement currentMeasurement = GsonFactory.getGsonInstance().fromJson(data, Measurement.class);
		log.finer("got measurement: " + data);
		
//		spindata = LocatorHome.getLocator().locateb(currentMeasurement);
//		
//			
//		if(spindata == null) {
//			log.fine("no matching spinnerdata found");
//			res = new Response(Status.failed, "Building no find", null);
//			
//		} else {
//			res = new Response(Status.ok, null, spindata);
//			log.finer("building found: " + spindata );
//		}
//		
		
		return res = new Response(Status.failed, "Building no find code dans GetSpinnerDataHandler", null);
	}
	

}
