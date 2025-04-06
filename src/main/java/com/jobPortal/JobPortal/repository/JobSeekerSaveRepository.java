package com.jobPortal.JobPortal.repository;

import com.jobPortal.JobPortal.entity.JobPostActivity;
import com.jobPortal.JobPortal.entity.JobSeekerProfile;
import com.jobPortal.JobPortal.entity.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {

    public List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);

    List<JobSeekerSave> findByJob(JobPostActivity job);
}
