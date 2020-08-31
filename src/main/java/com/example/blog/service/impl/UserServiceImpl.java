package com.example.blog.service.impl;

import com.example.blog.bindingModel.UserBindingModel;
import com.example.blog.entity.Role;
import com.example.blog.entity.User;
import com.example.blog.repository.RoleRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User getCurrentUser() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User userEntity = this.userRepository.findByEmail(user.getUsername());

        return userEntity;
    }

    @Override
    public User getById(Integer id) throws NotFoundException {
        User user = this.userRepository.getOne(id);
        if (user == null){
            throw new NotFoundException("Invalid USER!");
        }
        return user;
    }

    @Override
    public User create(UserBindingModel bindingModel) throws IOException {
        Role userRole = this.roleRepository.findByName("USER");
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        User user = new User(
                bindingModel.getEmail(),
                bindingModel.getFullName(),
                bCryptPasswordEncoder.encode(bindingModel.getPassword()));

        if (bindingModel.getProfilePicture() != null){
            byte[] profilePicture = bindingModel.getProfilePicture().getBytes();
            user.setProfilePicture(profilePicture);
        }

        user.addRole(userRole);

        return this.userRepository.saveAndFlush(user);
    }

    @Override
    public User edit(User user, UserBindingModel userBindingModel) throws IOException {
        if (!StringUtils.isEmpty(userBindingModel.getPassword())
                && !StringUtils.isEmpty(userBindingModel.getConfirmPassword())){

            if (userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())){
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

                user.setPassword(bCryptPasswordEncoder.encode(userBindingModel.getPassword()));
            }
        }

        //new
        if (userBindingModel.getProfilePicture() != null){
            byte[] profilePicture = userBindingModel.getProfilePicture().getBytes();
            user.setProfilePicture(profilePicture);
        }
        //end

        user.setFullName(userBindingModel.getFullName());
        user.setEmail(userBindingModel.getEmail());

        /*if (userBindingModel.getProfilePicture() != null) {
            user.setProfilePicture(userBindingModel.getProfilePicture().getBytes());
        }

        if(user.getProfilePicture() != null){
            user.setProfilePictureBase64(Base64.getEncoder().encodeToString(user.getProfilePicture()));
        }*/

        this.userRepository.saveAndFlush(user);
        return user;
    }

    @Override
    public void delete(User user) {
        this.userRepository.delete(user);
    }

    @Override
    public List<User> getAll() {
        return this.userRepository.findAllByOrderByIdAsc();
    }
}
