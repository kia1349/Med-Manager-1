package com.nwagu.medmanager;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.SearchManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nwagu.medmanager.PillsLedger.PillDetails;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.graphics.Color.argb;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ServiceCallBacks {

    //LinearLayout mainView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ExpandableHeightGridView pillsGridView;
    Menu mainMenu;
    TextView footnoteText;

    FloatingActionButton fab;

    public PillsLedger pillsLedger;

    MediaPlayer pillDueSound;

    private AlarmService alarmService;
    private boolean bound = false;

    SQLiteDatabase sqlDatabase;
    MedDbHelper dbHelper;

    SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {Thread.sleep(500);} catch (InterruptedException ignored) {} //give time for user to enjoy splash screen:)
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        Fresco.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serverIntent = new Intent(MainActivity.this, NewMedActivity.class);
                startActivityForResult(serverIntent, Constants.REQUEST_NEW_MED);
            }
        });

        //-----------------------------------------

        pillsLedger = new PillsLedger();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        pillsGridView = (ExpandableHeightGridView) findViewById(R.id.pills_grid_view);
        footnoteText = (TextView) findViewById(R.id.foot_note);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, 1);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO refresh action
                readMedData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        dbHelper = new MedDbHelper(this);
        sqlDatabase = dbHelper.getWritableDatabase();

        pillDueSound = MediaPlayer.create(getApplicationContext(), R.raw.pill_due);

        settings = getSharedPreferences("mMedData", MODE_PRIVATE);

        if(!settings.contains("fircheck")) {
            //TODO Do something at first launch of app

            SharedPreferences.Editor editor = settings.edit(); editor.putInt("fircheck", 1); editor.apply();
        }

        readMedData();

    }

    private static class ViewHolder {
        private LinearLayout backLayout;
        private TextView deviceTextView, descTextView;
        private ImageButton dueImageButton;
        private CardView mainCard;
    }


    private class MyAdapter extends BaseAdapter {
        ArrayList<PillDetails> medList;

        private MyAdapter(ArrayList<PillDetails> ledgerArrayInstance) {
            medList = ledgerArrayInstance;
        }

        public int getCount() {
            return medList.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        @TargetApi(16)
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.grid_view_format, parent, false);
                holder = new ViewHolder();
                holder.backLayout = (LinearLayout) convertView.findViewById(R.id.back_layout);
                holder.deviceTextView = (TextView) convertView.findViewById(R.id.device_name);
                holder.dueImageButton = (ImageButton) convertView.findViewById(R.id.due_button);
                holder.descTextView = (TextView) convertView.findViewById(R.id.desc_view);
                holder.mainCard = (CardView) convertView.findViewById(R.id.main_card);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            final PillDetails pill = medList.get(position);

            final int pillIndex = pill.getIndex();
            final String pillName = pill.getPillName();
            final String pillDesc = pill.getDescription();
            final String pillFreq = pill.getInterval();
            final String pillStart = pill.getStartDate();
            final String pillEnd = pill.getEndDate();
            final String pillLast = pill.getLastDate();
            final boolean pillDue = pill.getDue();

            holder.deviceTextView.setText(pillName);

            if(pillDue) {
                //try {holder.backLayout.setBackground(getDrawable(R.drawable.due_selector_up));} catch (Exception e) {}
                holder.mainCard.setCardBackgroundColor(argb(144, 187, 232, 207));
                holder.dueImageButton.setImageResource(R.drawable.time_on);
            } else {
                try {holder.backLayout.setBackground(getDrawable(R.drawable.due_selector_down));} catch (Exception ignored) {}
                //holder.mainCard.setCardBackgroundColor(argb(32, 187, 232, 207));
                //holder.dueImageButton.setImageResource(R.drawable.time_off);
            }

            holder.deviceTextView.setTextColor(getResources()
                    .getColor(getResId("pillTextColour" + (pillIndex % Constants.PILL_DISPLAY_COLOURS), R.color.class)));
            holder.descTextView.setText(pillDesc);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.putExtra(Constants.EXTRA_MED_INDEX, pillIndex);
                    intent.putExtra(Constants.EXTRA_MED_NAME, pillName);
                    intent.putExtra(Constants.EXTRA_MED_DESC, pillDesc);
                    intent.putExtra(Constants.EXTRA_MED_FREQ, pillFreq);
                    intent.putExtra(Constants.EXTRA_MED_START, pillStart);
                    intent.putExtra(Constants.EXTRA_MED_END, pillEnd);
                    intent.putExtra(Constants.EXTRA_MED_LAST, pillLast);
                    startActivityForResult(intent, Constants.REQUEST_PILL_EDIT);
                    //overridePendingTransition(R.anim.slide_in_right, R.anim.no_anim);
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //TODO unset pill-due notification
                    /*int switchToInt;
                    if(pillDue) switchToInt = 0; else switchToInt = 1;
                    switchAction(portIndex, switchToInt);*/
                    return true;
                }
            });

            return convertView;

        }
    }

    //method to update UI from content of database
    public void readMedData() {

        pillsLedger = new PillsLedger();

        //get pills from database ordered by ID
        Cursor c1 = sqlDatabase.query(MedContract.PillsEntry.TABLE_NAME, null, null, null, null, null,
                MedContract.PillsEntry._ID);
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    pillsLedger.add(c1.getInt(c1.getColumnIndex(MedContract.PillsEntry._ID)),
                            c1.getString(c1.getColumnIndex(MedContract.PillsEntry.COLUMN_PILL_NAME)),
                            c1.getString(c1.getColumnIndex(MedContract.PillsEntry.COLUMN_PILL_DESC)),
                            c1.getString(c1.getColumnIndex(MedContract.PillsEntry.COLUMN_PILL_INTERVAL)),
                            c1.getString(c1.getColumnIndex(MedContract.PillsEntry.COLUMN_PILL_START)),
                            c1.getString(c1.getColumnIndex(MedContract.PillsEntry.COLUMN_PILL_END)),
                            c1.getString(c1.getColumnIndex(MedContract.PillsEntry.COLUMN_PILL_LAST))
                            );
                    //SimpleDateFormat format = new SimpleDateFormat("d-M-yyyy hh:mm");
                    SimpleDateFormat format = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    long intervalInInt = Integer.parseInt(pillsLedger.pillsArrayList.get(pillsLedger.pillsArrayList.size() - 1).getInterval());
                    try {
                        Date date = format.parse(pillsLedger.pillsArrayList.get(pillsLedger.pillsArrayList.size() - 1).getLastDate());
                        long lastDateInMillis = date.getTime();
                        if((lastDateInMillis + intervalInInt * 3600000) < cal.getTimeInMillis())
                            pillsLedger.pillsArrayList.get(pillsLedger.pillsArrayList.size() - 1).setDue(true);
                        else pillsLedger.pillsArrayList.get(pillsLedger.pillsArrayList.size() - 1).setDue(false);
                    } catch (ParseException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }


                } while (c1.moveToNext());
            }
        }
        try { c1.close();} catch (NullPointerException ignored) {}

        pillsGridView.setAdapter(new MyAdapter(pillsLedger.pillsArrayList));

        if(pillsLedger.pillsArrayList.size() == 0) footnoteText.setText(R.string.no_active_med);
        else footnoteText.setText(null);

    }


    public void setAlarm(int index) {
        String duePillName = " ";
        int duePillFreq = 1;
        String[] arg = new String[] {Integer.toString(index)};
        Cursor c = sqlDatabase.query(MedContract.PillsEntry.TABLE_NAME, null,
                MedContract.PillsEntry._ID + "=?", arg, null, null, null);
        if (c != null && c.getCount() != 0) {
            if (c.moveToFirst()) {
                duePillName = c.getString(c.getColumnIndex(MedContract.PillsEntry.COLUMN_PILL_NAME));
                duePillFreq = Integer.parseInt(c.getString(c.getColumnIndex(MedContract.PillsEntry.COLUMN_PILL_INTERVAL)));
            }
        }
        try { c.close();} catch (NullPointerException ignored) {}

        Intent intent = new Intent(this, AlarmService.class);
        intent.setAction(duePillName);
        intent.putExtra(Constants.DUE_PILL_NAME, duePillName);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + (duePillFreq * 3600000), pendingIntent);

    }

    public void setAlarm(String duePillName, int duePillFreq) {
        Intent intent = new Intent(this, AlarmService.class);
        intent.setAction(duePillName);
        intent.putExtra(Constants.DUE_PILL_NAME, duePillName);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + (duePillFreq * 3600000), pendingIntent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    }

    private void showResults(String query) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //bind to service
        Intent intent = new Intent(this, AlarmService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        readMedData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unbind from service
        if(bound) {
            alarmService.setCallBacks(null); //unregister
            unbindService(serviceConnection);
            bound = false;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem mMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mMenuItem);
        searchView.setQueryHint("Search");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pillsLedger = new PillsLedger();
                Cursor c2 = sqlDatabase.rawQuery("SELECT * FROM "
                        + MedContract.PillsEntry.TABLE_NAME + " WHERE "
                + MedContract.PillsEntry.COLUMN_PILL_NAME + " LIKE '" + newText + "%'", null);

                if (c2 != null && c2.getCount() != 0) {
                    if (c2.moveToFirst()) {
                        do {
                            pillsLedger.add(c2.getInt(c2.getColumnIndex("_id")),
                                    c2.getString(c2.getColumnIndex(MedContract.PillsEntry.COLUMN_PILL_NAME)),
                                    c2.getString(c2.getColumnIndex(MedContract.PillsEntry.COLUMN_PILL_DESC)),
                                    c2.getString(c2.getColumnIndex(MedContract.PillsEntry.COLUMN_PILL_INTERVAL)),
                                    c2.getString(c2.getColumnIndex(MedContract.PillsEntry.COLUMN_PILL_START)),
                                    c2.getString(c2.getColumnIndex(MedContract.PillsEntry.COLUMN_PILL_END)),
                                    c2.getString(c2.getColumnIndex(MedContract.PillsEntry.COLUMN_PILL_LAST))
                            );

                        } while (c2.moveToNext());
                    }
                }
                try { c2.close();} catch (NullPointerException ignored) {}

                pillsGridView.setAdapter(new MyAdapter(pillsLedger.pillsArrayList));

                if(pillsLedger.pillsArrayList.size() == 0) footnoteText.setText(R.string.no_match_found);
                else footnoteText.setText(null);

                return false;
            }
        });

        this.mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_refresh) {
            //TODO refresh action
            //mSwipeRefreshLayout.setRefreshing(true); //to show the progress bar for refresh
            readMedData();

            return true;
        } /*else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.no_anim);
            return true;
        } */

        return super.onOptionsItemSelected(item);
    }

    //@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Snackbar.make(mSwipeRefreshLayout, "Do history", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (id == R.id.nav_settings) {
            Snackbar.make(mSwipeRefreshLayout, "Do settings", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (id == R.id.nav_share) {
            Intent i = new Intent(android.content.Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(android.content.Intent.EXTRA_TEXT, "Manage all your medication with this Med Manager!");
            startActivity(Intent.createChooser(i, "Tell people about Med Manager"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case Constants.REQUEST_NEW_MED:
                if (resultCode == Activity.RESULT_OK) {
                    String name = data.getStringExtra(Constants.EXTRA_MED_NAME);
                    String desc = data.getStringExtra(Constants.EXTRA_MED_DESC);
                    String freq = data.getStringExtra(Constants.EXTRA_MED_FREQ);
                    String start = data.getStringExtra(Constants.EXTRA_MED_START);
                    String end = data.getStringExtra(Constants.EXTRA_MED_END);
                    dbHelper.addNewPill(sqlDatabase, name, desc, freq, start, end);
                    readMedData();

                    //set reminder
                    setAlarm(name, Integer.parseInt(freq));
                }

                break;

            case Constants.REQUEST_PILL_EDIT:
                if (resultCode == Constants.RESULT_MED_DELETE) {
                    int index = data.getIntExtra(Constants.EXTRA_MED_INDEX, 1);
                    String[] whereArgs = new String[] {Integer.toString(index)};

                    sqlDatabase.delete(MedContract.PillsEntry.TABLE_NAME, MedContract.PillsEntry._ID + "=?", whereArgs);
                    readMedData();

                }
                else if (resultCode == Constants.RESULT_MED_TAKE) {
                    int index = data.getIntExtra(Constants.EXTRA_MED_INDEX, 1);
                    String[] whereArgs = new String[] {"" + index};

                    //SimpleDateFormat format = new SimpleDateFormat("d-M-yyyy hh:mm", Locale.US);
                    SimpleDateFormat format = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(System.currentTimeMillis());

                    ContentValues cv = new ContentValues();
                    cv.put(MedContract.PillsEntry.COLUMN_PILL_LAST, format.format(c.getTime()));
                    sqlDatabase.update(MedContract.PillsEntry.TABLE_NAME, cv, MedContract.PillsEntry._ID + "=?", whereArgs);
                    readMedData();

                    //set reminder
                    setAlarm(index);
                }

                break;

        }


    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            return -1;
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AlarmService.LocalBinder binder = (AlarmService.LocalBinder) service;
            alarmService = binder.getService();
            bound = true;
            alarmService.setCallBacks(MainActivity.this); //register
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    public void updateUI() {
        readMedData();
        pillDueSound.setVolume(1.0f, 1.0f);
        pillDueSound.start();
    }

}