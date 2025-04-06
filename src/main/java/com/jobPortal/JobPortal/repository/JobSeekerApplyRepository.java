package com.jobPortal.JobPortal.repository;

import com.jobPortal.JobPortal.entity.JobPostActivity;
import com.jobPortal.JobPortal.entity.JobSeekerApply;
import com.jobPortal.JobPortal.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {

    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);


}
