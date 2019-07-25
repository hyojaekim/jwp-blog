package techcourse.myblog.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import techcourse.myblog.domain.Article;
import techcourse.myblog.dto.LoginDto;
import techcourse.myblog.dto.UserDto;
import techcourse.myblog.exception.CouldNotFindArticleIdException;
import techcourse.myblog.repository.ArticleRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ArticleControllerTest extends ControllerTest {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Article article;
    private UserDto userDto;
    private LoginDto loginDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        loginDto = new LoginDto();

        article = articleRepository.save(Article.of("test title",
                "test coverUrl",
                "test contents")
        );

        userDto.setName("test");
        userDto.setEmail("test@test.com");
        userDto.setPassword("PassW0rd@");
        userDto.setPasswordConfirm("PassW0rd@");

        loginDto.setEmail("test@test.com");
        loginDto.setPassword("PassW0rd@");
    }

    private WebTestClient.ResponseSpec requestGetTest(String testUrl) {
        return webTestClient.get()
                .uri(testUrl)
                .exchange()
                .expectStatus().isOk();
    }


    private WebTestClient.ResponseSpec requestPostTest(String inputTitle, String inputCoverUrl, String inputContents) {
        return webTestClient.post()
                .uri("/write")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("title", inputTitle)
                        .with("coverUrl", inputCoverUrl)
                        .with("contents", inputContents))
                .exchange();
    }

    @Test
    @DisplayName("게시글을 작성한 뒤 생성 버튼을 눌렀을 때 생성된 게시글을 보여준다.")
    void createNewArticleTest() {
        // Given
        long expectedIdGeneratedByServer = article.getArticleId();
        String inputTitle = "test title";
        String inputCoverUrl = "test coverUrl";
        String inputContents = "test contents";

        // When
        WebTestClient.ResponseSpec responseSpec = requestPostTest(inputTitle, inputCoverUrl, inputContents);

        // Then
        responseSpec
                .expectStatus().isFound();
        Article article = articleRepository.findById(expectedIdGeneratedByServer)
                .orElseThrow(CouldNotFindArticleIdException::new);

        assertThat(article.getTitle()).isEqualTo(inputTitle);
        assertThat(article.getCoverUrl()).isEqualTo(inputCoverUrl);
        assertThat(article.getContents()).isEqualTo(inputContents);
    }

    @Test
    @DisplayName("새로운 Article 생성시 article-edit 페이지를 되돌려준다.")
    void articleCreationPageTest() {
        postUser(webTestClient, userDto, postUserResponse -> {
            String sessionId = getSessionId(postUserResponse);
            postLogin(webTestClient, loginDto, sessionId, postLoginResponse -> {
                requestGetTest("articles/new" + sessionId);
            });
        });
    }

    @Test
    @DisplayName("게시글에서 수정 버튼을 누르는 경우 id에 해당하는 edit 페이지를 되돌려준다.")
    void articleEditPageTest() {
        userDto.setEmail("edit@test.com");
        loginDto.setEmail("edit@test.com");

        postUser(webTestClient, userDto, postUserResponse -> {
            String sessionId = getSessionId(postUserResponse);
            postLogin(webTestClient, loginDto, sessionId, postLoginResponse -> {
                requestGetTest("/articles/" + article.getArticleId() + "/edit" + sessionId)
                        .expectBody()
                        .consumeWith(response -> {
                            String body = new String(response.getResponseBody());
                            assertThat(body.contains(article.getTitle())).isTrue();
                            assertThat(body.contains(article.getCoverUrl())).isTrue();
                            assertThat(body.contains(article.getContents())).isTrue();
                        });
            });
        });

    }

    @Test
    @DisplayName("게시글을 삭제한다.")
    void deleteArticleTest() {
        long deleteTestId = article.getArticleId();

        // When, Then
        webTestClient.delete()
                .uri("/articles/" + deleteTestId)
                .exchange()
                .expectStatus().isFound();

        assertThatThrownBy(() -> articleRepository
                .findById(deleteTestId)
                .orElseThrow(CouldNotFindArticleIdException::new))
                .isInstanceOf(CouldNotFindArticleIdException.class);
    }

    @AfterEach
    void tearDown() {
        articleRepository.deleteAll();
    }
}