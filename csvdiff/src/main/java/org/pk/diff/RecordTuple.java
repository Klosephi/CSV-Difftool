package org.pk.diff;


import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.List;

public class RecordTuple {

    private final CSVRecord base;
    private final CSVRecord actual;
    private final String surrogateKey;
    private final int size;
    private final List<String> headerlist = new ArrayList<String>();
    private final List<FieldTuple> fieldTuples = new ArrayList<FieldTuple>();


    private Boolean isEqual;

    public RecordTuple(CSVRecord base, CSVRecord actual, String surrogateKey, List<String> headerList) {
        this.base = base;
        this.actual = actual;
        this.surrogateKey = surrogateKey;
        if (base != null) {
            size = base.size();
        } else if (actual != null) {
            size = actual.size();
        } else {
            size = 0;
        }

        this.headerlist.addAll(headerList);

    }

    public boolean recordIsEqual () {
        if (isEqual == null) {
          for (FieldTuple fieldTuple : compareRecords()) {
              if (!fieldTuple.isEqual()) {
                  isEqual = false;
                  return isEqual;
              }
          }

          isEqual = true;
        }
        return isEqual;
    }




    public List<FieldTuple> compareRecords () {
        if (!fieldTuples.isEmpty()) {
            return fieldTuples;
        }
        List<FieldTuple> fieldTuples = new ArrayList<FieldTuple>();
        for (int i=0;i<size;i++) {
            String baseValue = "";
            String actualValue = "";

            if (base != null) {
                baseValue = base.get(i);
            }

            if (actual != null) {
                actualValue =  actual.get(i);
            }

            fieldTuples.add(new FieldTuple(getColumnNameByIndex(i), baseValue, actualValue));
        }

        this.fieldTuples.addAll(fieldTuples);
        return this.fieldTuples;
    }



    private String createDeltaText (String baseValue, String actualValue) {
        return "B:" + baseValue + "|A:"+ actualValue;
    }

    private String getColumnNameByIndex (int i) {
       return headerlist.get(i);
    }



    public String getSurrogateKey() {
        return surrogateKey;
    }

    public boolean isBaseOnlyRecord () {
        return actual == null && base != null;
    }

    public boolean isActualOnlyRecord ()  {
        return base == null && actual != null;
    }

}
