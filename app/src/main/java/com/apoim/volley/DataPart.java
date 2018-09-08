package com.apoim.volley;

// Simple data container use for passing byte file
public class DataPart {
    private String fileName;
    private byte[] content;
    private String type;

    // Default Constructor
    public DataPart() {
    }

    // Constructor with data
    public DataPart(String name, byte[] data) {
        fileName = name;
        content = data;
    }

    // Constructor with mime data type
    // mimeType mime data like "image/jpeg"
    public DataPart(String name, byte[] data, String mimeType) {
        fileName = name;
        content = data;
        type = mimeType;
    }

    // Get file name
    public String getFileName() {
        return fileName;
    }

    // Set file name
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    // Get content
    public byte[] getContent() {
        return content;
    }

    // Set content
    public void setContent(byte[] content) {
        this.content = content;
    }

    // Get mime type
    public String getType() {
        return type;
    }

    //Set mime type
    public void setType(String type) {
        this.type = type;
    }
}

