import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.junit.Test;

import java.io.IOException;

public class XstreamTest {

    @Test
    public void testXstreamCreation() throws IOException, InterruptedException {
        ListTest listTest = new ListTest();
        // Convert the object to XML
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("listTest", ListTest.class);
        String xml = xstream.toXML(listTest);

        // Print the XML
        System.out.println("Serialized XML:\n" + xml);

//        // Deserialize the XML back to an object
//        ListTest deserializedListTest = (ListTest) xstream.fromXML(xml);
//
//        // Execute a method on the deserialized object
//        deserializedListTest.testList();
    }
}
