package stelztech.youknowehv4.fragments.statistics;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.MainActivityManager;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.fragments.FragmentCommon;
import stelztech.youknowehv4.manager.FloatingActionButtonManager;
import stelztech.youknowehv4.manager.ThemeManager;

/**
 * Created by alex on 2017-04-03.
 */

public class StatisticsFragment extends FragmentCommon {

    View view;

    private Calendar startCalendar = null;
    private Calendar endCalendar = null;

    private List<BarEntry> entries = new ArrayList<BarEntry>();
    private BarChart chart;

    private StatsMultiSelectionSpinner decksSpinner;
    private Spinner filterSpinner;

    private List<Deck> deckList;


    public StatisticsFragment(int animationLayoutPosition, boolean animationFade) {
        super(animationLayoutPosition, animationFade);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_statistics, container, false);
        FloatingActionButtonManager.getInstance().setState(FloatingActionButtonManager.ActionButtonState.GONE, getActivity());


        if (startCalendar == null) {
            startCalendar = Calendar.getInstance();
            startCalendar.add(Calendar.DAY_OF_MONTH, -7);
        }
        if (endCalendar == null) {
            endCalendar = Calendar.getInstance();
        }

        initDeckList();
        initFilterSpinner();
        initDecksSpinner();
        initStartEndTextViews();
        initChart();
        return view;
    }

    private void initDeckList() {
        deckList = Database.mDeckDao.fetchAllDecks();
    }

    private void initFilterSpinner() {
        filterSpinner = (Spinner) view.findViewById(R.id.fragment_statistics_filter_spinner);
        String[] items = new String[]{"Cards Added", "Total of Cards"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_textview_align, items);
        adapter.setDropDownViewResource(R.layout.spinner_textview_align);
        filterSpinner.setAdapter(adapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateEntries();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void initDecksSpinner() {
        decksSpinner = (StatsMultiSelectionSpinner) view.findViewById(R.id.fragment_statistics_decks_spinner);
        decksSpinner.setStatisticsFragment(this);

        List<String> list = new ArrayList<String>();
        list.add("Select all");
        for (int counter = 0; counter < deckList.size(); counter++) {
            list.add(deckList.get(counter).getDeckName());
        }
        if (!list.isEmpty()) {
            decksSpinner.setItems(list);
        } else {
            decksSpinner.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    decksSpinner.setClickable(false);
                    Toast.makeText(getActivity(),
                            "spinner have no items", Toast.LENGTH_SHORT).show();

                    return false;
                }
            });
        }

        decksSpinner.setAdapterTitle();

    }


    private void initStartEndTextViews() {


        final TextView endDateTextView = (TextView) view.findViewById(R.id.fragment_statistics_end_date);
        final TextView startDateTextView = (TextView) view.findViewById(R.id.fragment_statistics_start_date);


        final DatePickerDialog.OnDateSetListener startDatePickerDialog = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                startCalendar.set(Calendar.YEAR, year);
                startCalendar.set(Calendar.MONTH, monthOfYear);
                startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
                populateEntries();
            }

            private void updateLabel() {
                String myFormat = "dd MMM, yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                startDateTextView.setText(sdf.format(startCalendar.getTime()));

                if (startCalendar.after(endCalendar)) {
                    endCalendar.set(Calendar.YEAR, startCalendar.get(Calendar.YEAR));
                    endCalendar.set(Calendar.MONTH, startCalendar.get(Calendar.MONTH));
                    endCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.get(Calendar.DAY_OF_MONTH));
                    endDateTextView.setText(sdf.format(endCalendar.getTime()));
                }

            }

        };

        startDateTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), startDatePickerDialog, startCalendar
                        .get(Calendar.YEAR), startCalendar.get(Calendar.MONTH),
                        startCalendar.get(Calendar.DAY_OF_MONTH));
                Calendar minCalendarDate = (Calendar.getInstance());
                minCalendarDate.set(Calendar.YEAR, DayAxisValueFormatter.START_YEAR);
                minCalendarDate.set(Calendar.MONTH, 0);
                minCalendarDate.set(Calendar.DAY_OF_MONTH, 1);
                datePickerDialog.getDatePicker().setMinDate(minCalendarDate.getTime().getTime());
                datePickerDialog.show();
            }
        });

        final DatePickerDialog.OnDateSetListener endDatePickerDialog = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, monthOfYear);
                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
                populateEntries();
            }

            private void updateLabel() {
                String myFormat = "dd MMM, yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                endDateTextView.setText(sdf.format(endCalendar.getTime()));
                if (endCalendar.before(startCalendar)) {
                    startCalendar.set(Calendar.YEAR, endCalendar.get(Calendar.YEAR));
                    startCalendar.set(Calendar.MONTH, endCalendar.get(Calendar.MONTH));
                    startCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH));
                    startDateTextView.setText(sdf.format(startCalendar.getTime()));
                }

            }

        };

        endDateTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), endDatePickerDialog, endCalendar
                        .get(Calendar.YEAR), endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH));
                Calendar minCalendarDate = (Calendar.getInstance());
                minCalendarDate.set(Calendar.YEAR, DayAxisValueFormatter.START_YEAR);
                minCalendarDate.set(Calendar.MONTH, 0);
                minCalendarDate.set(Calendar.DAY_OF_MONTH, 1);
                datePickerDialog.getDatePicker().setMinDate(minCalendarDate.getTime().getTime());
                datePickerDialog.show();
            }
        });

        String myFormat = "dd MMM, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        startDateTextView.setText(sdf.format(startCalendar.getTime()));
        endDateTextView.setText(sdf.format(endCalendar.getTime()));

    }


    private void initChart() {

        chart = (BarChart) view.findViewById(R.id.chart);


        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(1);
        xAxis.setValueFormatter(xAxisFormatter);

        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();

        chart.getDescription().setEnabled(false);

        leftAxis.setAxisMinimum(0);
        rightAxis.setAxisMinimum(0);


        populateEntries();
    }

    private void populateEntries() {


        entries.clear();

        int daysStart = getDaysSinceChartFirstDate(startCalendar.getTime());
        int daysEnd = getDaysSinceChartFirstDate(endCalendar.getTime());

        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        int numberOfEntries = 0;
        for (int daysIndex = daysStart; daysIndex <= daysEnd; daysIndex++) {
            Calendar tempCal = Calendar.getInstance();
            tempCal.set(Calendar.YEAR, DayAxisValueFormatter.START_YEAR);
            tempCal.set(Calendar.MONTH, Calendar.JANUARY);
            tempCal.set(Calendar.DAY_OF_MONTH, 1);
            tempCal.add(Calendar.DAY_OF_YEAR, daysIndex);


            List<Integer> selection = decksSpinner.getSelectedIndicies();
            if (filterSpinner.getSelectedItemPosition() == 0) // cards added reset counter
                numberOfEntries = 0;
            for (int i = 1; i < selection.size(); i++) {
                int selectionIndex = selection.get(i);
                numberOfEntries += Database.mCardDeckDao.fetchNumberOfCardsCreatedOnDateByDeckId(deckList.get(selectionIndex - 1).getDeckId(), sdf.format(tempCal.getTime()));
            }

            if (numberOfEntries > 0)
                entries.add(new BarEntry(daysIndex, numberOfEntries));

        }


        BarDataSet dataSet = new BarDataSet(entries, "Label"); // add entries to dataset
        BarData lineData = new BarData(dataSet);
        dataSet.setColor(ThemeManager.getInstance().getThemePrimaryColor(getContext(), Database.mUserDao.fetchActiveProfile().getProfileColor()));
        dataSet.setValueFormatter(new CustomValueFormatter());
        dataSet.setHighlightEnabled(false);
        lineData.setBarWidth(0.9f);
        chart.setData(lineData);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.setScaleYEnabled(false);
        chart.invalidate();
    }

    private int getDaysSinceChartFirstDate(Date date) {
        Calendar minCalendarDate = (Calendar.getInstance());
        minCalendarDate.set(Calendar.YEAR, DayAxisValueFormatter.START_YEAR);
        minCalendarDate.set(Calendar.MONTH, 0);
        minCalendarDate.set(Calendar.DAY_OF_MONTH, 1);

        return (int) ((date.getTime() - minCalendarDate.getTime().getTime()) / (1000 * 60 * 60 * 24)) + 1;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar_other, menu);
        ActionBar actionBar = ((MainActivityManager) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        getActivity().findViewById(R.id.spinner_nav_layout).setVisibility(View.GONE);

    }

    public void onDeckSpinnerPositiveButtonClicked() {
        populateEntries();
    }

}
