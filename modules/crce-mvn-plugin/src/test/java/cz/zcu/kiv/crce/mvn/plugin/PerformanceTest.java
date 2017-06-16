package cz.zcu.kiv.crce.mvn.plugin;

import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.*;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.json.CentralRepoJsonResponse;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Zdenek Vales on 15.6.2017.
 */
public class PerformanceTest {

    @Test
    public void testSearchErrors() {
        String fc = "org.hibernate.dialect";
        int rows = 800;
        int queryCount = 1000;
        double avgTime = 0;
        long timeSum = 0;
        Map<Integer, Integer> results = new TreeMap<>();
        CentralRepoRestConsumer consumer = new CentralRepoRestConsumer();

        QueryBuilder qb = new QueryBuilder()
                .addParameter(QueryParam.CLASS_NAME, fc)
                .addStandardAdditionalParameters()
                .addAdditionalParameter(AdditionalQueryParam.ROWS,Integer.toString(rows));

        for (int i = 0; i < 1000; i++) {
            long now = System.currentTimeMillis();

            int code = 0;
            try {
                CentralRepoJsonResponse response = consumer.getJson(qb);
                code = 400;
            } catch (ServerErrorException e) {
                code = e.htmlCode;
            }

            if(!results.containsKey(code)) {
                results.put(code, 1);
            } else {
                results.put(code, results.get(code) +1);
            }

            timeSum += (System.currentTimeMillis() - now);
        }

        avgTime = timeSum / queryCount;

        System.out.println("Average time: "+avgTime+" ms");
        System.out.println(" HTML CODE | COUNT ");
        for(Integer code : results.keySet()) {
            System.out.println(String.format("%10d | %5d", code, results.get(code)));
        }
    }
}
