package com.jobPortal.JobPortal.services;

import com.jobPortal.JobPortal.entity.JobPostActivity;
import com.jobPortal.JobPortal.entity.JobSeekerApply;
import com.jobPortal.JobPortal.entity.JobSeekerProfile;
import com.jobPortal.JobPortal.repository.JobSeekerApplyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerApplyService {

    private final JobSeekerApplyRepository jobSeekerApplyRepository;
    @PersistenceContext
    private final EntityManager em;
    @Autowired
    public JobSeekerApplyService(JobSeekerApplyRepository jobSeekerApplyRepository, EntityManager em) {
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
        this.em = em;
    }

    public List<JobSeekerApply> getCandidatesJob(JobSeekerProfile userAccountId){
        return jobSeekerApplyRepository.findByUserId(userAccountId);
    }

    public List<JobSeekerApply> getJobCandidates(JobPostActivity job){
        return jobSeekerApplyRepository.findByJob(job);
    }

    @Transactional
    public void addNew(JobSeekerApply jobSeekerApply) {
        em.merge(jobSeekerApply);
    }
}
