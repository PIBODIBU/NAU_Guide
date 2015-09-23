package ua.nau.edu.University;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by gaponec on 20.09.15.
 */
public abstract class University {
    private String nameAbbreviation;

    private String fullName;

    private HashMap<Integer, LatLng> Corps;
    private HashMap<Integer, Set> lectureHalls;

    public University() {

    }

    public University(String fullName, String nameAbbreviation) {
        this.fullName = fullName;
        this.nameAbbreviation = nameAbbreviation;
    }

    //SETTERS
    public void setNameAbbreviation(String nameAbbreviation) {
        this.nameAbbreviation = nameAbbreviation;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setCorps(HashMap<Integer, LatLng> corps) {
        Corps = corps;
    }

    public void setLectureHalls(HashMap<Integer, Set> lectureHalls) {
        this.lectureHalls = lectureHalls;
    }

    //GETTERS
    public String getNameAbbreviation() {
        return nameAbbreviation;
    }

    public String getFullName() {
        return fullName;
    }

    public HashMap<Integer, LatLng> getCorps() {
        return Corps;
    }

    public HashMap<Integer, Set> getLectureHalls() {
        return lectureHalls;
    }
}
