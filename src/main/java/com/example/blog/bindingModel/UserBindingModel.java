package com.example.blog.bindingModel;

import com.sun.istack.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class UserBindingModel {
    @NotNull
    private String email;
    @NotNull
    private String fullName;
    @NotNull
    private String password;
    @NotNull
    private String  confirmPassword;

    //new
    private MultipartFile picture;
    public byte[] getPicture() throws IOException{
        return picture.getBytes();
    }
    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }
    //till here

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
