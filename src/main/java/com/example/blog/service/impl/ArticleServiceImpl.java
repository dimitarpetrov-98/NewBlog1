package com.example.blog.service.impl;

import com.example.blog.bindingModel.ArticleBindingModel;
import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.entity.Tag;
import com.example.blog.entity.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.TagRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.HashSet;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, UserRepository userRepository, TagRepository tagRepository, CategoryRepository categoryRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Article getCurrentArticle(@PathVariable Integer id) {
        Article article = this.articleRepository.findArticleById(id);
        return article;
    }


    @Override
    public Article create(ArticleBindingModel bindingModel) throws IOException {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User userEntity = this.userRepository.findByEmail(user.getUsername());
        Category category = this.categoryRepository.findById(bindingModel.getCategoryId()).orElse(null);
        HashSet<Tag> tags = this.findTagsFromString(bindingModel.getTagString());

        Article articleEntity = new Article(
                bindingModel.getTitle(),
                bindingModel.getContent(),
                userEntity,
                category,
                tags
        );

        if (bindingModel.getPicture() != null){
            byte[] picture = bindingModel.getPicture().getBytes();
            articleEntity.setPicture(picture);
        }

        return this.articleRepository.saveAndFlush(articleEntity);
    }


    @Override
    public Article edit(Article article, ArticleBindingModel articleBindingModel) throws IOException {

        if(!this.articleRepository.existsById(article.getId())){
            //was return "redirect:/";
            //changed to
            return null;
        }
        //added orElse(null)
        if(isUserAuthorOrAdmin(article)){
            //was return "redirect:/article" + id;
            //changed to
            return null;
        }
        Category category = this.categoryRepository.findById(articleBindingModel.getCategoryId()).orElse(null);

        HashSet<Tag> tags = this.findTagsFromString(articleBindingModel.getTagString());

        article.setContent(articleBindingModel.getContent());
        article.setTitle(articleBindingModel.getTitle());
        article.setCategory(category);
        article.setTags(tags);

        //new
        if (articleBindingModel.getPicture() != null){
            byte[] picture = articleBindingModel.getPicture().getBytes();
            article.setPicture(picture);
        }
        //end

        return this.articleRepository.saveAndFlush(article);
    }


    public void delete(Article article){
        if(!this.articleRepository.existsById(article.getId())){
            return;
        }
        if(isUserAuthorOrAdmin(article)){
            return;
        }
        this.articleRepository.delete(article);
    }

    private HashSet<Tag> findTagsFromString(String tagString){

        HashSet<Tag> tags = new HashSet<>();
        String[] tagNames = tagString.split(",\\s*");

        for(String tagName: tagNames){
            Tag currentTag = this.tagRepository.findByName(tagName);

            if(currentTag == null){
                currentTag = new Tag(tagName);
                this.tagRepository.saveAndFlush(currentTag);
            }
            tags.add(currentTag);
        }
        return tags;
    }
    private boolean isUserAuthorOrAdmin(Article article){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User userEntity = this.userRepository.findByEmail(user.getUsername());

        return (!userEntity.isAdmin() && !userEntity.isAuthor(article));
    }
}
