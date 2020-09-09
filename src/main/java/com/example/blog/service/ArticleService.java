package com.example.blog.service;

import com.example.blog.bindingModel.ArticleBindingModel;
import com.example.blog.entity.Article;
import com.example.blog.entity.User;

import java.io.IOException;

public interface ArticleService {
    Article getCurrentArticle(Integer id);

    Article create(ArticleBindingModel articleBindingModel) throws IOException;

    Article edit(Article article, ArticleBindingModel articleBindingModel) throws IOException;

    void delete(Article article);
}
