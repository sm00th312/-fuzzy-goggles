package exercise;

import org.junit.Assert;
import org.junit.Test;

public class TableTest {

    @Test(expected = IllegalArgumentException.class)
    public void testInsertFailsWithInvalidIndex() {
        //given
        Table table = new Table(5, 1, new String[]{"id", "name"});

        //when
        table.insert(new String[]{"-1", "foo"});
    }

    @Test
    public void testInsertOk() {
        //given
        Table table = new Table(5, 1, new String[]{"id", "name"});
        String [] data = new String[]{"1", "foo"};

        //when
        table.insert(data);

        //then
        Assert.assertArrayEquals(table.rowValue(1), data);
        Assert.assertNotSame(table.rowValue(1), data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSortByColumnFailsInvalidColumn() {
        //given
        Table table = new Table(5, 1, new String[]{"id", "name"});

        //when
        table.sortByColumn("surname", String::compareTo);
    }

    @Test
    public void testSortByColumnOk() {
        //given
        Table table = new Table(3, 1, new String[]{"id", "value"});
        System.out.println(table.getMetadata());
        //when
        table.insert(new String[]{"2", "b"});
        table.insert(new String[]{"1", "a"});
        table.insert(new String[]{"3", "c"});

        Table sorted = table.sortByColumn("value", String::compareTo);

        //then
        Assert.assertNotSame(sorted, table);
        Assert.assertArrayEquals(sorted.rowValue(1), new String[]{"1", "a"});
        Assert.assertArrayEquals(sorted.rowValue(2), new String[]{"2", "b"});
        Assert.assertArrayEquals(sorted.rowValue(3), new String[]{"3", "c"});
    }
}
