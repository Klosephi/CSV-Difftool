package org.pk.diff;

import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVDiffTest {



    @Test
    public void testCompare2 () throws IOException {
        CSVDiff csvDiff = new CSVDiff("Pfnr", "PfnrShortName");
        csvDiff.load("src/test/ressources/PORTFOLIOSTAMM.csv", "src/test/ressources/PORTFOLIOSTAMM-Actual.csv");
        List<RecordTuple> compare = csvDiff.compare();


        FileWriter fileWriter = new FileWriter("src/test/ressources/result.csv");
        new ResultPrinter(fileWriter).printResult(compare, csvDiff.getHeaderList());


    }

}
