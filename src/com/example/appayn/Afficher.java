package com.example.appayn;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.adapter.PointInteretPrive;
import com.example.business.DbManager;

public class Afficher extends Activity {
	DbManager dbb;
	ListView listview;
	ListView listeViewEnseigne;
	//tableau qui contients tous les nom des points priv�
	String arr[];
	String arr2[];
	Double arrLat[];
	Double arrLong[];
	String name;
	Context context;
	int textlength = 0;
	EditText nom;
	Button chercher;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.afficher);

		// boutton fermer
		fermer();

		// instance de DbManager
		dbb = new DbManager(this);

		// id du champ a remplir
		nom = (EditText) findViewById(R.id.TextView1);

		// recuperer tous les points priv�
		recuperer_la_liste_des_points_prive();

		// ajouter un listener au champs � remplir
		nom.addTextChangedListener(new TextWatcher() {
			// apres le remplissage du champ retourner la valeur a MainActivity
			@Override
			public void afterTextChanged(final Editable s) {

				final Intent intent = getIntent();
				// Log.v("test", "name   :  " + name);
				intent.putExtra("returnedData", name);
				setResult(RESULT_OK, intent);

			}
            
			@Override
			public void beforeTextChanged(final CharSequence s,
					final int start, final int count, final int after) {

			}

			@Override
			@SuppressWarnings("unchecked")
			public void onTextChanged(final CharSequence s, final int start,
					final int before, final int count) {

				textlength = nom.getText().length();
				@SuppressWarnings("rawtypes")
				final ArrayList text_sort = new ArrayList();
				text_sort.clear();

				for (int i = 0; i < arr.length; i++) {
					if (textlength <= arr[i].length()) {
						if (nom.getText()
								.toString()
								.equalsIgnoreCase(
										(String) arr[i].subSequence(0,
												textlength))) {
							text_sort.add(arr[i]);

						}
					}
				}

				arr2 = new String[text_sort.size()];
				for (int j = 0; j < text_sort.size(); j++) {
					arr2[j] = text_sort.get(j).toString();
					//Log.v("test","arr2     :"+arr2[j]);
				}

				listeViewEnseigne.setAdapter(new MyCustomAdapter(text_sort));

			}
		});

		listeViewEnseigne = (ListView) findViewById(R.id.ListView01);

		// listeViewEnseigne.setAdapter(new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, arr));
		addOnClickListener();
		chercher = (Button) findViewById(R.id.chercher);
		chercher.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				// hide keyboard
				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						((EditText) findViewById(R.id.TextView1))
								.getWindowToken(), 0);
				final EditText poi = (EditText) findViewById(R.id.TextView1);
				name = ((EditText) findViewById(R.id.TextView1)).getText()
						.toString();
				if (name.compareTo("") == 0) {
					poi.setError("Champ vide");
				} else {
					poi.setError(null);
					final Intent intent = getIntent();
					// Log.v("test", "name   :  " + name);
					intent.putExtra("returnedData", name);
					setResult(RESULT_OK, intent);
					finish();
				}

			}
		});
	}

	public void recuperer_la_liste_des_points_prive() {
		final List<PointInteretPrive> PointPrive = dbb.GetAll();
		// int nb = 0;
		arr = new String[PointPrive.size()];
		for (int i = 0; i < PointPrive.size(); i++) {
			arr[i] = PointPrive.get(i).getNom();
			// arrLat[i]=PointPrive.get(i).getLatitude();
			// arrLong[i]=PointPrive.get(i).getLongitude();
			// Log.v("test",
			// "                  PointPrive.get(i).getLongitude();  " +
			// PointPrive.get(i).getLongitude());
			// Log.v("test",
			// "                  PointPrive.get(i).getLatitude();  " +
			// PointPrive.get(i).getLatitude());
			// new LatLng(PointPrive.get(i).getLatitude(),
			// PointPrive.get(i).getLongitude());
			PointPrive.get(i).getLatitude();

			Log.v("test", "les noms qui existent dans la base priv� sont:   "
					+ arr[i]);
			// nb++;
		}
	}

	public void fermer() {
		final Button close = (Button) findViewById(R.id.btnquitter);
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				finish();
			}
		});
	}

	// Evenement OnClick de la ligne
	private void addOnClickListener() {
		final ListView listeViewEnseigne = (ListView) findViewById(R.id.ListView01);
		listeViewEnseigne.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> arg0, final View view,
					final int arg2, final long itemID) {

				name = arr2[(int) itemID];

				nom.setText(name);
				finish();

			}
		});
	}

	class MyCustomAdapter extends BaseAdapter {

		String[] data_text;
		int[] data_image;

		MyCustomAdapter() {

		}

		MyCustomAdapter(final String[] text) {
			data_text = text;

		}

		MyCustomAdapter(final ArrayList text) {

			data_text = new String[text.size()];

			for (int i = 0; i < text.size(); i++) {
				data_text[i] = (String) text.get(i);
			}

		}

		@Override
		public int getCount() {
			return data_text.length;
		}

		@Override
		public String getItem(final int position) {
			return null;
		}

		@Override
		public long getItemId(final int position) {
			return position;
		}

		@Override
		public View getView(final int position, final View convertView,
				final ViewGroup parent) {

			final LayoutInflater inflater = getLayoutInflater();
			View row;

			row = inflater.inflate(R.layout.listview, parent, false);

			final TextView textview = (TextView) row
					.findViewById(R.id.TextView01);

			textview.setText(data_text[position]);

			return row;

		}
	}

}
