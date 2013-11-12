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
package org.redpin.server.standalone.db.homes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.logging.Level;

import org.redpin.server.standalone.core.SpinnerData;
import org.redpin.server.standalone.db.HomeFactory;

/**
 * @see EntityHome
 * @author Davy Ushaka Ishimwe
 * 
 */
public class SpinnerDataHome extends EntityHome<SpinnerData> {

	private static final String[] TableCols = { "buildingName" };
	private static final String TableName = "spinnerdata";
	private static final String TableIdCol = "buildingId";

	public SpinnerDataHome() {
		super();
	}

	/**
	 * @see EntityHome#getTableName()
	 */
	@Override
	protected String getTableName() {
		return TableName;
	}

	/**
	 * @see EntityHome#getTableIdCol()
	 */
	@Override
	protected String getTableIdCol() {
		return TableIdCol;
	}

	/**
	 * @see EntityHome#getTableCols()
	 */
	@Override
	protected String[] getTableCols() {
		return TableCols;
	}

	/**
	 * @see EntityHome#parseResultRow(ResultSet, int)
	 */
	@Override
	public SpinnerData parseResultRow(final ResultSet rs, int fromIndex)
			throws SQLException {
		SpinnerData spindata = new SpinnerData();

		try {
			spindata.setId(rs.getInt(fromIndex));
			spindata.setBuildingName(rs.getString(fromIndex + 1));

		} catch (SQLException e) {
			log.log(Level.SEVERE, "parseResultRow failed: " + e.getMessage(), e);
			throw e;
		}

		return spindata;
	}

	/**
	 * @see EntityHome#remove(String)
	 */
	@Override
	public boolean remove(String constraint) {
		// remove all locations and fingerprints for the spindata, then remove
		// spindata
		String spindataCnst = (constraint != null && constraint.length() > 0) ? constraint
				: "1=1";
		// String locationCnst = HomeFactory.getLocationHome().getTableIdCol() +
		// " IN (SELECT " + HomeFactory.getLocationHome().getTableIdCol() +
		// " FROM " + HomeFactory.getLocationHome().getTableName() +
		// " WHERE " + spindataCnst + ")";
		// String fingerprintsCnst =
		// HomeFactory.getFingerprintHome().getTableIdCol() + " IN (SELECT " +
		// HomeFactory.getFingerprintHome().getTableIdCol() +
		// " FROM " + HomeFactory.getFingerprintHome().getTableName() +
		// " WHERE (" + locationCnst + ")) ";
		// String measurementsCnst =
		// HomeFactory.getMeasurementHome().getTableIdCol() + " IN (SELECT " +
		// HomeFactory.getFingerprintHome().getTableCols()[1] +
		// " FROM " + HomeFactory.getFingerprintHome().getTableName() +
		// " WHERE (" + fingerprintsCnst + ")) ";
		// String readingInMeasurementCnst =
		// " IN (SELECT readingId FROM readinginmeasurement WHERE (" +
		// measurementsCnst + ")) ";
		//
		//
		// String sql_m = " DELETE FROM " +
		// HomeFactory.getMeasurementHome().getTableName() + " WHERE " +
		// measurementsCnst;
		// String sql_wifi = " DELETE FROM " +
		// HomeFactory.getWiFiReadingHome().getTableName() +
		// " WHERE " + HomeFactory.getWiFiReadingHome().getTableIdCol() +
		// readingInMeasurementCnst;
		// String sql_gsm = " DELETE FROM " +
		// HomeFactory.getGSMReadingHome().getTableName() +
		// " WHERE " + HomeFactory.getGSMReadingHome().getTableIdCol() +
		// readingInMeasurementCnst;
		// String sql_bluetooth = " DELETE FROM " +
		// HomeFactory.getBluetoothReadingHome().getTableName() +
		// " WHERE " + HomeFactory.getBluetoothReadingHome().getTableIdCol() +
		// readingInMeasurementCnst;
		//
		// String sql_rinm = "DELETE FROM readinginmeasurement WHERE " +
		// measurementsCnst;
		// String sql_fp = "DELETE FROM " +
		// HomeFactory.getFingerprintHome().getTableName() + " WHERE " +
		// locationCnst;
		//
		// String sql_l = "DELETE FROM " +
		// HomeFactory.getLocationHome().getTableName() + " WHERE " +
		// spindataCnst;
		String sql_spindata = "DELETE FROM " + getTableName() + " WHERE "
				+ spindataCnst;
		System.out.println(spindataCnst + "\n");
		Statement stat = null;

		// log.finest(sql_wifi);
		// log.finest(sql_gsm);
		// log.finest(sql_bluetooth);
		// log.finest(sql_rinm);
		// log.finest(sql_m);
		// log.finest(sql_fp);
		// log.finest(sql_l);
		log.finest(sql_spindata);
		try {
			int res = -1;
			db.getConnection().setAutoCommit(false);
			stat = db.getConnection().createStatement();
			if (db.getConnection().getMetaData().supportsBatchUpdates()) {
				// stat.addBatch(sql_wifi);
				// stat.addBatch(sql_gsm);
				// stat.addBatch(sql_bluetooth);
				// stat.addBatch(sql_rinm);
				// stat.addBatch(sql_fp);
				// stat.addBatch(sql_m);
				// stat.addBatch(sql_l);
				stat.addBatch(sql_spindata);
				int results[] = stat.executeBatch();
				if (results != null && results.length > 0) {
					res = results[results.length - 1];
				}
			} else {
				// stat.executeUpdate(sql_wifi);
				// stat.executeUpdate(sql_gsm);
				// stat.executeUpdate(sql_bluetooth);
				// stat.executeUpdate(sql_rinm);
				// stat.executeUpdate(sql_fp);
				// stat.executeUpdate(sql_m);
				// stat.executeUpdate(sql_l);
				res = stat.executeUpdate(sql_spindata);
			}
			db.getConnection().commit();
			return res > 0;
		} catch (SQLException e) {
			log.log(Level.SEVERE, "remove spindata failed: " + e.getMessage(),
					e);
		} finally {
			try {
				db.getConnection().setAutoCommit(true);
				if (stat != null)
					stat.close();
			} catch (SQLException es) {
				log.log(Level.WARNING,
						"failed to close statement: " + es.getMessage(), es);
			}
		}
		return false;

	}

	/**
	 * @see EntityHome#fillInStatement(PreparedStatement,
	 *      org.redpin.server.standalone.db.IEntity, int)
	 */
	@Override
	public int fillInStatement(PreparedStatement ps, SpinnerData t,
			int fromIndex) throws SQLException {
		return fillInStatement(ps, new Object[] { t.getBuildingName() },
				new int[] { Types.VARCHAR }, fromIndex);
	}

}
