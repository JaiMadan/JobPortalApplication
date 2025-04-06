package com.jobPortal.JobPortal.services;

import com.jobPortal.JobPortal.entity.JobSeekerProfile;
import com.jobPortal.JobPortal.entity.RecruiterProfile;
import com.jobPortal.JobPortal.entity.Users;
import com.jobPortal.JobPortal.repository.JobSeekerProfileRepository;
import com.jobPortal.JobPortal.repository.RecruiterProfileRepository;
import com.jobPortal.JobPortal.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UsersService {

    public UsersRepository usersRepository;
    public JobSeekerProfileRepository jobSeekerProfileRepository;
    public RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(UsersRepository usersRepository,
                        JobSeekerProfileRepository jobSeekerProfileRepository,
                        RecruiterProfileRepository recruiterProfileRepository,
                        PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users addNew(Users user){
        user.setActive(true);
        user.setRegistrationDate(new Date(System.currentTimeMillis()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Users savedUsers = usersRepository.save(user);
        if(user.getUserTypeId().getUserTypeId() == 1){
            recruiterProfileRepository.save(new RecruiterProfile(user));
        }
        else{
            jobSeekerProfileRepository.save(new JobSeekerProfile(user));
        }
        //usersRepository.save(user);
        return savedUsers;
    }

    public Optional<Users> getUserByEmail(String email){
        return usersRepository.findByEmail(email);
    }

    public Object getCurrentUserProfile() {

       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

       if(!(authentication instanceof AnonymousAuthenticationToken)){
           String username = authentication.getName();
           Users user = usersRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("Could not find username"));
           int userid = user.getUserId();
           if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))){
               return recruiterProfileRepository.findById(userid).orElse(new RecruiterProfile());
           }else{
               return jobSeekerProfileRepository.findById(userid).orElse(new JobSeekerProfile());
           }
       }
       return null;
    }

    public Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String username = authentication.getName();
            Users user = usersRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("Could not find username"));
            return user;
        }
        return null;
    }
}
