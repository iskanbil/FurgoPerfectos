package com.iskanbil.furgoperfectos;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SearchViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.iskanbil.database.FPDatabaseApi;
import com.iskanbil.database.FPDatabaseUtils.FurgoPerfectos;

public class ListaActivity extends
		com.actionbarsherlock.app.SherlockFragmentActivity implements
		TabListener {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private Cursor m_cursor1;
	private Cursor m_cursor2;
	private CursorAdapter m_adapter1;
	private CursorAdapter m_adapter2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Context context = getSupportActionBar().getThemedContext();
		View searchView = SearchViewCompat.newSearchView(context);
		if (searchView != null) {
			Toast.makeText(this, "native", Toast.LENGTH_SHORT).show();
		    //Use native implementation
		} else {
		    //Use simple compatibility implementation
			Toast.makeText(this, "simple", Toast.LENGTH_SHORT).show();
		}
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// For each of the sections in the app, add a tab to the action bar.
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section1)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section2)
				.setTabListener(this));
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
				.getSelectedNavigationIndex());
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu.activity_lista, menu);
	// return true;
	// }
	//
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case android.R.id.home:
	// NavUtils.navigateUpFromSameTask(this);
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_lista, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends SherlockListFragment {
		public DummySectionFragment() {
		}

		public static final String ARG_SECTION_NUMBER = "section_number";

		@SuppressWarnings("deprecation")
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			ListView lista = (ListView) inflater
					.inflate(R.layout.fp_list, null);

			return lista;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// When the given tab is selected, show the tab contents in the
		// container
		DummySectionFragment fragment = new DummySectionFragment();
		FPDatabaseApi api = new FPDatabaseApi(this);
		if (tab.getPosition() == 0 && m_cursor1 == null) {
			m_cursor1 = api.getFurgoPerfectos(FurgoPerfectos.TIPO_FP);
			m_adapter1 = new CursorAdapter(this, m_cursor1) {

				@Override
				public View newView(Context arg0, Cursor c, ViewGroup arg2) {
					TwoLineListItem li = (TwoLineListItem) LayoutInflater.from(
							ListaActivity.this).inflate(
							android.R.layout.simple_list_item_2, null);
					li.getText1().setText(
							c.getString(FurgoPerfectos.COLUMN_INDEX_NAME));
					li.getText2()
							.setText(
									c.getString(FurgoPerfectos.COLUMN_INDEX_DESCRIPCION));
					return li;
				}

				@Override
				public void bindView(View view, Context context, Cursor c) {
					// TODO Auto-generated method stub
					TwoLineListItem li = (TwoLineListItem) view;
					li.getText1().setText(
							c.getString(FurgoPerfectos.COLUMN_INDEX_NAME));
					li.getText2()
							.setText(
									c.getString(FurgoPerfectos.COLUMN_INDEX_DESCRIPCION));

				}
			};
		}
		if (tab.getPosition() == 1 && m_cursor2 == null) {
			m_cursor2 = api.getFurgoPerfectos(FurgoPerfectos.TIPO_AC);
			m_adapter2 = new CursorAdapter(this, m_cursor2) {

				@Override
				public View newView(Context arg0, Cursor c, ViewGroup arg2) {
					TwoLineListItem li = (TwoLineListItem) LayoutInflater.from(
							ListaActivity.this).inflate(
							android.R.layout.simple_list_item_2, null);
					li.getText1().setText(
							c.getString(FurgoPerfectos.COLUMN_INDEX_NAME));
					li.getText2()
							.setText(
									c.getString(FurgoPerfectos.COLUMN_INDEX_DESCRIPCION));
					return li;
				}

				@Override
				public void bindView(View view, Context context, Cursor c) {
					TwoLineListItem li = (TwoLineListItem) view;
					li.getText1().setText(
							c.getString(FurgoPerfectos.COLUMN_INDEX_NAME));
					li.getText2()
							.setText(
									c.getString(FurgoPerfectos.COLUMN_INDEX_DESCRIPCION));

				}
			};
		}

		fragment.setListAdapter(tab.getPosition() == 0 ? m_adapter1
				: m_adapter2);

		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER,
				tab.getPosition() + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (m_cursor1!=null && !m_cursor1.isClosed())
			m_cursor1.close();
		if (m_cursor2!=null && !m_cursor2.isClosed())
			m_cursor2.close();
	}

}
