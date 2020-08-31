package com.example.blog.service;

import com.example.blog.bindingModel.UserBindingModel;
import com.example.blog.entity.User;
import javassist.NotFoundException;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User getCurrentUser();
    User getById(Integer id) throws NotFoundException;

    User create(UserBindingModel bindingModel) throws IOException;
    User edit(User user, UserBindingModel userBindingModel) throws IOException;
    void delete(User user);

    List<User> getAll();
}
