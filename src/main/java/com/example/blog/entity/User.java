package com.example.blog.entity;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    private Integer id;
    private String email;
    private String fullName;
    private String password;
    private Set<Role> roles;
    private Set<Article> articles;

    //new added
    private byte[] profilePicture;
    //till here


    public User(String email, String fullName, String password, byte[] picture) throws IOException {
        this.email = email;
        this.password = password;
        this.fullName = fullName;

        this.profilePicture = picture;

        this.roles = new HashSet<>();
        this.articles = new HashSet<>();
    }
    public User(){ }

    public User(String email, String fullName, String password) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;

        this.roles = new HashSet<>();
        this.articles = new HashSet<>();
    }

    @Column(name="picture")
    public byte[] getProfilePicture() { return profilePicture;   }
    public void setProfilePicture(byte[] profilePicture) { this.profilePicture = profilePicture; }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "email", unique = true, nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "fullName", nullable = false)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "password", length = 60, nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles")
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role){
        this.roles.add(role);
    }

    @OneToMany(mappedBy = "author")
    public Set<Article> getArticles() {
        return articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }

    @Transient
    public boolean isAdmin(){
        return this.getRoles()
                .stream()
                .anyMatch(role->role.getName().equals("ROLE_ADMIN"));
    }
    @Transient
    public boolean isAuthor(Article article){
        return Objects.equals(this.getId(),
                article.getAuthor().getId());
    }
}
