package com.vh.ds.classutils.model;

public class Field {

    private int index;

    private String originalName;

    private String fieldName;

    private DataType fieldType;

    public Field(int index, String originalName, String fieldName, DataType fieldType) {
        this.index = index;
        this.originalName = originalName;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public DataType getFieldType() {
        return fieldType;
    }

    public void setFieldType(DataType fieldType) {
        this.fieldType = fieldType;
    }
}
