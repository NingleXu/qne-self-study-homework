package cn.ningle.logstats;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author ningle
 * @version : GuavaTest.java, v 0.1 2024/06/21 23:28 ningle
 **/
public class GuavaTest {

    @Test
    public void testMultiMap() {
        Multimap<String, String> multimap = LinkedHashMultimap.create();
        multimap.put("ningle", "123");
        multimap.put("ningle", "1234");
        multimap.put("ningle", "add");
        multimap.put("ningle", "abb");
        multimap.put("ningle", "123");
        System.out.println(multimap);
    }

}
