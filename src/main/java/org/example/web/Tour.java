package org.example.web;

import io.quarkus.qute.TemplateData;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Date;

@TemplateData
public class Tour {
    int id;
    String startDateTime;
    String endDateTime;
    int duration;
    String status;
    int badgeCode;
    int employeeFk;
    int userFk;


    public int getId() {
        return id;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public int getDuration() {
        return duration;
    }

    public String getStatus() {
        return status;
    }

    public int getBadgeCode() {
        return badgeCode;
    }

    public int getEmployeeFk() {
        return employeeFk;
    }

    public int getUserFk() {
        return userFk;
    }

    public Tour(int id, String startDateTime, String endDateTime, int duration, String status, int badgeCode, int employeeFk, int userFk) {
        this.id = id;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.duration = duration;
        this.status = status;
        this.badgeCode = badgeCode;
        this.employeeFk = employeeFk;
        this.userFk = userFk;
    }


}
