package cz.zcu.kiv.crce.mvn.plugin;

import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.*;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.json.CentralRepoJsonResponse;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Zdenek Vales on 15.6.2017.
 */
public class PerformanceTest {

    /**
     * Server returns 500 or 504 everytime.
     */
    @Test
    @Ignore
    public void testSearchErrors() {
        String fc = "org.hibernate.dialect";
        int rows = 800;
        int queryCount = 100;
        double avgTime = 0;
        long timeSum = 0;
        Map<Integer, Integer> results = new TreeMap<>();
        CentralRepoRestConsumer consumer = new CentralRepoRestConsumer();

        QueryBuilder qb = new QueryBuilder()
                .addParameter(QueryParam.CLASS_NAME, fc)
                .addStandardAdditionalParameters()
                .addAdditionalParameter(AdditionalQueryParam.ROWS,Integer.toString(rows));

        for (int i = 0; i < queryCount; i++) {
            if(i % 10 == 0) {
                System.out.println(String.format("%d iteration.",i));
                avgTime = timeSum / queryCount;
                System.out.println("Average time: "+avgTime+" ms");
                System.out.println(" HTML CODE | COUNT ");
                for(Integer c : results.keySet()) {
                    System.out.println(String.format("%10d | %5d", c, results.get(c)));
                }
                System.out.println("");
            }

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

    /**
     * Test differences in times needed to download set of results with and without paralelization.
     */
    @Test
    @Ignore
    public void testThreadVsNonThreadPerformance() throws ServerErrorException {
        long nonParalelTime = 0L;
        long paralelTime = 0L;
        int rows = 800;
        String fc = "org.hibernate.dialect";
        CentralRepoRestConsumer consumer = new CentralRepoRestConsumer();
        int maxArt = CentralMavenRestLocator.MAX_ARTIFACTS_PER_QUERY;
        int queryCount = rows / maxArt;

        // prepare queries
        QueryBuilder qb = new QueryBuilder()
                .addParameter(QueryParam.CLASS_NAME, fc)
                .addStandardAdditionalParameters()
                .addAdditionalParameter(AdditionalQueryParam.ROWS,Integer.toString(maxArt));
        List<QueryBuilder> queries = new ArrayList<>();
        for (int i = 0; i < queryCount; i++) {
            QueryBuilder q = qb.clone();
            q.addAdditionalParameter(AdditionalQueryParam.START, Integer.toString(maxArt*i));
            queries.add(q);
        }


        // non paralel download
        nonParalelTime = System.nanoTime();
        // download results
        for (int i = 0; i < queryCount; i++) {
            QueryBuilder q = queries.get(i);
            consumer.getJson(q);
        }
        nonParalelTime = System.nanoTime() - nonParalelTime;

        // paralel download
        paralelTime = System.nanoTime();
        List<FetchResultSetThread> threadPool = new ArrayList<>(queryCount);
        // start downloading threads
        for (int i = 0; i < queryCount; i++) {
//            logger.debug("Starting result downloading thread for "+maxArt+" results starting at "+start);
            FetchResultSetThread t = new FetchResultSetThread(queries.get(i));
            threadPool.add(t);
            t.start();
        }

        // join threads and get found artifacts
        for (FetchResultSetThread t : threadPool) {
            try {
                t.join();
//                foundArtifacts.addAll(t.getFoundArtifacts());
            } catch (InterruptedException e) {
//                logger.error("Error while joining result downloading thread: "+e.getMessage());
            }
        }
        paralelTime = System.nanoTime() - paralelTime;

        System.out.println("Time to download "+rows+" in "+queryCount+" parts without threads: "+nonParalelTime / 1000000000);
        System.out.println("Time to download "+rows+" in "+queryCount+" parts with threads: "+paralelTime / 1000000000);
    }

}
