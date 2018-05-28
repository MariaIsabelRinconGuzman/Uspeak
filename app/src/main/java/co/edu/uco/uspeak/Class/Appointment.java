package co.edu.uco.uspeak.Class;

/**
 * Created by Isabel on 11/03/2018.
 */

public class Appointment {
    private String date;
    private String time;
    private int qualification;
    private String user_creator;
    private String user_receptor;
    private String status;
    private String name_creator;
    private String name_receptor;

    public Appointment(){}

    public Appointment(String date, String time, int qualification, String user_creator, String user_receptor, String status, String name_creator, String name_receptor) {
        this.date = date;
        this.time = time;
        this.qualification = qualification;
        this.user_creator = user_creator;
        this.user_receptor = user_receptor;
        this.status = status;
        this.name_creator = name_creator;
        this.name_receptor = name_receptor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getQualification() {
        return qualification;
    }

    public void setQualification(int qualification) {
        this.qualification = qualification;
    }

    public String getUser_creator() {
        return user_creator;
    }

    public void setUser_creator(String user_creator) {
        this.user_creator = user_creator;
    }

    public String getUser_receptor() {
        return user_receptor;
    }

    public void setUser_receptor(String user_receptor) {
        this.user_receptor = user_receptor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName_creator() {
        return name_creator;
    }

    public void setName_creator(String name_creator) {
        this.name_creator = name_creator;
    }

    public String getName_receptor() {
        return name_receptor;
    }

    public void setName_receptor(String name_receptor) {
        this.name_receptor = name_receptor;
    }
}
