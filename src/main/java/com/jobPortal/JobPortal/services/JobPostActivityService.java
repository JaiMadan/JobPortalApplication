package com.jobPortal.JobPortal.services;

import com.jobPortal.JobPortal.entity.*;
import com.jobPortal.JobPortal.repository.JobPostActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class JobPostActivityService {

    private final  JobPostActivityRepository jobPostActivityRepository;

    @Autowired
    public JobPostActivityService(JobPostActivityRepository jobPostActivityRepository) {
        this.jobPostActivityRepository = jobPostActivityRepository;
    }

    public JobPostActivity addNew(JobPostActivity jobPostActivity){
        return jobPostActivityRepository.save(jobPostActivity);
    }

    public List<RecruiterJobsDto> getRecruiterJobs(Integer userid){
         List<IRecruiterJobs> recruiterJobsList = jobPostActivityRepository.getRecruiterJobs(userid);

         List<RecruiterJobsDto> data = new ArrayList<>();

         for(IRecruiterJobs ice: recruiterJobsList){
             JobLocation loc = new JobLocation(ice.getLocationId(),ice.getCity(),ice.getState(),ice.getCountry());
             JobCompany comp = new JobCompany(ice.getCompanyId(),ice.getName(),"");
             data.add(new RecruiterJobsDto(ice.getTotalCandidates(),ice.getJob_post_id(),ice.getJob_title(),loc,comp));
         }
         return data;
    }

    public JobPostActivity getOne(int id) {
        return jobPostActivityRepository.findById(id).orElseThrow(()->new RuntimeException("Job not found"));
    }

    public List<JobPostActivity> getAll() {
        return jobPostActivityRepository.findAll();
    }

    public List<JobPostActivity> searchjob(String job, String location, List<String> type, List<String> remote, LocalDate searchDate) {
        return Objects.isNull(searchDate)?jobPostActivityRepository.searchWithoutDate(job,location,remote,type) :
                jobPostActivityRepository.search(job,location,remote,type,searchDate);
    }
}
