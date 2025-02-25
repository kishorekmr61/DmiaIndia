package com.dmia.bioAttendance.model;

public class DomainModel
{
    public String DisplayName;
    public String DatabaseURL;

    public DomainModel(String xDisplayName, String xDatabaseURL){
        DisplayName = xDisplayName;
        DatabaseURL = xDatabaseURL;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getDatabaseURL() {
        return DatabaseURL;
    }

    public void setDatabaseURL(String databaseURL) {
        DatabaseURL = databaseURL;
    }
}
