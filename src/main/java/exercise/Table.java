package exercise;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * TODO implement
 */
public class Table {

    private String[][] table;
    private Map<String, Integer> metadata;
    private int size;

    /**
     * @param M number of rows in a table
     * @param N number of columns in a table
     * @param metadata columns names
     */
    public Table(int M, int N, String[] metadata) {
        this.table = new String[M + 1][N];
        this.metadata = new HashMap<>();
        if (metadata.length < 2 || !metadata[0].equals("id")) {
            throw new IllegalArgumentException("Table should contain at least 2 columns, 1st columns must be `id`");
        }
        int mIndex = 0;
        for (String m : metadata) {
            if (m == null || m.isEmpty() || this.metadata.containsKey(m)) {
                throw new IllegalArgumentException("Metadata must not contain duplicates or empty strings");
            }
            this.metadata.put(m, mIndex++);
        }
    }

    protected Table(Table other) {
        this.table = other.table.clone();
        this.metadata = new HashMap<>(other.metadata);
    }

    public void insert(String [] data) {
        int index = Integer.parseInt(data[0]);
        verifyIndex(index);
        table[index] = data.clone();
        size++;
    }

    public String cellValue(int index, String column) {
        verifyIndex(index);
        verifyColumn(column);
        return table[index][metadata.get(column)];
    }

    public String[] rowValue(int index) {
        verifyIndex(index);
        return table[index].clone();
    }

    public int size() {
        return size;
    }

    public int numOfCols() {
        return metadata.size();
    }

    public List<String> getMetadata() {
        return metadata.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Table sortByColumn(String column, Comparator<String> comparator) {
        verifyColumn(column);
        Table copy = new Table(this);
        Arrays.sort(copy.table, 1, copy.table.length, (x,y) -> comparator
                .compare(x[metadata.get(column)], y[metadata.get(column)]));
        return copy;
    }

    private void verifyIndex(int index) {
        if (index < 1 || index > table.length) {
            throw new IllegalArgumentException("Index should be in [1;" + (table.length - 1) + "]");
        }
    }

    private void verifyColumn(String column) {
        if (!metadata.containsKey(column)) {
            throw new IllegalArgumentException("Column " + column + " doesn't exist");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table1 = (Table) o;
        return Arrays.equals(table, table1.table) &&
                Objects.equals(metadata, table1.metadata);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(metadata);
        result = 31 * result + Arrays.hashCode(table);
        return result;
    }

    @Override
    public String toString() {
        return "Table{" +
                "table=" + Arrays.deepToString(table) +
                ", metadata=" + metadata +
                '}';
    }
}
