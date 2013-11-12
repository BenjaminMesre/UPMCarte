/**
 *  Filename: Location.java (in org.repin.base.core)
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
package org.redpin.base.core;

import org.redpin.base.core.Map;



/**
 * Describes a location with containing a label and map with corresponding pixel
 * coordinates
 * 
 * @author Philipp Bolliger (philipp@bolliger.name)
 * @author Simon Tobler (simon.p.tobler@gmx.ch)
 * @author Davide Spena (davide.spena@gmail.com)
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @version 0.2
 */
public class Location {


	/*
	 * unique identifier, commonly the name of this location e.g. 'IFW D47.2'
	 */
	protected String symbolicID = "";
	
	protected String buildingNAME = "";
	
	protected String roomNAME = "";
	
	protected String typeOfROOM = "";
	
	protected String openingTIME = "";
	
	protected String closingTIME = "";

	/*
	 * the Map where this location resides. includes path to image and a name
	 */
	protected Map map;

	/*
	 * X and Y coordinates of the location in the image referenced by fileName
	 * in pixel format
	 */
	protected float mapXcord = 0;
	protected float mapYcord = 0;
	protected float mapZcord = 0;		//For floors

	/*
	 * StaticResources.LOCATION_UNKNOWN = location totally unknown
	 * StaticResources.LOCATION_KNOWN = location known Numbers in between define
	 * level of accuracy
	 */
	protected int accuracy = 0;
	
	/* **************** Constructors **************** */

	public Location() {
		this("","","", "", "", "", new Map(), 0, 0, 0, 0, -1);
	}

	public Location(String symbolicId, Map map, float mapXcord, float mapYcord,
			int accuracy, int reflocationId) {
		this.symbolicID = symbolicId;
		this.map = map;
		this.mapXcord = mapXcord;
		this.mapYcord = mapYcord;
		this.accuracy = accuracy;
	}
	
	public Location(String symbolicId,String buildingName , String typeOfRoom, String roomName , 
			String openingTime , String closingTime, Map map, 
			float mapXcord, float mapYcord, float mapZcord,	int accuracy, int reflocationId) {
		this.symbolicID = symbolicId;
		this.buildingNAME = buildingName;
		this.typeOfROOM = typeOfRoom;
		this.roomNAME = roomName;
		this.openingTIME = openingTime;
		this.closingTIME = closingTime;
		this.map = map;
		this.mapXcord = mapXcord;
		this.mapYcord = mapYcord;
		this.mapZcord = mapZcord;
		this.accuracy = accuracy;
	}

	/* **************** Getter and Setter Methods **************** */

	/**
	 * @return accuracy
	 */
	public int getAccuracy() {
		return accuracy;
	}

	/**
	 * @param accuracy
	 */
	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * @return the symbolicID
	 */
	public String getSymbolicID() {
		return symbolicID;
	}

	/**
	 * @param symbolicID
	 *            the symbolicID to set
	 */
	public void setSymbolicID(String symbolicID) {
		this.symbolicID = symbolicID;
	}

	//**************************************

		/**
		 * @return the buildingNAME
		 */
		public String getBatimentID() {
			return buildingNAME;
		}

		/**
		 * @param buildingNAME
		 *            the buildingNAME to set
		 */
		public void setBatimentID(String buildingNAME) {
			this.buildingNAME = buildingNAME;
		}
		
		/**
		 * @return the roomNAME
		 */
		public String getSalleID() {
			return roomNAME;
		}

		/**
		 * @param roomNAME
		 *            the roomNAME to set
		 */
		public void setSalleID(String roomNAME) {
			this.roomNAME = roomNAME;
		}
		
		/**
		 * @return the typeOfRoom
		 */
		public String getTypeSalleID() {
			return typeOfROOM;
		}

		/**
		 * @param typeOfRoom
		 *            the typeOfRoom to set
		 */
		public void setTypeSalleID(String typeOfROOM) {
			this.typeOfROOM = typeOfROOM;
		}
		/**
		 * @return the openingTime
		 */
		public String getOpeningTIME() {
			return openingTIME;
		}

		/**
		 * @param openingTime
		 *            the openingTime to set
		 */
		public void setOpeningTIME(String openingTIME) {
			this.openingTIME = openingTIME;
		}
		
		/**
		 * @return the closingTime
		 */
		public String getClosingTIME() {
			return closingTIME;
		}

		/**
		 * @param closingTime
		 *            the closingTime to set
		 */
		public void setClosingTIME(String closingTIME) {
			this.closingTIME = closingTIME;
		}
		//***********************
		
	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public float getMapXcord() {
		return mapXcord;
	}

	public void setMapXcord(float mapXcord) {
		this.mapXcord = mapXcord;
	}

	public float getMapYcord() {
		return mapYcord;
	}

	public void setMapYcord(float mapYcord) {
		this.mapYcord = mapYcord;
	}
	
	public float getMapZcord() {
		return mapZcord;
	}

	public void setMapZcord(float mapZcord) {
		this.mapZcord = mapZcord;
	}
	
}