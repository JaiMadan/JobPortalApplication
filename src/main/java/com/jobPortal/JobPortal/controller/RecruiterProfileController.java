package com.jobPortal.JobPortal.controller;

import ch.qos.logback.core.util.StringUtil;
import com.jobPortal.JobPortal.entity.RecruiterProfile;
import com.jobPortal.JobPortal.entity.Users;
import com.jobPortal.JobPortal.repository.UsersRepository;
import com.jobPortal.JobPortal.services.RecruiterProfileService;
import com.jobPortal.JobPortal.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

    private final UsersRepository usersRepository;
    private final RecruiterProfileService recruiterProfileService;

    @Autowired
    public RecruiterProfileController(UsersRepository usersRepository, RecruiterProfileService recruiterProfileService) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileService;
    }

    @GetMapping("/")
    public String recruiterProfile(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String username = authentication.getName();
            Users users = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Could not find User with the given Username"));
            Optional<RecruiterProfile> profile = recruiterProfileService.getOne(users.getUserId());

            profile.ifPresent(recruiterProfile -> model.addAttribute("profile", recruiterProfile));
        }
        return "recruiter_profile";

    }


    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile, @RequestParam("image")MultipartFile multipartFile, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String username = authentication.getName();
            Users users = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Could not find User with the given Username"));

            recruiterProfile.setUserId(users);
            recruiterProfile.setUserAccountId(users.getUserId());
        }
        model.addAttribute("profile",recruiterProfile);
        String fileName = "";
        if(!Objects.equals(multipartFile.getOriginalFilename(), "")){
             fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
             recruiterProfile.setProfilePhoto(fileName);
        }
        RecruiterProfile recruiterProfile1 = recruiterProfileService.addNew(recruiterProfile);

        String uploadDir = "photos/recruiter/"+recruiterProfile1.getUserAccountId();

        try{
            FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return "redirect:/dashboard/";
    }
}
