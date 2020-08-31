package com.example.blog.controller;

import com.example.blog.bindingModel.UserBindingModel;
import com.example.blog.entity.Article;
import com.example.blog.entity.Role;
import com.example.blog.entity.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.RoleRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Console;
import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class UserController {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    //new added article repo
    private final ArticleRepository articleRepository;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService,
                          RoleRepository roleRepository,
                          UserRepository userRepository,
                          //new added repository
                          ArticleRepository articleRepository) {

        this.userService = userService;
        this.userRepository =userRepository;
        this.roleRepository = roleRepository;
        this.articleRepository = articleRepository;
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("view", "user/login");
        return "base-layout";
    }

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("view","user/register");
        return "base-layout";
    }

    @PostMapping("/register")
    public String registerProcess(UserBindingModel userBindingModel) throws IOException {

        if (!userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())) {
            return "redirect:/register";
        }
        this.userService.create(userBindingModel);

        return "redirect:/login";
    }

    @RequestMapping(value="/logout",method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth != null){
            new SecurityContextLogoutHandler().logout(request,response,auth);
        }
        return "redirect:/login?logout";
    }


    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profilePage(Model model){
        User user = this.userService.getCurrentUser();
        if(user.getProfilePicture() != null){
            user.setProfilePictureBase64(Base64.getEncoder().encodeToString(user.getProfilePicture()));
        }
        model.addAttribute("user", user);
        model.addAttribute("view", "user/profile");
        return "base-layout";
    }

    @GetMapping("/user/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(Model model) throws NotFoundException {
        User user = this.userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("view", "user/edit");
        return "base-layout";
    }

    @PostMapping("/user/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProcess(@PathVariable Integer id,UserBindingModel userBindingModel) throws IOException, NotFoundException {
        User user = this.userService.getById(id);
        this.userService.edit(user, userBindingModel);
        return "redirect:/";
    }




    //new too
    /*@GetMapping("/myarticles")
    @PreAuthorize("isAuthenticated()")
    public String listMyArticles(Model model){
        User user = this.userService.getCurrentUser();

        Set<Article> articles = new HashSet<>();

        int id =1;
        for(;id<=articleRepository.count();id++) {

            Article article = this.articleRepository.findById(id).orElse(null);

            if(isUserAuthor(article)) {
                    articles.add(article);
            }
        }
        model.addAttribute("articles", articles);
        model.addAttribute("user", user);
        model.addAttribute("view", "user/myarticles");
        return "base-layout";
    }
    */

    @GetMapping("/myarticles")
    @PreAuthorize("isAuthenticated()")
    public String listMyArticles(Model model){
        User user = this.userService.getCurrentUser();

        Set<Article> articles = user.getArticles();

        model.addAttribute("articles", articles);
        model.addAttribute("user", user);
        model.addAttribute("view", "user/myarticles");
        return "base-layout";
    }





    //new method
    private boolean isUserAuthor(Article article){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User userEntity = this.userRepository.findByEmail(user.getUsername());

        return userEntity.isAuthor(article);
    }

}
