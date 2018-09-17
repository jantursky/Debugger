package com.jantursky.debugger.dbviewer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jantursky.debugger.R;
import com.jantursky.debugger.dbviewer.adapters.DbViewerGridRowAdapter;
import com.jantursky.debugger.dbviewer.adapters.DbViewerListAdapter;
import com.jantursky.debugger.dbviewer.annotations.DbViewerScreenType;
import com.jantursky.debugger.dbviewer.annotations.DbViewerSortType;
import com.jantursky.debugger.dbviewer.comparators.DbGridComparator;
import com.jantursky.debugger.dbviewer.helpers.DBViewerHelper;
import com.jantursky.debugger.dbviewer.listeners.DbViewerGridItemListener;
import com.jantursky.debugger.dbviewer.listeners.DbViewerListItemListener;
import com.jantursky.debugger.dbviewer.models.DbViewerDataModel;
import com.jantursky.debugger.dbviewer.models.DbViewerRecyclerViewModel;
import com.jantursky.debugger.interfaces.GlobalViewListener;
import com.jantursky.debugger.listeners.RepeatListener;
import com.jantursky.debugger.utils.ViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DbGridView extends FrameLayout implements View.OnClickListener, DbViewerListItemListener, DbViewerGridItemListener {

    private static final String TAG = DbGridView.class.getSimpleName();

    private static final float ALPHA_DISABLED = 0.4F;
    private static final float ALPHA_ENABLED = 1.0F;

    private LinearLayout gridCntLayout;
    private View listLayout, dataLayout;
    private TextView pageTxt, pageLeftTxt, pageRightTxt, closeTxt, deleteTxt, addTxt;

    private DBViewerHelper dbHelper;
    private @DbViewerScreenType
    int screenType = DbViewerScreenType.Tables;

    private DbViewerListAdapter dbViewerListAdapter;

    private RecyclerView recyclerViewList;

    private float rowHeight;

    private int count = 0;
    private int currentPage = 1;
    private int totalPage = 1;
    private int maxPerPage = 0;
    private int scrollBy = 0;
    private String selectedTable;
    private ArrayList<DbViewerDataModel> headerRow;
    private ArrayList<DbViewerRecyclerViewModel> recyclerViews;
    final RecyclerView.OnScrollListener listener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            scrollBy += dx;
            if (scrollBy < 0) {
                scrollBy = 0;
            }
            scrollToX((Integer) recyclerView.getTag(), dx);
        }
    };
    private ArrayList<ArrayList<DbViewerDataModel>> data;
    private DbGridComparator comparator = new DbGridComparator();
    private int lastHeaderPos = -1;

    public DbGridView(@NonNull Context context) {
        super(context);
        init();
    }

    public DbGridView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DbGridView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DbGridView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_db_grid, this, true);

        gridCntLayout = view.findViewById(R.id.grid_cnt_layout);

        listLayout = view.findViewById(R.id.list_layout);
        dataLayout = view.findViewById(R.id.data_layout);

        recyclerViewList = view.findViewById(R.id.list_recycler_view);

        pageTxt = view.findViewById(R.id.page_textview);
        pageLeftTxt = view.findViewById(R.id.page_left_textview);
        pageRightTxt = view.findViewById(R.id.page_right_textview);
        deleteTxt = view.findViewById(R.id.delete_textview);
        closeTxt = view.findViewById(R.id.close_textview);
        addTxt = view.findViewById(R.id.add_textview);

        rowHeight = getContext().getResources().getDimension(R.dimen.db_viewer_row_height);

        setTablesList();

        ViewUtils.addListener(gridCntLayout, new GlobalViewListener() {
            @Override
            public void onGlobalLayout(int width, int height) {
                maxPerPage = (int) Math.floor((float) (height) / rowHeight);

                /*File dbFile = new File("/data/data/" + getContext().getPackageName() + "/databases/" + DatabaseUtil.DATABASE_NAME);
                onDatabaseClick(dbFile);*/
                displayDatabases();
            }
        });

        setListeners();
    }

    private void setTablesList() {
        dbViewerListAdapter = new DbViewerListAdapter();
        recyclerViewList.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerViewList.setAdapter(dbViewerListAdapter);
        dbViewerListAdapter.setOnItemClickListener(this);
    }

    private void displayDatabases() {
        screenType = DbViewerScreenType.Databases;
        applyView();
        dbViewerListAdapter.setDatabases(getDbFiles());
    }

    public void displayTables() {
        screenType = DbViewerScreenType.Tables;
        applyView();
        dbViewerListAdapter.setTables(getTables());
    }

    private ArrayList<File> getDbFiles() {
        File root = new File("/data/data/" + getContext().getPackageName() + "/databases");
        ArrayList<File> files = new ArrayList<>();
        if (root.isDirectory()) {
            files.addAll(Arrays.asList(root.listFiles()));
        }
        return files;
    }

    private ArrayList<String> getTables() {
        return dbHelper.getAllTables();
    }

    private void applyView() {
        if (screenType == DbViewerScreenType.Databases || screenType == DbViewerScreenType.Tables) {
            listLayout.setVisibility(View.VISIBLE);
            dataLayout.setVisibility(View.GONE);
        } else if (screenType == DbViewerScreenType.Data) {
            listLayout.setVisibility(View.GONE);
            dataLayout.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        pageTxt.setOnClickListener(this);
        /*pageLeftTxt.setOnClickListener(this);
        pageRightTxt.setOnClickListener(this);*/
        closeTxt.setOnClickListener(this);
        deleteTxt.setOnClickListener(this);

        pageLeftTxt.setOnTouchListener(new RepeatListener(400, 100, true, new OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToPage(-1);
            }
        }));

        pageRightTxt.setOnTouchListener(new RepeatListener(400, 100, true, new OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToPage(+1);
            }
        }));
//        addTxt.setOnClickListener(this);
    }

    private void selectDb(File dbFile) {
        DBViewerHelper.resetInstance();
        dbHelper = DBViewerHelper.getInstance(getContext(), dbFile);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.page_textview) {
            displayJumpToPageDialog();
        }/* else if (viewId == R.id.page_left_textview) {
            jumpToPage(-1);
        } else if (viewId == R.id.page_right_textview) {
            jumpToPage(+1);
        }*/ else if (viewId == R.id.delete_textview) {
            /*if (dbViewerListener != null) {
                dbViewerListener.deleteDatabase(selectedTable);
            }*/
            yesNoDialog("Delete the table?", "Yes", "No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        deleteTable(selectedTable);
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {

                    }
                }
            });
        } else if (viewId == R.id.close_textview) {
            canGoBack();
        } else if (viewId == R.id.add_textview) {
            addRow();
        }
    }

    private void yesNoDialog(String message, String yes, String no, DialogInterface.OnClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        if (yes != null) {
            builder.setPositiveButton(yes, dialogClickListener);
        }
        if (no != null) {
            builder.setNegativeButton(no, dialogClickListener);
        }
        builder.show();
    }

    public void deleteTable(String table) {
        dbHelper.delete(table, null);
        displayData(table);
    }

    private void jumpToPage(int jump) {
        int current = currentPage + jump;

        if (current >= 1 && current <= totalPage) {
            currentPage = current;
            refreshGridList();
//            scrollToX(-1, scrollBy);
        }
    }

    private void refreshGridList() {
        applyRowChanges();
        setRowData();
    }

    private void applyRowChanges() {
        setBottomPages();
        checkPages();
    }

    private void checkPages() {
        if (currentPage <= 1) {
            pageLeftTxt.setAlpha(ALPHA_DISABLED);
            pageLeftTxt.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.db_viewer_bottom_bg));
        } else {
            pageLeftTxt.setAlpha(ALPHA_ENABLED);
            pageRightTxt.setBackgroundResource(R.drawable.db_viewer_icon_selector);
        }

        if (currentPage >= totalPage) {
            pageRightTxt.setAlpha(ALPHA_DISABLED);
            pageRightTxt.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.db_viewer_bottom_bg));
        } else {
            pageRightTxt.setAlpha(ALPHA_ENABLED);
            pageRightTxt.setBackgroundResource(R.drawable.db_viewer_icon_selector);
        }
    }

    private void displayJumpToPageDialog() {

    }

    private void addRow() {

    }

    @Override
    public void goBack() {
        canGoBack();
    }

    @Override
    public void onDatabaseClick(File databaseFile) {
        selectDb(databaseFile);
        displayTables();
    }

    @Override
    public void onTableClick(String table) {
        displayData(table);
    }

    private void displayData(String table) {
        screenType = DbViewerScreenType.Data;
        scrollBy = 0;
        count = 0;
        currentPage = 1;
        totalPage = 1;
        lastHeaderPos = -1;
        applyView();
        fillData(table);
    }

    private void firstListFill() {
        gridCntLayout.addView(addRecyclerRow(headerRow.size(), true, 0));
        for (int i = 0; i < maxPerPage; i++) {
            int pos = i + 1;
            gridCntLayout.addView(addRecyclerRow(headerRow.size(), false, pos));
        }
    }

    private void fillData(String table) {
        selectedTable = table;

        String[] tableColumns = dbHelper.getAllColumns(table);

        headerRow = dbHelper.getColumnsType(table);
        count = dbHelper.getCount(table);
        totalPage = (int) Math.ceil(count / maxPerPage);
        if ((maxPerPage * totalPage) < count) {
            totalPage++;
        }
//        Log.w(TAG, "##### Row " + count + " " + maxPerPage + " - " + totalPage);
        data = dbHelper.getData(table, tableColumns);

        if (recyclerViews == null) {
            recyclerViews = new ArrayList<>();
        } else {
            for (DbViewerRecyclerViewModel recyclerView : recyclerViews) {
                recyclerView.recyclerView.removeOnScrollListener(listener);
            }
        }
        recyclerViews.clear();
        gridCntLayout.removeAllViews();

        firstListFill();
        applyRowChanges();
        setRowData();
    }

    private void setRowData() {
        if (data != null) {
            List<ArrayList<DbViewerDataModel>> dataPerPage = getDataForPage(currentPage, maxPerPage, data);

            updateGridRow(headerRow, getColumns(), true, recyclerViews.get(0), 0);
//            if (!dataPerPage.isEmpty()) {
            for (int i = 0; i < maxPerPage; i++) {
                int pos = i + 1;
                if (!dataPerPage.isEmpty() && i < dataPerPage.size()) {
                    ArrayList<DbViewerDataModel> arrayList = dataPerPage.get(i);
                    updateGridRow(arrayList, getColumns(), false, recyclerViews.get(pos), pos);
                } else {
                    updateGridRow(null, getColumns(), false, recyclerViews.get(pos), pos);
                }
            }
//            }
        }
    }

    private int getColumns() {
        return (headerRow != null) ? headerRow.size() : 0;
    }

    private void updateGridRow(ArrayList<DbViewerDataModel> headerRow, int columns, boolean isHeader, DbViewerRecyclerViewModel dbViewerRecyclerViewModel, int pos) {
        dbViewerRecyclerViewModel.adapter.setData(headerRow, columns, isHeader, pos);
    }

    private View addRecyclerRow(int columns, boolean isHeader, int tagId) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_db_grid_recyclerview, this, false);
        RecyclerView recyclerView = view.findViewById(R.id.list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
//        recyclerView.addItemDecoration(new ItemDecorationGrid(getResources().getDimensionPixelSize(R.dimen.db_viewer_grid_row_spacing), columns));
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));

        DbViewerGridRowAdapter adapter = new DbViewerGridRowAdapter(getContext(), isHeader);
        adapter.setOnItemClickListener(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setTag(tagId);
        recyclerView.addOnScrollListener(listener);
        recyclerView.setPadding(0, 0, 0, getContext().getResources().getDimensionPixelSize(R.dimen.db_viewer_grid_row_spacing));

        recyclerViews.add(new DbViewerRecyclerViewModel(view, recyclerView, adapter));
        return view;
    }

    private void scrollToX(int tagId, int scrollX) {
        for (DbViewerRecyclerViewModel model : recyclerViews) {
            if ((Integer) model.recyclerView.getTag() != tagId) {
                model.recyclerView.removeOnScrollListener(listener);
//                Log.w(getClass().getSimpleName(), "###### Scroll " + scrollX);
                model.recyclerView.scrollBy(scrollX, 0);
                model.recyclerView.addOnScrollListener(listener);
            }
        }
    }


    private List<ArrayList<DbViewerDataModel>> getDataForPage(int currentPage, int maxPerPage, ArrayList<ArrayList<DbViewerDataModel>> data) {
        currentPage = currentPage - 1;
        maxPerPage = maxPerPage - 1;
        int from = currentPage * maxPerPage;
        int to = currentPage * maxPerPage + maxPerPage;
        if (from > data.size()) {
            from = data.size();
        }
        if (to > data.size()) {
            to = data.size();
        }
        return data.subList(from, to);
    }

    private void setBottomPages() {
        pageTxt.setText(currentPage + "/" + totalPage);
    }

    public boolean canGoBack() {
        if (screenType == DbViewerScreenType.Data) {
            displayTables();
            return false;
        } else if (screenType == DbViewerScreenType.Tables) {
            displayDatabases();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onHeaderClick(final DbViewerDataModel model) {
//        refreshGridList();
        if (data != null && !data.isEmpty()) {
            int pos = -1;
            ArrayList<DbViewerDataModel> get = data.get(0);
            for (int i = 0; i < get.size(); i++) {
                DbViewerDataModel dbModel = get.get(i);
                if (dbModel.dbKey.equals(model.dbKey)) {
                    pos = i;
                    break;
                }
            }

            if (pos != -1) {
                int sortType = comparator.getSortType();
                if (lastHeaderPos == -1 || pos != lastHeaderPos || sortType == DbViewerSortType.DISABLED) {
                    sortType = DbViewerSortType.A_Z;
                } else {
                    if (sortType == DbViewerSortType.A_Z) {
                        sortType = DbViewerSortType.Z_A;
                    } else if (sortType == DbViewerSortType.Z_A) {
                        sortType = DbViewerSortType.DISABLED;
                        pos = 0;
                    }
                }
                comparator.setRow(pos);
                comparator.setSortType(sortType);
                Collections.sort(data, comparator);
//                Log.w(TAG, "##### Sort " + sortType + " " + lastHeaderPos + " " + pos);
                if (recyclerViews != null && !recyclerViews.isEmpty()) {
                    DbViewerRecyclerViewModel dbModel = recyclerViews.get(0);
                    if (dbModel.areValidData()) {
                        dbModel.adapter.highlightHeader(model, sortType);
                    }
                }
                lastHeaderPos = pos;
            }
            refreshGridList();
        }
    }

    @Override
    public void highlightCell(int row) {
        if (recyclerViews != null) {
            for (DbViewerRecyclerViewModel dbModel : recyclerViews) {
                if (dbModel.areValidData()) {
                    dbModel.adapter.highlightRow(row);
                }
            }
        }
    }

    @Override
    public void showDetailCell(DbViewerDataModel model) {
        yesNoDialog(model.getCleanedText(), "Close", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

            }
        });
    }
}