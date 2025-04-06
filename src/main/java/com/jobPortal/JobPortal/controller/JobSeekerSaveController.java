package com.jobPortal.JobPortal.controller;

import com.jobPortal.JobPortal.entity.JobPostActivity;
import com.jobPortal.JobPortal.entity.JobSeekerProfile;
import com.jobPortal.JobPortal.entity.JobSeekerSave;
import com.jobPortal.JobPortal.entity.Users;
import com.jobPortal.JobPortal.services.JobPostActivityService;
import com.jobPortal.JobPortal.services.JobSeekerProfileService;
import com.jobPortal.JobPortal.services.JobSeekerSaveService;
import com.jobPortal.JobPortal.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class JobSeekerSaveController {

    private final UsersService usersService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerSaveService jobSeekerSaveService;

    @Autowired
    public JobSeekerSaveController(UsersService usersService, JobSeekerProfileService jobSeekerProfileService, JobPostActivityService jobPostActivityService, JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }


    @PostMapping("job-details/save/{id}")
    public String save(@PathVariable("id") int id, JobSeekerSave jobSeekerSave){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JobSeekerSave jobSeekerSave1 = new JobSeekerSave();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            Optional<Users> users = usersService.getUserByEmail(authentication.getName());
            if(users.isPresent()){
                Optional<JobSeekerProfile> jobSeekerProfile = jobSeekerProfileService.getOne(users.get().getUserId());
                if(jobSeekerProfile.isPresent()){
                    JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
                    if(jobPostActivity != null){
                        jobSeekerSave1.setJob(jobPostActivity);
                        jobSeekerSave1.setUserId(jobSeekerProfile.get());
                    }
                    jobSeekerSaveService.addNew(jobSeekerSave1);
                }
            }
        }
        return "redirect:/dashboard/";
    }


    @GetMapping("saved-jobs/")
    public String savedJobs(Model model){
        List<JobPostActivity> jobPostActivities = new ArrayList<>();
        Object currentUser =  usersService.getCurrentUserProfile();

        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUser);
        for(JobSeekerSave jobSeekerSave: jobSeekerSaveList){
            jobPostActivities.add(jobSeekerSave.getJob());
        }

        model.addAttribute("jobPost",jobPostActivities);
        model.addAttribute("user",currentUser);
        return "saved-jobs";
    }
}
