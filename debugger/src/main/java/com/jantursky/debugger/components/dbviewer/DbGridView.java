package com.jantursky.debugger.components.dbviewer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
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
import android.support.v7.widget.SimpleItemAnimator;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jantursky.debugger.R;
import com.jantursky.debugger.components.dbviewer.adapters.DbViewerGridRowAdapter;
import com.jantursky.debugger.components.dbviewer.adapters.DbViewerListAdapter;
import com.jantursky.debugger.components.dbviewer.annotations.DbViewerScreenType;
import com.jantursky.debugger.components.dbviewer.annotations.DbViewerSortType;
import com.jantursky.debugger.components.dbviewer.comparators.DbGridComparator;
import com.jantursky.debugger.components.dbviewer.helpers.DBViewerHelper;
import com.jantursky.debugger.components.dbviewer.listeners.DbViewerGridItemListener;
import com.jantursky.debugger.components.dbviewer.listeners.DbViewerInputListener;
import com.jantursky.debugger.components.dbviewer.listeners.DbViewerListItemListener;
import com.jantursky.debugger.components.dbviewer.models.DbViewerColumnModel;
import com.jantursky.debugger.components.dbviewer.models.DbViewerDataModel;
import com.jantursky.debugger.components.dbviewer.models.DbViewerRecyclerViewModel;
import com.jantursky.debugger.components.dbviewer.models.DbViewerRowModel;
import com.jantursky.debugger.interfaces.ComponentListener;
import com.jantursky.debugger.interfaces.GlobalViewListener;
import com.jantursky.debugger.listeners.RepeatListener;
import com.jantursky.debugger.utils.FormatUtils;
import com.jantursky.debugger.utils.RecyclerViewUtils;
import com.jantursky.debugger.utils.StringUtils;
import com.jantursky.debugger.utils.ViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DbGridView extends FrameLayout implements View.OnClickListener, DbViewerListItemListener, DbViewerGridItemListener {

    private static final String TAG = DbGridView.class.getSimpleName();

    private static final float ALPHA_DISABLED = 0.4F;
    private static final float ALPHA_ENABLED = 1.0F;

    private LinearLayout gridCntLayout;
    private View listLayout, dataLayout;
    private TextView txtCancel, pageTxt, txtSchema, txtQuery, txtRowCount, pageLeftTxt, pageRightTxt, pageFirstTxt,
            pageLastTxt, closeTxt, deleteTxt, addTxt;

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
    private RecyclerView.RecycledViewPool recyclerViewPoolItems;
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

    private ComponentListener componentListener;

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

        txtCancel = view.findViewById(R.id.cancel_textview);
        pageTxt = view.findViewById(R.id.page_textview);
        txtSchema = view.findViewById(R.id.schema_textview);
        txtQuery = view.findViewById(R.id.query_textview);
        txtRowCount = view.findViewById(R.id.row_count_textview);
        pageLeftTxt = view.findViewById(R.id.page_left_textview);
        pageRightTxt = view.findViewById(R.id.page_right_textview);
        pageFirstTxt = view.findViewById(R.id.page_first_textview);
        pageLastTxt = view.findViewById(R.id.page_last_textview);
        deleteTxt = view.findViewById(R.id.delete_textview);
        closeTxt = view.findViewById(R.id.close_textview);
        addTxt = view.findViewById(R.id.add_textview);

        rowHeight = getContext().getResources().getDimension(R.dimen.db_viewer_row_height);

        recyclerViewPoolItems = new RecyclerView.RecycledViewPool();

        setTablesList();

        ViewUtils.addListener(gridCntLayout, new GlobalViewListener() {
            @Override
            public void onGlobalLayout(int width, int height) {
                maxPerPage = (int) Math.floor((float) (height) / rowHeight);
                displayDatabases();
            }
        });

        setListeners();
    }

    public void setComponentListener(ComponentListener componentListener) {
        this.componentListener = componentListener;
    }

    private void setTablesList() {
        dbViewerListAdapter = new DbViewerListAdapter();
        recyclerViewList.addItemDecoration(RecyclerViewUtils.getVerticalItemDecoration(getContext()));
        recyclerViewList.setAdapter(dbViewerListAdapter);
        ((SimpleItemAnimator) recyclerViewList.getItemAnimator()).setSupportsChangeAnimations(false);
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
            File[] list = root.listFiles();
            for (File file : list) {
                String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                if (!extension.equals(".db-journal")) {
                    files.add(file);
                }
            }
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
            txtCancel.setVisibility(View.VISIBLE);
        } else if (screenType == DbViewerScreenType.Data) {
            listLayout.setVisibility(View.GONE);
            dataLayout.setVisibility(View.VISIBLE);
            txtCancel.setVisibility(View.GONE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        pageTxt.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        txtQuery.setOnClickListener(this);
        txtSchema.setOnClickListener(this);
        closeTxt.setOnClickListener(this);
        deleteTxt.setOnClickListener(this);
        pageFirstTxt.setOnClickListener(this);
        pageLastTxt.setOnClickListener(this);

        pageLeftTxt.setOnTouchListener(new RepeatListener(400, 30, true, new OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = currentPage - 1;
                jumpToPage(current);
            }
        }));

        pageRightTxt.setOnTouchListener(new RepeatListener(400, 30, true, new OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = currentPage + 1;
                jumpToPage(current);
            }
        }));
    }

    private void selectDb(File dbFile) {
        DBViewerHelper.resetInstance();
        dbHelper = DBViewerHelper.getInstance(getContext(), dbFile);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.cancel_textview) {
            if (componentListener != null) {
                componentListener.closeComponent();
            }
        } else if (viewId == R.id.page_textview) {
            displayJumpToPageDialog();
        } else if (viewId == R.id.delete_textview) {
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
        } else if (viewId == R.id.page_first_textview) {
            jumpToFirst();
        } else if (viewId == R.id.page_last_textview) {
            jumpToLast();
        } else if (viewId == R.id.schema_textview) {
            yesNoDialog(FormatUtils.formatSchema(dbHelper.getSchema(selectedTable)), "Close", null, null);
        } else if (viewId == R.id.query_textview) {
            inputDialog("Create query", "Select * from " + selectedTable, "Execute", "Cancel", new DbViewerInputListener() {
                @Override
                public void applyInput(String text) {
                    if (!isEmpty(text)) {
                        fillData(null, text);
                    }
                }

                @Override
                public void neutral() {

                }
            });
        }
    }

    private void jumpToFirst() {
        int current = 1;
        jumpToPage(current);
    }

    private void jumpToLast() {
        int current = totalPage;
        jumpToPage(current);
    }

    private boolean isEmpty(String text) {
        return StringUtils.isEmpty(text);
    }

    private void inputDialog(String message, String inputText, String yes, String no, final DbViewerInputListener listener) {
        inputDialog(message, inputText, yes, no, null, listener);
    }

    private void inputDialog(String message, String inputText, String yes, String no, String neutral, final DbViewerInputListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(message);

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(inputText);
        input.setSingleLine(false);
        input.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        builder.setView(input);

        input.setSelection(input.getText().length());

        if (!isEmpty(yes)) {
            builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.applyInput(input.getText().toString());
                    }
                }
            });
        }

        if (!isEmpty(no)) {
            builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }

        if (!isEmpty(neutral)) {
            builder.setNeutralButton(neutral, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.neutral();
                    }
                }
            });
        }

        builder.show();
    }

    private void yesNoDialog(String message, String yes, String no, DialogInterface.OnClickListener dialogClickListener) {
        yesNoDialog(message, yes, no, null, dialogClickListener);
    }

    private void yesNoDialog(String message, String yes, String no, String neutral, DialogInterface.OnClickListener dialogClickListener) {
        if (isEmpty(message)) {
            message = "";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        if (!isEmpty(yes)) {
            builder.setPositiveButton(yes, dialogClickListener);
        }
        if (!isEmpty(no)) {
            builder.setNegativeButton(no, dialogClickListener);
        }
        if (!isEmpty(neutral)) {
            builder.setNeutralButton(neutral, dialogClickListener);
        }
        builder.show();
    }

    public void deleteTable(String table) {
        dbHelper.delete(table, null);
        resetDataView();
        displayData(table, null);
    }

    private void jumpToPage(int current) {
        if (current >= 1 && current <= totalPage) {
            currentPage = current;
            refreshGridList();
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
            pageFirstTxt.setVisibility(View.INVISIBLE);
        } else {
            pageLeftTxt.setAlpha(ALPHA_ENABLED);
            pageLeftTxt.setBackgroundResource(R.drawable.db_viewer_icon_selector);
            pageFirstTxt.setVisibility(View.VISIBLE);
            pageRightTxt.setBackgroundResource(R.drawable.db_viewer_icon_selector);
        }

        if (currentPage >= totalPage) {
            pageRightTxt.setAlpha(ALPHA_DISABLED);
            pageRightTxt.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.db_viewer_bottom_bg));
            pageLastTxt.setVisibility(View.INVISIBLE);
        } else {
            pageRightTxt.setAlpha(ALPHA_ENABLED);
            pageRightTxt.setBackgroundResource(R.drawable.db_viewer_icon_selector);
            pageLastTxt.setVisibility(View.VISIBLE);
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
        resetDataView();
        displayData(table, null);
    }

    private void resetDataView() {
        scrollBy = 0;
        count = 0;
        currentPage = 1;
        totalPage = 1;
        lastHeaderPos = -1;
    }

    private void displayData(String table, String query) {
        screenType = DbViewerScreenType.Data;
        applyView();
        fillData(table, query);
    }

    private void firstListFill() {
        gridCntLayout.addView(addRecyclerRow(headerRow.size(), true, 0));
        for (int i = 0; i < maxPerPage; i++) {
            int pos = i + 1;
            gridCntLayout.addView(addRecyclerRow(headerRow.size(), false, pos));
        }
    }

    private void fillData(String table, String query) {
        if (!isEmpty(table)) {
            selectedTable = table;
            deleteTxt.setVisibility(View.VISIBLE);

            String[] tableColumns = dbHelper.getAllColumns(table);

            headerRow = dbHelper.getColumnsType(table);
            count = dbHelper.getCountForTable(table);
            totalPage = (int) Math.ceil(count / (maxPerPage - 1));
//            Log.w(TAG, "##### Total BEFORE: " + totalPage + " " + count + " " + maxPerPage);
            if (((maxPerPage - 1) * totalPage) <= count) {
                totalPage++;
            }
//            Log.w(TAG, "##### Total AFTER:  " + totalPage + " " + count + " " + maxPerPage);
            data = dbHelper.getDataForTable(table, tableColumns, getPrimaryColumn(headerRow));
        } else {
            selectedTable = null;
            deleteTxt.setVisibility(View.GONE);

            String[] tableColumns = dbHelper.getCustomColumns(query);

            headerRow = dbHelper.getColumnsType(query);
            count = dbHelper.getCountForQuery(query);
            totalPage = (int) Math.ceil(count / maxPerPage);
            if ((maxPerPage * totalPage) <= count) {
                totalPage++;
            }
            data = dbHelper.getDataForQuery(query, tableColumns, getPrimaryColumn(headerRow));
        }

        txtRowCount.setText(getResources().getQuantityString(R.plurals.data_row_count, data.size(), data.size()));

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

    private String getPrimaryColumn(ArrayList<DbViewerDataModel> headerRow) {
        if (headerRow != null && !headerRow.isEmpty()) {
            for (DbViewerDataModel dbViewerDataModel : headerRow) {
                if (((DbViewerColumnModel) dbViewerDataModel).isPrimaryKey) {
                    return ((DbViewerColumnModel) dbViewerDataModel).columnName;
                }
            }
        }
        return null;
    }

    private void setRowData() {
        if (data != null) {
            List<ArrayList<DbViewerDataModel>> dataPerPage = getDataForPage(currentPage, maxPerPage, data);

            updateGridRow(headerRow, getColumns(), true, recyclerViews.get(0), 0);
            for (int i = 0; i < maxPerPage; i++) {
                int pos = i + 1;
                if (!dataPerPage.isEmpty() && i < dataPerPage.size()) {
                    ArrayList<DbViewerDataModel> arrayList = dataPerPage.get(i);
                    updateGridRow(arrayList, getColumns(), false, recyclerViews.get(pos), pos);
                } else {
                    updateGridRow(null, getColumns(), false, recyclerViews.get(pos), pos);
                }
            }
        }
    }

    private int getColumns() {
        return (headerRow != null) ? headerRow.size() : 0;
    }

    private void updateGridRow(ArrayList<DbViewerDataModel> headerRow, int columns, boolean isHeader, DbViewerRecyclerViewModel dbViewerRecyclerViewModel, int pos) {
        dbViewerRecyclerViewModel.adapter.setData(headerRow, columns, isHeader, currentPage, maxPerPage, pos);
    }

    private View addRecyclerRow(int columns, boolean isHeader, int tagId) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_db_grid_recyclerview, this, false);
        RecyclerView recyclerView = view.findViewById(R.id.list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        DbViewerGridRowAdapter adapter = new DbViewerGridRowAdapter(getContext(), isHeader);
        adapter.setOnItemClickListener(this);

        recyclerView.setAdapter(adapter);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setTag(tagId);
        recyclerView.addOnScrollListener(listener);
        recyclerView.setPadding(0, 0, 0, getContext().getResources().getDimensionPixelSize(R.dimen.db_viewer_grid_row_spacing));
        recyclerView.setHasFixedSize(true);
        recyclerView.setRecycledViewPool(recyclerViewPoolItems);

        recyclerViews.add(new DbViewerRecyclerViewModel(view, recyclerView, adapter));
        return view;
    }

    private void scrollToX(int tagId, int scrollX) {
        for (DbViewerRecyclerViewModel model : recyclerViews) {
            if ((Integer) model.recyclerView.getTag() != tagId) {
                model.recyclerView.removeOnScrollListener(listener);
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
        pageTxt.setText(currentPage + "/" + (totalPage == 0 ? 1 : totalPage));
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
        if (data != null && !data.isEmpty()) {
            int pos = -1;
            ArrayList<DbViewerDataModel> dbViewerRowModels = data.get(0);
            for (int i = 0; i < dbViewerRowModels.size(); i++) {
                DbViewerDataModel dbModel = dbViewerRowModels.get(i);
                if ((dbModel.isHeader() || dbModel.isRow()) && dbModel.dbKey.equals(model.dbKey)) {
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
    public void showDetailCell(final DbViewerDataModel model) {
//        Log.w(TAG, "##### ShowDetailCell " + model.toString());
        showDetailCell(model, ((DbViewerRowModel) model).getCleanedText());
    }

    public void showDetailCell(final DbViewerDataModel model, String data) {
        yesNoDialog(data,
                "Close",
                !isEmpty(selectedTable) ? "Edit" : null,
                "Format",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            inputDialog("Edit cell", ((DbViewerRowModel) model).getCleanedText(), "Save", "Cancel", "Clear", new DbViewerInputListener() {
                                @Override
                                public void applyInput(String text) {
                                    saveData(model, text);
                                }

                                @Override
                                public void neutral() {
                                    saveData(model, null);
                                }
                            });
                        } else if (which == DialogInterface.BUTTON_NEUTRAL) {
                            showDetailCell(model, ((DbViewerRowModel) model).getCleanedText(true));
                        }
                    }
                });
    }

    private void saveData(DbViewerDataModel model, String text) {
        if (isEmpty(text)) {
            text = "";
        }
        ContentValues cv = new ContentValues();
        cv.put(model.dbKey, text);
        dbHelper.update(
                selectedTable,
                cv,
                ((DbViewerRowModel) model).primaryColumnName + "=?",
                new String[]{String.valueOf(((DbViewerRowModel) model).primaryColumnValue)});

        displayData(selectedTable, null);
    }
}