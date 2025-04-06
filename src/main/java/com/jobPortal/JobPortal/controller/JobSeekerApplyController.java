package com.jobPortal.JobPortal.controller;

import com.jobPortal.JobPortal.entity.*;
import com.jobPortal.JobPortal.services.JobPostActivityService;
import com.jobPortal.JobPortal.services.JobSeekerApplyService;
import com.jobPortal.JobPortal.services.JobSeekerSaveService;
import com.jobPortal.JobPortal.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class JobSeekerApplyController {

    private final JobPostActivityService jobPostActivityService;
    private final UsersService usersService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;

    @Autowired
    public JobSeekerApplyController(JobPostActivityService jobPostActivityService, UsersService usersService, JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService) {
        this.jobPostActivityService = jobPostActivityService;
        this.usersService = usersService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @GetMapping("job-details-apply/{id}")
    public String display(@PathVariable("id") int id, Model model){
        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
        List<JobSeekerApply> jobApplyList = jobSeekerApplyService.getJobCandidates(jobPostActivity);
        List<JobSeekerSave> jobSaveList = jobSeekerSaveService.getJobCandidates(jobPostActivity);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AnonymousAuthenticationToken)){
            if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))){
                RecruiterProfile recruiterProfile = (RecruiterProfile) usersService.getCurrentUserProfile();
                if(recruiterProfile!=null){
                    model.addAttribute("applyList",jobApplyList);
                }
            } else{
                JobSeekerProfile jobSeekerProfile = (JobSeekerProfile) usersService.getCurrentUserProfile();
                if(jobSeekerProfile!=null){
                    boolean exists = false;
                    boolean saved = false;
                    for(JobSeekerApply jobSeekerApply: jobApplyList){
                        if(jobSeekerApply.getUserId().getUserAccountId() == jobSeekerProfile.getUserAccountId()){
                            exists = true;
                            break;
                        }
                    }
                    for(JobSeekerSave jobSeekerSave: jobSaveList){
                        if(jobSeekerSave.getUserId().getUserAccountId() == jobSeekerProfile.getUserAccountId()){
                            saved = true;
                            break;
                        }
                    }
                    model.addAttribute("alreadyApplied",exists);
                    model.addAttribute("alreadySaved",saved);
                }
            }
        }

        JobSeekerApply jobSeekerApply = new JobSeekerApply();
        model.addAttribute("applyJob",jobSeekerApply);

        model.addAttribute("jobDetails",jobPostActivity);
        model.addAttribute("user",usersService.getCurrentUserProfile());

        return "job-details";
    }

    @PostMapping("dashboard/edit/{id}")
    public String editaJob(@PathVariable("id") int id, Model model){
        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
        model.addAttribute("jobPostActivity",jobPostActivity);
        model.addAttribute("user",usersService.getCurrentUserProfile());
        return "add-jobs";
    }

    @PostMapping("job-details/apply/{id}")
    public String apply(@PathVariable("id") int id, JobSeekerApply jobSeekerApply){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String name = authentication.getName();
            Object user = usersService.getCurrentUserProfile();
            if(user != null){
                JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
                if(jobPostActivity != null){
                    jobSeekerApply = new JobSeekerApply();
                    jobSeekerApply.setUserId((JobSeekerProfile) user);
                    jobSeekerApply.setJob(jobPostActivity);
                    jobSeekerApply.setApplyDate(new Date());
                } else{
                    throw new RuntimeException("User Not Found");
                }
                jobSeekerApplyService.addNew(jobSeekerApply);
            }
        }

        return "redirect:/dashboard/";
    }
}
