/**
 *  Filename: EntityHomeFactory.java (in org.repin.android.db)
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
package org.redpin.android.db;

/**
 * Factory for all EntityHomes
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class EntityHomeFactory {

	private static MapHome mapHome;

	public static MapHome getMapHome() {
		if (mapHome == null) {
			mapHome = new MapHome();
		}
		return mapHome;
	}

	private static LocationHome locHome;

	public static LocationHome getLocationHome() {
		if (locHome == null) {
			locHome = new LocationHome();
		}
		return locHome;
	}
	private static SpinnerDataHome spindatdaHome;
	
	public static SpinnerDataHome getSpinnerDataHome() {
		if (spindatdaHome == null) {
			spindatdaHome = new SpinnerDataHome();
		}
		return spindatdaHome;
	}
	
}
