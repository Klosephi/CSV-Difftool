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
    private List<RecordTuple> comparison;
    private Map<String, Integer> baseDuplicatedKeys;
    private Map<String, Integer> actualDuplicatedKeys;

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
        if (comparison != null) {
            return comparison;
        }

        comparison = new ArrayList<RecordTuple>();
        baseDuplicatedKeys = new HashMap<String, Integer>();
        actualDuplicatedKeys = new HashMap<String, Integer>();

        Map<String, CSVRecord> baseRecords = createRecordMap(baseParser, baseDuplicatedKeys);
        Map<String, CSVRecord> actualRecords = createRecordMap(actualParser, actualDuplicatedKeys);



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





    public Map<String, CSVRecord> createRecordMap (CSVParser parser, Map<String, Integer> keyCounter) {

        Map<String, CSVRecord> keyRecords = new TreeMap<String, CSVRecord>();

        for (CSVRecord record : parser) {
            String surrogateKey = "";
            for (String key : keys) {
                surrogateKey += key + ":" + record.get(key) + "|";
            }
            keyRecords.put(surrogateKey, record);

            if (keyCounter.containsKey(surrogateKey)) {
                Integer integer = keyCounter.get(surrogateKey);
                keyCounter.put(surrogateKey, integer + 1);
            } else {
                keyCounter.put(surrogateKey, 0);
            }
        }

        return keyRecords;
    }

    public Map<String, Integer> getBaseDuplicatedKeys() {
        return baseDuplicatedKeys;
    }

    public Map<String, Integer> getActualDuplicatedKeys() {
        return actualDuplicatedKeys;
    }

    public static List<String> getKeysGreateThan (Map<String, Integer> map, int g) {

        List<String> keys = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > g) {
                keys.add(entry.getKey());
            }
        }

        return keys;
    }
}
