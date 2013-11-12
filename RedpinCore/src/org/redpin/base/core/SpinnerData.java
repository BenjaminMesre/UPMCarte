/**
 *  Filename: SpinnerData.java (in org.repin.base.core)
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
package org.redpin.base.core;




/**
 * Describe a SpinnerData
 * @author Davy Ushaka Ishimwe 
 * 
 */
public class SpinnerData {

	
	/*
	 * unique identifier, commonly the name of this map e.g. 'IFW floor A'
	 */
	protected String buildingName = "";


	/* **************** Constructors **************** */

	public SpinnerData() {
		buildingName = "";
	}

	public SpinnerData(String buildingName) {
		super();
		this.buildingName = buildingName;
	}

	/* **************** Getter and Setter Methods **************** */

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	
	public String toString() {
		return super.toString() + ": " + buildingName ;
	}
}