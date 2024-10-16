import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ListTest {

    public void printTraceCall() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            System.out.println(element.getClassName() + " - " + element.getMethodName());
        }
    }
    @Test
    public void testList() throws InterruptedException, IOException {
        int i = 1;
        int j = 2;
        List<Integer> list = new ArrayList<>();
        list.add(i);
        list.add(j);
        System.out.println( list.remove(1));
        System.out.println("I'm in testList");
//        printTraceCall();
        assertEquals(1, list.size());
//        Thread.sleep(60000);
    }
}
