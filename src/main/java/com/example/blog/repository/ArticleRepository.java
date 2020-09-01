package com.example.blog.repository;

import com.example.blog.entity.Article;
import com.example.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    Article findArticleByIdAndAuthor(Integer id, User user);
}
