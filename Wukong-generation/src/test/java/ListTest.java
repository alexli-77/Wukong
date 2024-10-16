import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ListTest {

    @Test
    public void testList() throws InterruptedException, IOException {
        int i = 1;
        int j = 2;
        List<Integer> list = new ArrayList<>();
        list.add(i);
        list.add(j);
        System.out.println( list.remove(1));
        System.out.println("I'm in testList");
        assertEquals(1, list.size());
    }
}
