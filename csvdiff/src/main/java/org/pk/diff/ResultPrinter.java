package org.pk.diff;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResultPrinter {

    private Appendable outputStream;



    public ResultPrinter (Appendable outputStream) {
        this.outputStream = outputStream;
    }

    public void printResult (List<RecordTuple> tuples, List<String> headerList, boolean hideEqualResults) throws IOException {
        CSVPrinter printer = new CSVPrinter(outputStream, CSVFormat.newFormat(';').withIgnoreEmptyLines().withFirstRecordAsHeader());

        printer.printRecord(comparisonHeaderList(headerList));
        outputStream.append("\n");

        for (RecordTuple tuple : tuples) {
            List<String> recordComparison = new ArrayList<String>();

            recordComparison.add(tuple.getSurrogateKey());
            recordComparison.add(tuple.recordIsEqual() + "");
            recordComparison.add(tuple.isBaseOnlyRecord()+ "");
            recordComparison.add(tuple.isActualOnlyRecord() + "");

            for (FieldTuple fieldTuple : tuple.compareRecords()) {

                if (!fieldTuple.isEqual() || !hideEqualResults) {
                    recordComparison.add(fieldTuple.getBase());
                    recordComparison.add(fieldTuple.getActual());
                    recordComparison.add("" + fieldTuple.isEqual());
                } else  {
                    recordComparison.add("");
                    recordComparison.add("");
                    recordComparison.add("" + fieldTuple.isEqual());
                }
            }
            printer.printRecord(recordComparison);
            outputStream.append("\n");
        }

        printer.flush();
        printer.close();
    }

    public void printResult (List<RecordTuple> tuples, List<String> headerList) throws IOException {
        printResult (tuples, headerList, false);
    }



    private List<String> comparisonHeaderList (List<String> headers) {
        List<String> comparisonHeaders = new ArrayList<String>();

        comparisonHeaders.add("Surrogate Key");
        comparisonHeaders.add("Record is Equal");
        comparisonHeaders.add("Base Only");
        comparisonHeaders.add("Actual Only");

        for (String header : headers) {
            comparisonHeaders.add(new String(header) + " - Base");
            comparisonHeaders.add(new String(header) + " - Actual");
            comparisonHeaders.add(new String(header) + " - Equals");
        }

        return comparisonHeaders;
    }
}
