package com.jantursky.debugger.dbviewer.comparators;

import com.jantursky.debugger.dbviewer.annotations.DbViewerSortType;
import com.jantursky.debugger.dbviewer.models.DbViewerDataModel;
import com.jantursky.debugger.dbviewer.models.DbViewerRowModel;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class DbGridComparator implements Comparator<ArrayList<DbViewerDataModel>> {

    @DbViewerSortType
    private int sortType = DbViewerSortType.DISABLED;
    private final Collator collator;
    private int row;

    public DbGridComparator() {
        collator = Collator.getInstance(Locale.getDefault());
        collator.setStrength(Collator.SECONDARY);
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public int compare(ArrayList<DbViewerDataModel> db1, ArrayList<DbViewerDataModel> db2) {
        if (sortType == DbViewerSortType.DISABLED) {
            DbViewerDataModel model1 = db1.get(0);
            DbViewerDataModel model2 = db2.get(0);
            return compareTexts(model1.getText(), model2.getText());
        } else {
            DbViewerDataModel model1 = db1.get(row);
            DbViewerDataModel model2 = db2.get(row);
            if (sortType == DbViewerSortType.A_Z) {
                return compareTexts(model1.getText(), model2.getText());
            } else {
                return -compareTexts(model1.getText(), model2.getText());
            }
        }
    }

    private boolean isDigit(char ch) {
        return ((ch >= 48) && (ch <= 57));
    }

    /**
     * Length of string is passed in for improved efficiency (only need to calculate it once)
     **/
    private String getChunk(String s, int slength, int marker) {
        StringBuilder chunk = new StringBuilder();
        char c = s.charAt(marker);
        chunk.append(c);
        marker++;
        if (isDigit(c)) {
            while (marker < slength) {
                c = s.charAt(marker);
                if (!isDigit(c))
                    break;
                chunk.append(c);
                marker++;
            }
        } else {
            while (marker < slength) {
                c = s.charAt(marker);
                if (isDigit(c))
                    break;
                chunk.append(c);
                marker++;
            }
        }
        return chunk.toString();
    }

    public int compareTexts(String s1, String s2) {
        if ((s1 == null) || (s2 == null)) {
            return 0;
        }

        int thisMarker = 0;
        int thatMarker = 0;
        int s1Length = s1.length();
        int s2Length = s2.length();

        while (thisMarker < s1Length && thatMarker < s2Length) {
            String thisChunk = getChunk(s1, s1Length, thisMarker);
            thisMarker += thisChunk.length();

            String thatChunk = getChunk(s2, s2Length, thatMarker);
            thatMarker += thatChunk.length();

            // If both chunks contain numeric characters, sort them numerically
            int result;
            if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
                // Simple chunk comparison by length.
                int thisChunkLength = thisChunk.length();
                result = thisChunkLength - thatChunk.length();
                // If equal, the first different number counts
                if (result == 0) {
                    for (int i = 0; i < thisChunkLength; i++) {
                        result = thisChunk.charAt(i) - thatChunk.charAt(i);
                        if (result != 0) {
                            return result;
                        }
                    }
                }
            } else {
                result = thisChunk.compareTo(thatChunk);
            }

            if (result != 0)
                return result;
        }

        return s1Length - s2Length;
    }

    public void setSortType(@DbViewerSortType int sortType) {
        this.sortType = sortType;
    }

    public int getSortType() {
        return sortType;
    }
}