package com.rectify.judge.utils;

public class SubmissionResults {
    private SubmissionStatus submissionStatus;
    private String errorStatus;
    private String codeOutput;
    
    public String getCodeOutput() {
        return codeOutput;
    }

    public void setCodeOutput(String codeOutput) {
        this.codeOutput = codeOutput;
    }

    public void setSubmissionStatus(String codeOutput) {
        this.codeOutput = codeOutput;
    }

    // JAXB needs no-param constructor
    public SubmissionResults(){}

    public SubmissionStatus getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(SubmissionStatus submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }
}
