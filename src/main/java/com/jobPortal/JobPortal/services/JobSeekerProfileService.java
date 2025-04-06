package com.jobPortal.JobPortal.services;

import com.jobPortal.JobPortal.entity.JobSeekerProfile;
import com.jobPortal.JobPortal.repository.JobSeekerProfileRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobSeekerProfileService {

    private final JobSeekerProfileRepository jobSeekerProfileRepository;

    @Autowired
    public JobSeekerProfileService(JobSeekerProfileRepository jobSeekerProfileRepository) {
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
    }


    public Optional<JobSeekerProfile> getOne(Integer id){
          return jobSeekerProfileRepository.findById(id);
    }

    public void addNew(JobSeekerProfile jobSeekerProfile) {
        jobSeekerProfileRepository.save(jobSeekerProfile);
    }
}
