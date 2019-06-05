package exercise;

import org.junit.Assert;
import org.junit.Test;

public class SqlTest {

    @Test
    public void testJoinTwoTablesNonEmpty() {
        // given
        Table t1 = new Table(3, 3, new String[]{"id", "name", "age"});

        t1.insert(new String[]{"1", "foo", "41"});
        t1.insert(new String[]{"2", "bar", "42"});
        t1.insert(new String[]{"3", "bar", "49"});

        Table t2 = new Table(3, 3, new String[]{"id", "label", "surname"});

        t2.insert(new String[]{"1", "foo", "foo"});
        t2.insert(new String[]{"2", "bar", "test"});
        t2.insert(new String[]{"3", "bazz", "test123"});

        Sql sql = new Sql();

        //when
        Table join = sql.join(t1, t2, "name", "label");

        Assert.assertTrue(join.size() > 0);
        String[][] expectedJoins = new String[3][7];
        expectedJoins[0] = new String[]{"1", "2", "bar", "42", "2", "bar", "test"};
        expectedJoins[1] = new String[]{"2", "3", "bar", "49", "2", "bar", "test"};
        expectedJoins[2] = new String[]{"3", "1", "foo", "41", "1", "foo", "foo"};

        //then
        for (int i = 1; i < join.size(); i++) {
            String[] row = join.rowValue(i);
            if (row[0] == null) continue;
            boolean found = false;
            for (String[] expectedJoin : expectedJoins) {
                found |= containsJoinedTow(expectedJoin, row);
            }
            Assert.assertTrue(found);
        }
    }

    @Test
    public void testJoinTwoTablesEmpty() {
        // given
        Table t1 = new Table(3, 3, new String[]{"id", "name", "age"});

        t1.insert(new String[]{"1", "foo1", "41"});
        t1.insert(new String[]{"2", "bar1", "42"});
        t1.insert(new String[]{"3", "bar1", "49"});

        Table t2 = new Table(3, 3, new String[]{"id", "label", "surname"});

        t2.insert(new String[]{"1", "foo2", "foo"});
        t2.insert(new String[]{"2", "bar2", "test"});
        t2.insert(new String[]{"3", "bazz2", "test123"});

        Sql sql = new Sql();

        //when
        Table join = sql.join(t1, t2, "name", "label");

        //then
        Assert.assertEquals(0, join.size());
    }

    private boolean containsJoinedTow(String[] expected, String [] actual) {
        for (int i = 1; i < expected.length; i++) {
            if (!expected[i].equals(actual[i])) return false;
        }
        return true;
    }

}
