package com.jobPortal.JobPortal.controller;


import com.jobPortal.JobPortal.entity.JobSeekerProfile;
import com.jobPortal.JobPortal.entity.Skills;
import com.jobPortal.JobPortal.entity.Users;
import com.jobPortal.JobPortal.repository.UsersRepository;
import com.jobPortal.JobPortal.services.JobSeekerProfileService;
import com.jobPortal.JobPortal.util.FileDownloadUtil;
import com.jobPortal.JobPortal.util.FileUploadUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/job-seeker-profile")
public class JobSeekerProfileController {

    private final JobSeekerProfileService jobSeekerProfileService;
    private final UsersRepository usersRepository;

    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService, UsersRepository usersRepository) {
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.usersRepository = usersRepository;
    }

    @GetMapping("/")
    public String JobSeekerProfile(Model model){

        JobSeekerProfile jobSeekerProfile = new JobSeekerProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Skills> skills = new ArrayList<>();

        if(!(authentication instanceof AnonymousAuthenticationToken)){
          Users users =  usersRepository.findByEmail(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("Could not find user"));
            Optional<JobSeekerProfile> one = jobSeekerProfileService.getOne(users.getUserId());
            if(one.isPresent()){
                jobSeekerProfile = one.get();
                if(jobSeekerProfile.getSkills().isEmpty()){
                    skills.add(new Skills());
                    jobSeekerProfile.setSkills(skills);
                }
            }
            model.addAttribute("skills",skills);
            model.addAttribute("profile",jobSeekerProfile);
        }


        return "job-seeker-profile";
    }


    @PostMapping("/addNew")
    public String addNew(JobSeekerProfile jobSeekerProfile,
                         @RequestParam("image") MultipartFile image,
                         @RequestParam("pdf") MultipartFile pdf,
                         Model model) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AnonymousAuthenticationToken)){
            Users users =  usersRepository.findByEmail(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("Could not find user"));
            jobSeekerProfile.setUserId(users);
            jobSeekerProfile.setUserAccountId(users.getUserId());
        }

        List<Skills> skillsSet = new ArrayList<>();
        model.addAttribute("profile",jobSeekerProfile);
        model.addAttribute("skills",skillsSet);

        for(Skills skills: jobSeekerProfile.getSkills()){
            skills.setJobSeekerProfile(jobSeekerProfile);
        }

        String imageName="";
        String resumeName="";

        if(!Objects.equals(image.getOriginalFilename(),"")){
            imageName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            jobSeekerProfile.setProfilePhoto(imageName);
        }

        if(!Objects.equals(pdf.getOriginalFilename(),"")){
            resumeName = StringUtils.cleanPath(Objects.requireNonNull(pdf.getOriginalFilename()));
            jobSeekerProfile.setResume(resumeName);
        }

        jobSeekerProfileService.addNew(jobSeekerProfile);

        try {
            String uploadDir = "photos/candidate/"+jobSeekerProfile.getUserAccountId();
            if(!Objects.equals(image.getOriginalFilename(),"")){
                FileUploadUtil.saveFile(uploadDir,imageName,image);
            }
            if(!Objects.equals(pdf.getOriginalFilename(),"")){
                FileUploadUtil.saveFile(uploadDir,resumeName,pdf);
            }
        }catch (IOException ex){
            throw new RuntimeException(ex);
        }


        return  "redirect:/dashboard/";

    }

    @GetMapping("/{id}")
    public String candidateProfile(@PathVariable("id") int id, Model model){
        Optional<JobSeekerProfile> jobSeekerProfile = jobSeekerProfileService.getOne(id);
        jobSeekerProfile.ifPresent(seekerProfile -> model.addAttribute("profile", seekerProfile));
        return "job-seeker-profile";
    }

    @GetMapping("/downloadResume")
    public ResponseEntity<?> downloadResume(@RequestParam(value = "fileName") String fileName, @RequestParam(value = "userID") String userId){
         FileDownloadUtil fileDownloadUtil = new FileDownloadUtil();
         Resource resource = null;

         try {
             resource = fileDownloadUtil.getFileAsResource("photos/candidate/"+userId,fileName);

         } catch (IOException io){
             return ResponseEntity.badRequest().build();
         }

         if(resource==null){
             return new ResponseEntity<>("File Not Found",HttpStatus.NOT_FOUND);
         }

         String contentType = "application/octet-stream";

         String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

         return  ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                 .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                 .body(resource);
    }


}
