package ua.nau.edu.University;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by gaponec on 20.09.15.
 */
public abstract class University {
    private String nameAbbreviation;
    private String fullName;

    private int hashMapSize = -1;
    private int corpsNum = -1;

    private HashMap<Integer, LatLng> Corps;
    private HashMap<Integer, Integer> CorpsIcon;
    private HashMap<Integer, Integer> CorpsGerb;
    private HashMap<Integer, Set> LectureHalls;
    private HashMap<Integer, String> CorpsMarkerLabel;
    private HashMap<Integer, String> CorpsLabel;

    private HashMap<Integer, String> CorpsInfoNameShort;
    private HashMap<Integer, String> CorpsInfoNameFull;
    private HashMap<Integer, String> CorpsInfoPhone;
    private HashMap<Integer, String> CorpsInfoUrl;

    public University() {

    }

    public University(String fullName, String nameAbbreviation) {
        this.fullName = fullName;
        this.nameAbbreviation = nameAbbreviation;
    }

    /**
     * SETTERS
     **/
    public void setNameAbbreviation(String nameAbbreviation) {
        this.nameAbbreviation = nameAbbreviation;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setCorps(HashMap<Integer, LatLng> corps) {
        this.Corps = corps;
    }

    public void setCorpsIcon(HashMap<Integer, Integer> corpsIcon) {
        this.CorpsIcon = corpsIcon;
    }

    public void setCorpsGerb(HashMap<Integer, Integer> corpsGerb) {
        this.CorpsGerb = corpsGerb;
    }

    public void setCorpsMarkerLabel(HashMap<Integer, String> corpsMarkerLabel) {
        this.CorpsMarkerLabel = corpsMarkerLabel;
    }

    public void setCorpsLabel(HashMap<Integer, String> corpsLabel) {
        this.CorpsLabel = corpsLabel;
    }

    public void setCorpsInfoNameShort(HashMap<Integer, String> corpsInfoNameShort) {
        this.CorpsInfoNameShort = corpsInfoNameShort;
    }

    public void setCorpsInfoNameFull(HashMap<Integer, String> corpsInfoNameFull) {
        this.CorpsInfoNameFull = corpsInfoNameFull;
    }

    public void setCorpsInfoPhone(HashMap<Integer, String> corpsInfoPhone) {
        this.CorpsInfoPhone = corpsInfoPhone;
    }

    public void setCorpsInfoUrl(HashMap<Integer, String> corpsInfoUrl) {
        this.CorpsInfoUrl = corpsInfoUrl;
    }

    public void setLectureHalls(HashMap<Integer, Set> lectureHalls) {
        this.LectureHalls = lectureHalls;
    }

    public void setHashMapSize(int size) {
        this.hashMapSize = size;
    }

    public void setCorpsNum(int size) {
        this.corpsNum = size;
    }

    /**
     * GETTERS
     **/
    public String getNameAbbreviation() {
        return nameAbbreviation;
    }

    public String getFullName() {
        return fullName;
    }

    public HashMap<Integer, LatLng> getCorps() {
        return Corps;
    }

    public HashMap<Integer, Integer> getCorpsIcon() {
        return CorpsIcon;
    }

    public HashMap<Integer, Integer> getCorpsGerb() {
        return CorpsGerb;
    }

    public HashMap<Integer, String> getCorpsMarkerLabel() {
        return CorpsMarkerLabel;
    }

    public HashMap<Integer, String> getCorpsLabel() {
        return CorpsLabel;
    }

    public HashMap<Integer, String> getCorpsInfoNameShort() {
        return CorpsInfoNameShort;
    }

    public HashMap<Integer, String> getCorpsInfoNameFull() {
        return CorpsInfoNameFull;
    }

    public HashMap<Integer, String> getCorpsInfoPhone() {
        return CorpsInfoPhone;
    }

    public HashMap<Integer, String> getCorpsInfoUrl() {
        return CorpsInfoUrl;
    }

    public HashMap<Integer, Set> getLectureHalls() {
        return LectureHalls;
    }

    public int getHashMapSize() {
        return hashMapSize;
    }

    public int getCopsNum() {
        return corpsNum;
    }
}
