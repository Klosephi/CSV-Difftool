package org.pk.diff;

public class FieldTuple {

    private final String columnname;
    private final String base;
    private final String actual;


    public FieldTuple(String columnname, String base, String actual) {
        this.columnname = columnname;
        this.base = base;
        this.actual = actual;
    }

    public String getColumnname() {
        return columnname;
    }

    public String getBase() {
        return base;
    }

    public String getActual() {
        return actual;
    }

    public boolean isEqual () {
        if (base != null && actual != null) {
            return base.equals(actual);
        } else {
            return false;
        }
    }
}
