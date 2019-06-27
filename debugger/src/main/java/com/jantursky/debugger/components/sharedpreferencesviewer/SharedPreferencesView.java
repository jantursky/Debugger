package com.jantursky.debugger.components.sharedpreferencesviewer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jantursky.debugger.R;
import com.jantursky.debugger.components.sharedpreferencesviewer.adapters.SharedPreferencesViewerListAdapter;
import com.jantursky.debugger.components.sharedpreferencesviewer.annotations.SharedPreferencesViewerScreenType;
import com.jantursky.debugger.components.sharedpreferencesviewer.comparators.SharedPreferencesListComparator;
import com.jantursky.debugger.components.sharedpreferencesviewer.interfaces.SharedPreferencesViewerListItemListener;
import com.jantursky.debugger.components.sharedpreferencesviewer.models.SharedPreferencesListModel;
import com.jantursky.debugger.interfaces.ComponentListener;
import com.jantursky.debugger.utils.FileUtils;
import com.jantursky.debugger.utils.RecyclerViewUtils;
import com.jantursky.debugger.utils.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SharedPreferencesView extends FrameLayout implements View.OnClickListener, SharedPreferencesViewerListItemListener {

    private static final String TAG = SharedPreferencesView.class.getSimpleName();

    private View listLayout, dataLayout;
    private EditText inputEditText;
    private TextView txtCancel, txtEdit, txtDelete, txtClose;

    @SharedPreferencesViewerScreenType
    private int screenType = SharedPreferencesViewerScreenType.List;

    private SharedPreferencesViewerListAdapter sharedPreferencesViewerListAdapter;

    private RecyclerView recyclerViewList;

    private File selectedFile;

    private SharedPreferencesListComparator comparator = new SharedPreferencesListComparator();

    private ComponentListener componentListener;

    public SharedPreferencesView(@NonNull Context context) {
        super(context);
        init();
    }

    public SharedPreferencesView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SharedPreferencesView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SharedPreferencesView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_shared_preferences_viewer, this, true);

        listLayout = view.findViewById(R.id.list_layout);
        dataLayout = view.findViewById(R.id.data_layout);

        recyclerViewList = view.findViewById(R.id.list_recycler_view);

        inputEditText = view.findViewById(R.id.input_edittext);

        txtCancel = view.findViewById(R.id.cancel_textview);
        txtClose = view.findViewById(R.id.close_textview);
        txtDelete = view.findViewById(R.id.delete_textview);
        txtEdit = view.findViewById(R.id.edit_textview);

        setTablesList();
        displayList();

        setListeners();
    }

    public void setComponentListener(ComponentListener componentListener) {
        this.componentListener = componentListener;
    }

    private void setTablesList() {
        sharedPreferencesViewerListAdapter = new SharedPreferencesViewerListAdapter();
        recyclerViewList.addItemDecoration(RecyclerViewUtils.getVerticalItemDecoration(getContext()));
        recyclerViewList.setAdapter(sharedPreferencesViewerListAdapter);
        ((SimpleItemAnimator) recyclerViewList.getItemAnimator()).setSupportsChangeAnimations(false);
        sharedPreferencesViewerListAdapter.setOnItemClickListener(this);
    }

    private void displayList() {
        screenType = SharedPreferencesViewerScreenType.List;
        applyView();
        sharedPreferencesViewerListAdapter.setData(getDbFiles());
    }

    private void displayFile(File file) {
        screenType = SharedPreferencesViewerScreenType.Data;
        selectedFile = file;
        applyView();
        displayData();
    }

    private void displayData() {
        String fileContent = FileUtils.readFile(selectedFile);
        inputEditText.setEnabled(false);
        inputEditText.setText(fileContent);

        applyEditable();
    }

    private void applyEditable() {
        if (inputEditText.isEnabled()) {
            txtEdit.setText(R.string.action_save);
        } else {
            txtEdit.setText(R.string.action_edit);
        }
    }

    private ArrayList<SharedPreferencesListModel> getDbFiles() {
        File root = new File("/data/data/" + getContext().getPackageName() + "/shared_prefs");
        ArrayList<SharedPreferencesListModel> files = new ArrayList<>();
        if (root.isDirectory()) {
            File[] list = root.listFiles();
            for (File file : list) {
                files.add(new SharedPreferencesListModel(file));
            }
        }
        Collections.sort(files, comparator);
        return files;
    }

    private void applyView() {
        if (screenType == SharedPreferencesViewerScreenType.List) {
            listLayout.setVisibility(View.VISIBLE);
            dataLayout.setVisibility(View.GONE);
            txtCancel.setVisibility(View.VISIBLE);
        } else if (screenType == SharedPreferencesViewerScreenType.Data) {
            listLayout.setVisibility(View.GONE);
            dataLayout.setVisibility(View.VISIBLE);
            txtCancel.setVisibility(View.GONE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        txtCancel.setOnClickListener(this);
        txtEdit.setOnClickListener(this);
        txtDelete.setOnClickListener(this);
        txtClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.cancel_textview) {
            if (componentListener != null) {
                componentListener.closeComponent();
            }
        } else if (viewId == R.id.edit_textview) {
            if (inputEditText.isEnabled()) {
                saveFile(selectedFile);
            } else {
                inputEditText.setEnabled(true);
            }
            applyEditable();
        } else if (viewId == R.id.delete_textview) {
            yesNoDialog("Delete the file?", "Yes", "No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        deleteFile(selectedFile);
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {

                    }
                }
            });
        } else if (viewId == R.id.close_textview) {
            canGoBack();
        }
    }

    private void saveFile(File file) {
        try {
            FileWriter out = new FileWriter(file, false);
            out.write(inputEditText.getText().toString());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        displayData();
    }

    private void yesNoDialog(String message, String yes, String no, DialogInterface.OnClickListener dialogClickListener) {
        yesNoDialog(message, yes, no, null, dialogClickListener);
    }

    private void yesNoDialog(String message, String yes, String no, String neutral, DialogInterface.OnClickListener dialogClickListener) {
        if (StringUtils.isEmpty(message)) {
            message = "";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        if (!StringUtils.isEmpty(yes)) {
            builder.setPositiveButton(yes, dialogClickListener);
        }
        if (!StringUtils.isEmpty(no)) {
            builder.setNegativeButton(no, dialogClickListener);
        }
        if (!StringUtils.isEmpty(neutral)) {
            builder.setNeutralButton(neutral, dialogClickListener);
        }
        builder.show();
    }

    public void deleteFile(File file) {
        if (file.canExecute()) {
            file.delete();
        }
        displayList();
    }

    public boolean canGoBack() {
        if (screenType == SharedPreferencesViewerScreenType.Data) {
            displayList();
            return false;
        } else if (screenType == SharedPreferencesViewerScreenType.List) {
            return true;
        }
        return true;
    }

    @Override
    public void onFileClick(File file) {
        displayFile(file);
    }
}