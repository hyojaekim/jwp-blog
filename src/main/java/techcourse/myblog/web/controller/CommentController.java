package techcourse.myblog.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import techcourse.myblog.domain.Article;
import techcourse.myblog.domain.Comment;
import techcourse.myblog.dto.CommentDto;
import techcourse.myblog.service.ArticleReadService;
import techcourse.myblog.service.CommentService;
import techcourse.myblog.web.support.SessionUser;

import java.util.List;

@Controller
@RequestMapping("/articles/{articleId}/comments")
public class CommentController {
    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;
    private final ArticleReadService articleReadService;

    public CommentController(CommentService commentService, ArticleReadService articleReadService) {
        this.commentService = commentService;
        this.articleReadService = articleReadService;
    }

    @GetMapping
    @ResponseBody
    public List<Comment> showComments(@PathVariable Long articleId) {
        return commentService.findByArticleId(articleId);
    }

    @GetMapping("/count")
    @ResponseBody
    public long countComments(@PathVariable Long articleId) {
        return commentService.countByArticleId(articleId);
    }

    @PostMapping
    @ResponseBody
    public Comment createComment(SessionUser loginUser, @PathVariable Long articleId, @RequestBody CommentDto commentDto) {
        log.info("Comment create: contents={}", commentDto.getContents());

        Article article = articleReadService.findById(articleId);
        Comment comment = commentService.save(commentDto.toComment(loginUser.getUser(), article));

        return comment;
    }

    @PutMapping("/{commentId}")
    @ResponseBody
    public Comment updateComment(SessionUser loginUser, @PathVariable Long commentId, @PathVariable Long articleId, @RequestBody CommentDto commentDto) {
        log.info("Comment update: id={}, contents={}", commentId, commentDto.getContents());

        Article article = articleReadService.findById(articleId);
        Comment updatedComment = commentService.modify(commentId, commentDto.toComment(loginUser.getUser(), article));

        return updatedComment;
    }

    @DeleteMapping("/{commentId}")
    @ResponseBody
    public Long removeComment(@PathVariable Long commentId, SessionUser loginUser) {
        Long deletedId = commentService.deleteById(commentId, loginUser.getUser());

        return deletedId;
    }
}
