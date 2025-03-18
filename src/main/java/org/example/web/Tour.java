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
    String employeeMail;
    String userMail;


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

    public String getEmployeeFk() {
        return employeeMail;
    }

    public String getUserFk() {
        return userMail;
    }

    public Tour(int id, String startDateTime, String endDateTime, int duration, String status, int badgeCode, String employeeMail, String userMail) {
        this.id = id;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.duration = duration;
        this.status = status;
        this.badgeCode = badgeCode;
        this.employeeMail = employeeMail;
        this.userMail = userMail;
    }


}
