package techcourse.myblog.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import techcourse.myblog.application.dto.ArticleDto;
import techcourse.myblog.application.dto.CommentDto;
import techcourse.myblog.application.service.ArticleService;
import techcourse.myblog.application.service.CommentService;

import java.util.List;

@Controller
public class ArticleController {
    private final ArticleService articleService;
    private final CommentService commentService;

    @Autowired
    public ArticleController(final ArticleService articleService, CommentService commentService) {
        this.articleService = articleService;
        this.commentService = commentService;
    }

    @PostMapping("/articles")
    public RedirectView createArticles(ArticleDto article) {
        Long id = articleService.save(article);

        return new RedirectView("/articles/" + id);
    }

    @GetMapping("/articles/{articleId}")
    public ModelAndView readArticlePageByArticleId(@PathVariable Long articleId) {
        ModelAndView modelAndView = new ModelAndView("/article");
        modelAndView.addObject("article", articleService.findById(articleId));

        List<CommentDto> commentDtos = commentService.findAllByArticleId(articleId);
        modelAndView.addObject("comments", commentDtos);
        return modelAndView;
    }

    @GetMapping("/articles/{articleId}/edit")
    public ModelAndView readArticleEditPage(@PathVariable Long articleId) {
        ModelAndView modelAndView = new ModelAndView("/article-edit");
        modelAndView.addObject("article", articleService.findById(articleId));
        modelAndView.addObject("method", "put");
        return modelAndView;
    }

    @PutMapping("/articles/{articleId}")
    public RedirectView updateArticle(@PathVariable Long articleId, ArticleDto article) {
        RedirectView redirectView = new RedirectView("/articles/" + articleId);
        articleService.modify(articleId, article);
        return redirectView;
    }

    @DeleteMapping("/articles/{articleId}")
    public RedirectView deleteArticle(@PathVariable Long articleId) {
        RedirectView redirectView = new RedirectView("/");
        articleService.removeById(articleId);
        return redirectView;
    }
}