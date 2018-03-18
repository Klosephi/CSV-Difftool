package org.pk.diff;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class CSVDiff {

    private static final Logger log = Logger.getLogger(Main.class.getName());

    private CSVParser baseParser;
    private CSVParser actualParser;

    private final char delimiter;
    private final Set<String> keys = new HashSet<String>();

    public CSVDiff(String... keys) {
        this.keys.addAll(Arrays.asList(keys));
        delimiter = ';';
    }

    public CSVDiff (char delimiter, String[] keys) {
        this.keys.addAll(Arrays.asList(keys));
        this.delimiter = delimiter;

        log.info("Created Diff");
        log.info("Delimiter:" + delimiter);
        log.info("Keys:" + StringUtils.join(keys));

    }



    public void load (String _base, String _actual) throws IOException {
        baseParser = CSVFormat.newFormat(delimiter).withFirstRecordAsHeader().parse(new FileReader(_base));
        actualParser = CSVFormat.newFormat(delimiter).withFirstRecordAsHeader().parse(new FileReader(_actual));
    }

    public List<RecordTuple> compare () {
        List<RecordTuple> comparison = new ArrayList<RecordTuple>();

        Map<String, CSVRecord> baseRecords = createRecordMap(baseParser);
        Map<String, CSVRecord> actualRecords = createRecordMap(actualParser);



        for (String key : baseRecords.keySet()) {
            RecordTuple recordTuple = new RecordTuple(baseRecords.get(key),actualRecords.get(key),key, getBaseHeaderList());
            comparison.add(recordTuple);
        }

        for (String key : actualRecords.keySet()) {
            if (!baseRecords.containsKey(key)) {
                RecordTuple recordTuple = new RecordTuple(baseRecords.get(key),actualRecords.get(key),key, getBaseHeaderList());
                comparison.add(recordTuple);
            }
        }



        return comparison;
    }

    public List<String> getBaseHeaderList() {
        List<String> headerList = new ArrayList<String>();

        for (Map.Entry<String, Integer> stringIntegerEntry : baseParser.getHeaderMap().entrySet()) {
            headerList.add(stringIntegerEntry.getValue(), stringIntegerEntry.getKey());
        }

        Collections.sort(headerList);
        return headerList;
    }

    public List<String> getActualHeaderList() {
        List<String> headerList = new ArrayList<String>();

        for (Map.Entry<String, Integer> stringIntegerEntry : actualParser.getHeaderMap().entrySet()) {
            headerList.add(stringIntegerEntry.getValue(), stringIntegerEntry.getKey());
        }

        Collections.sort(headerList);
        return headerList;
    }

    public List<String> getDifferingHeaders () {
        List<String> difference = new ArrayList<String>();
        List<String> baseHeaderList = getBaseHeaderList();
        List<String> actualHeaderList = getActualHeaderList();
        for (int i = 0; i< baseHeaderList.size() && i < actualHeaderList.size(); i++) {
            if (!baseHeaderList.get(i).equals(actualHeaderList.get(i))) {
                difference.add(actualHeaderList.get(i));
            }
        }

        return difference;
    }



    public Map<String, CSVRecord> createRecordMap (CSVParser parser) {

        Map<String, CSVRecord> keyRecords = new TreeMap<String, CSVRecord>();

        for (CSVRecord record : parser) {
            String surrogateKey = "";
            for (String key : keys) {
                surrogateKey += key + ":" + record.get(key) + "|";
            }
            keyRecords.put(surrogateKey, record);
        }

        return keyRecords;
    }



}
