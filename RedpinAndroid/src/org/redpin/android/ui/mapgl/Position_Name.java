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
package org.redpin.android.ui.mapgl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.redpin.android.R;
import org.redpin.android.core.Map;
import org.redpin.android.core.SpinnerData;
import org.redpin.android.db.EntityHomeFactory;
import org.redpin.android.db.MapHome;
import org.redpin.android.db.SpinnerDataHome;
import org.redpin.android.net.home.MapRemoteHome;
import org.redpin.android.net.home.SpinnerDataRemoteHome;
import org.redpin.android.provider.RedpinContract;
import org.redpin.android.ui.MapGLViewActivity;
import org.redpin.android.ui.mapgl.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Display the AlertDialog that allows the input data for a pin
 * 
 * @author Benjamin Mesre, Davy Ushaka Ishimwe
 * 
 */
public class Position_Name extends Dialog {

	Context mcontext;
	SpinnerData mdataspin;
	MapGLViewActivity mActivity;
	
	// Information about the markers' data
	private static int numberOfFields = 6;
	private static int posRoomName= 0;
	private static int posFloorName= 1;
	private static int posBuildingName= 2;
	private static int posRoomType= 3;
	private static int posTimeOpening= 4;
	private static int posTimeClosing= 5;
	private static String separator = "|";
	
	private int defaultPosFloorSpinner;
	String BatimentChoisi;
	String new_building;
	int control = 0;
	String h_opening;
	String h_closing;
	int i =1;
	
	private SpinnerDataHome spindataHome;
	
	public Position_Name(Context context, MapGLViewActivity mapGLViewActivity,
			int defaultPos, SpinnerData dataspin) {
		super(context);
		mcontext = context;
		mdataspin = dataspin;
		mActivity = mapGLViewActivity;
		defaultPosFloorSpinner = defaultPos;
	}

	private static String coordonnee;
	String[] t;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.position_name);

		// Get a Spinner and bind it to an ArrayAdapter that
		// references a String array for all different floor.
		final Spinner Etage_Spinner = (Spinner) findViewById(R.id.floor);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(mcontext,
				R.array.etages, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Etage_Spinner.setAdapter(adapter);
		Etage_Spinner.setSelection(defaultPosFloorSpinner);

		// Get a Spinner and bind it to an ArrayAdapter that
		// references a String array for all different type of floor.
		final Spinner Type_de_batiment_Spinner = (Spinner) findViewById(R.id.typeoffloor);
		ArrayAdapter adapter1 = ArrayAdapter
				.createFromResource(mcontext, R.array.type_de_batiments,
						android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Type_de_batiment_Spinner.setAdapter(adapter1);

		// Load a Spinner and bind it to a data query.
		String[] PROJECTION = new String[] { RedpinContract.SpinnerData._ID,
				RedpinContract.SpinnerData.BUILDING_NAME };
		String[] DataBuilding = new String[] { RedpinContract.SpinnerData.BUILDING_NAME };

		final Spinner Batiment_Spinner = (Spinner) findViewById(R.id.building);
		final Cursor cur = ((Activity) mcontext).managedQuery(
				RedpinContract.SpinnerData.CONTENT_URI, PROJECTION, null, null,
				null);

//		if (cur.getCount() == 1) {
//			SpinnerData dataspin = new SpinnerData();
//			dataspin.setBuildingName("Ajouter un batiment");
//			SpinnerDataRemoteHome.setSpinnerData(dataspin);
//		}
		
		final SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(mcontext,
				android.R.layout.simple_spinner_item, // Use a template
														// that displays a
														// text view
				cur, // Give the cursor to the list adapter
				DataBuilding, // Map the NAME column in the
								// people database to...
				new int[] { android.R.id.text1 }); // The "text1" view defined
													// in
													// the XML template

		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Batiment_Spinner.setAdapter(adapter2);
		Batiment_Spinner.setClickable(true);

		Batiment_Spinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						String batiment_choisi = cur.getString(cur
								.getColumnIndex(RedpinContract.SpinnerData.BUILDING_NAME));
						if (batiment_choisi.equals("Ajouter un batiment")) {

							BatimentChoisi = AlertDialogBuild(mdataspin);
							control = 1;
							new_building = BatimentChoisi;
						} else
							BatimentChoisi = batiment_choisi;
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						String batiment_choisi = cur.getString(cur
								.getColumnIndex(RedpinContract.SpinnerData.BUILDING_NAME));
//						if (batiment_choisi.equals("Ajouter un batiment")) {
//
//							BatimentChoisi = AlertDialogBuild(mdataspin);
//							;
//						} else
							BatimentChoisi = batiment_choisi;
					}
				});

		Button okButton = (Button) findViewById(R.id.buttonOK);
		Button cancelButton = (Button) findViewById(R.id.buttonCancel);
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Lorsque l'on cliquera sur le bouton "OK", on récupère le
				// TextView correspondant à notre vue personnalisée
				final String etage_choisi = Etage_Spinner.getSelectedItem().toString();
				final String type_salle_choisi = Type_de_batiment_Spinner.getSelectedItem().toString();
				
				// Collecting every information
				TextView textSalle = (TextView) findViewById(R.id.room);
				TextView HourOpening = (TextView) findViewById(R.id.OpeningTime);
				TextView HourClosing = (TextView) findViewById(R.id.ClosingTime);
				String hourOp = HourOpening.getText().toString();
				String hourCl = HourClosing.getText().toString();
				
				// Putting every data in data
				String data[];
				data = new String[numberOfFields];
				// Puts empty strings to avoid errors
				for(i = 0; i < numberOfFields; i++)
				{
					data[i] = "";
				}

				if (posRoomName < numberOfFields)
				{
					data[posRoomName] = textSalle.getText().toString();
				}
				if (posFloorName < numberOfFields)
				{
					data[posFloorName] = etage_choisi;
				}
				if (posBuildingName < numberOfFields)
				{
					data[posBuildingName] = BatimentChoisi;
				}
				if (posRoomType < numberOfFields)
				{
					data[posRoomType] = type_salle_choisi;
				}
				if (posTimeOpening < numberOfFields)
				{
					data[posTimeOpening] = hourOp;
				}
				if (posTimeClosing < numberOfFields)
				{
					data[posTimeClosing] = hourCl;
				}
				
				String formalizedDataSent = "";
				for(i = 0; i < numberOfFields; i++)
				{
					formalizedDataSent += data[i] + ( (i == numberOfFields - 1) ? "":separator);
				}
						mActivity.onEditorActionGL(formalizedDataSent);
//						Log.d(this.getClass().getName(), formalizedDataSent);
						dismiss();

			}

		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// mActivity.onEditorActionGL("PENSEZ A ANNULER LE PIN PAS VALIDE");
				dismiss();
			}
		});

	}

	public String AlertDialogBuild(final SpinnerData dataspin) {

		// setContentView(R.layout.alertdialog_ajout_batiment);

		// On instancie notre layout en tant que View
		LayoutInflater factory = LayoutInflater.from(mcontext);
		final View alertDialogView = factory.inflate(
				R.layout.alertdialog_ajout_batiment, null);
		// Création de l'AlertDialog
		AlertDialog.Builder adb = new AlertDialog.Builder(mcontext);

		// On affecte la vue personnalisé que l'on a crée à notre AlertDialog
		adb.setView(alertDialogView);

		// On donne un titre à l'AlertDialog
		adb.setTitle("Ajout d'un nouveau batiment");

		// On modifie l'icône de l'AlertDialog pour le fun ;)
		adb.setIcon(android.R.drawable.ic_dialog_alert);

		// On affecte un bouton "OK" à notre AlertDialog et on lui affecte un
		// évènement
		adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				// Lorsque l'on cliquera sur le bouton "OK", on récupère
				// l'EditText correspondant à notre vue personnalisée (cad à
				// alertDialogView)
				EditText et = (EditText) alertDialogView
						.findViewById(R.id.EditText1);
				new_building = et.getText().toString();
				SpinnerData dataspin = new SpinnerData();
				if (!dataspin.getBuildingName().equals(new_building)) {
					dataspin.setBuildingName(new_building);
					SpinnerDataRemoteHome.setSpinnerData(dataspin);
					// On affiche dans un Toast le texte contenu dans l'EditText
					// de notre AlertDialog
					Toast.makeText(mcontext, new_building, Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		// On crée un bouton "Annuler" à notre AlertDialog et on lui affecte un
		// évènement
		adb.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Lorsque l'on cliquera sur annuler on quittera l'application
				// dismiss();
			}
		});
		adb.show();
		return new_building;
	}

	String res;

	public String AlertDialogBuild(String message) {

		AlertDialog.Builder adb = new AlertDialog.Builder(mcontext);
		// on attribut un titre à notre boite de dialogue
		adb.setTitle("Correction Heure " + message);
		// on insère un message à notre boite de dialogue, et ici on
		// affiche le titre de l'item cliqué
		adb.setMessage("Merci de corriger l'heure " + message
				+ "\n Format Heure: 08:30");

		// on indique que l'on veut le bouton ok à notre boite de
		// dialogue
		adb.setPositiveButton("Ok", null);
		// on affiche la boite de dialogue
		adb.show();
		// final EditText input = new EditText(mcontext);
		// new AlertDialog.Builder(mcontext)
		// .setTitle("Update Status")
		// .setMessage("Merci de corriger l'heure! "+ message +
		// "\n Format Heure: 08H30")
		// .setView(input)
		// .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// Editable value = input.getText();
		// res = value.toString();
		// }
		// }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		// {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// // Do nothing.
		// }
		// }).show();
		return res;
	}

	public static String getSeparator() {
		return separator;
	}

	public static int getNumberOfFields() {
		return numberOfFields;
	}

	public static int getPosRoomName() {
		return posRoomName;
	}

	public static int getPosFloorName() {
		return posFloorName;
	}

	public static int getPosBuildingName() {
		return posBuildingName;
	}

	public static int getPosRoomType() {
		return posRoomType;
	}

	public static int getPosTimeOpening() {
		return posTimeOpening;
	}

	public static int getPosTimeClosing() {
		return posTimeClosing;
	}
}