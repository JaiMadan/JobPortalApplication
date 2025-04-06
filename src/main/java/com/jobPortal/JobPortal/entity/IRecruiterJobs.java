package com.jobPortal.JobPortal.entity;

public interface IRecruiterJobs {

    Long getTotalCandidates();

    int getJob_post_id();

    String getJob_title();

    int getLocationId();

    String getCity();

    String getCountry();

    String getState();

    int getCompanyId();

    String getName();
}
