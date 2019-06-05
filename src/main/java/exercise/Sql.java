package exercise;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * TODO implement
 */
public class Sql {

    private final Comparator<String> DESC_COMP = Collections.reverseOrder();

    public Table init(InputStream csvContent) {

        Scanner scanner = new Scanner(csvContent);
        String[] metadata = null;
        if (scanner.hasNext()) {
            String rows = scanner.nextLine();
            String[] split = rows.split(",");
            metadata = new String[split.length + 1];
            metadata[0] = "id";
            for (int i = 1; i < metadata.length; i++) {
                metadata[i] = split[i - 1].trim();
            }
        }
        if (metadata == null) {
            return null;
        }
        List<String[]> rows = new ArrayList<>();
        int idx = 0;
        while (scanner.hasNext()) {
            String[] line = scanner.nextLine().split(",");
            rows.add(concatenateRows(new String[]{++idx + ""}, line));
        }
        scanner.close();
        Table table = new Table(rows.size(), metadata.length, metadata);
        for (String[] row : rows) {
            table.insert(row);
        }
        return table;
    }	

    public Table orderByDesc(Table table, String columnName){
        if (table == null) {
            throw new IllegalArgumentException("Table can't be null");
        }
        return table.sortByColumn(columnName, DESC_COMP);
    }
     // Solution is based on storing intersection in a hashmap
    // The other approach to join two tables could be based on sorting
    public Table join(Table left, Table right, String joinColumnTableLeft, String joinColumnTableRight) {
        Map<String, Map<Character, List<String[]>>> innerJoin = new HashMap<>();

        // process left table
        for(int i = 1; i < left.size(); i++) {
            String outerKey = left.cellValue(i, joinColumnTableLeft);
            if (outerKey == null) continue;
            innerJoin.putIfAbsent(outerKey, new HashMap<>());

            Map<Character, List<String[]>> columnMap = innerJoin.get(outerKey);
            columnMap.putIfAbsent('L', new ArrayList<>());
            columnMap.get('L').add(left.rowValue(i));
        }

        // process right table
        for(int i = 1; i < right.size(); i++) {
            String outerKey = right.cellValue(i, joinColumnTableRight);
            if (outerKey == null) continue;
            innerJoin.putIfAbsent(outerKey, new HashMap<>());

            Map<Character, List<String[]>> columnMap = innerJoin.get(outerKey);
            columnMap.putIfAbsent('R', new ArrayList<>());
            columnMap.get('R').add(right.rowValue(i));
        }

        // calculate number of rows result table size
        int M = 0;
        for (String key : innerJoin.keySet()) {
            if (innerJoin.get(key).size() == 2) {
                // just upper bound, actual num of entries is lower
                M += innerJoin.get(key).size() * 2;
            }
        }

        // create metadata for joined table
        String[] newMetadata = new String[left.numOfCols() + right.numOfCols() + 1];
        newMetadata[0] = "id";
        {
            int i = 1;
            for (String m : left.getMetadata()) {
                newMetadata[i++] = "l_" + m;
            }
            // change right metadata because table can't have multiple columns with the same name
            for (String m : right.getMetadata()) {
                newMetadata[i++] = "r_" + m;
            }
        }

        Table join = new Table(M, newMetadata.length, newMetadata);

        // fill table joins
        // nested loop is used two produce all possible pairs of joins
        int idCounter = 0;
        for (Map.Entry<String, Map<Character, List<String[]>>> je : innerJoin.entrySet()) {
            if (je.getValue().size() == 2) {
                for (String[] leftRow : je.getValue().get('L')) {
                    for (String[] rightRow : je.getValue().get('R')) {
                        join.insert(concatenateRows(new String[]{++idCounter + ""} ,concatenateRows(leftRow, rightRow)));
                    }
                }
            }
        }

        return join;
    }

    // should be inside utility class, made public to use in tests
    public static String[] concatenateRows(String [] left, String [] right) {
        String[] result = new String[left.length + right.length];
        int i = 0;
        for (String s : left) {
            result[i++] = s;
        }
        for (String s : right) {
            result[i++] = s;
        }
        return result;
    }
}
