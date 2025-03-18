package org.example.web;

import io.quarkus.qute.TemplateData;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Date;

@TemplateData
public class Tour {
    Integer id;
    String startDateTime;
    String endDateTime;
    int duration;
    String status;
    int employeeFk;
    int userFk;


    public Integer getId() {
        return id;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public int getDuration() { return duration; }

    public String getStatus() { return status; }

    public int getEmployeeFk() {
        return employeeFk;
    }

    public int getUserFk() {
        return userFk;
    }

    public Tour(int id, String startDateTime, String endDateTime, int duration, String status, int employeeFk, int userFk) {
        this.id = id;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.duration = duration;
        this.status = status;
        this.employeeFk = employeeFk;
        this.userFk = userFk;
    }


}
