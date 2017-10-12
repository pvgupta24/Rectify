package com.rectify.judge.utils;

/**
 * Created by mohit on 13/10/17.
 */
public class HackResults {
    private String errorStatus;
    private HackStatus hackStatus;
    private String codeOutput;

    public HackResults() {

    }
    public HackResults(HackStatus hackStatus) {
        this.hackStatus = hackStatus;
        this.errorStatus = "";
        this.codeOutput = "";
    }

    public String getCodeOutput() {
        return codeOutput;
    }

    public void setCodeOutput(String codeOutput) {
        this.codeOutput = codeOutput;
    }

    public HackStatus getHackStatus() {
        return hackStatus;
    }

    public void setHackStatus(HackStatus hackStatus) {
        this.hackStatus = hackStatus;
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }
}
