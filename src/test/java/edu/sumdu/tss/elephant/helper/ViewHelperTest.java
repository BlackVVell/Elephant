package edu.sumdu.tss.elephant.helper;

import edu.sumdu.tss.elephant.controller.AbstractController;
import edu.sumdu.tss.elephant.helper.exception.HttpException;
import edu.sumdu.tss.elephant.helper.utils.ExceptionUtils;
import edu.sumdu.tss.elephant.model.Database;
import edu.sumdu.tss.elephant.model.DatabaseService;
import edu.sumdu.tss.elephant.model.User;
import io.javalin.core.util.JavalinLogger;
import io.javalin.http.Context;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ViewHelperTest {

    @Mock
    Context ctx;

    @Mock
    HttpException exception;

    @Test
    @DisplayName("Should output user error")
    @SneakyThrows
    void userError() {
        String stacktrace = new String();
        Map<String, Object> model = new HashMap<>();
        System.out.println(AbstractController.currentModel(ctx));
        int code = 0;
        String path = "/velocity/error.vm";

        try (MockedStatic<ExceptionUtils> exceptionUtilsMockedStatic = mockStatic(ExceptionUtils.class);
             MockedStatic<AbstractController> abstractControllerMockedStatic = mockStatic(AbstractController.class);
             MockedStatic<Keys> keysMockedStatic = mockStatic(Keys.class)) {
            exceptionUtilsMockedStatic.when(() -> ExceptionUtils.stacktrace(exception)).thenReturn(stacktrace);
            abstractControllerMockedStatic.when(() -> AbstractController.currentModel(ctx)).thenReturn(model);
            keysMockedStatic.when(() -> Keys.isProduction()).thenReturn(false);

            ViewHelper.userError(exception, ctx);

            verify(ctx).status(eq(code));
            verify(ctx).render(eq(path), eq(model));
        }
    }

    @Test
    @DisplayName("Should return breadcrumb")
    void breadcrumbIfNotNull() {
        List<String> breadcrumbs = List.of("1", "2");
        when(ctx.sessionAttribute(Keys.BREADCRUMB_KEY)).thenReturn(breadcrumbs);
        List<String> actual = ViewHelper.breadcrumb(ctx);
        assertEquals(breadcrumbs, actual);
    }

    @Test
    @DisplayName("Should return null if breadcrumb is null")
    void breadcrumbIfNull() {
        List<String> breadcrumbs = List.of("<a href='/home'><ion-icon name=\"home-outline\"></ion-icon></a>");
        when(ctx.sessionAttribute(eq(Keys.BREADCRUMB_KEY))).thenReturn(null);
        List<String> actual = ViewHelper.breadcrumb(ctx);
        verify(ctx).sessionAttribute(eq(Keys.BREADCRUMB_KEY), eq(breadcrumbs));
        assertEquals(breadcrumbs, actual);
    }

    @Test
    @DisplayName("Should cleanup session")
    void cleanupSession() {
        ViewHelper.cleanupSession(ctx);
        verify(ctx).sessionAttribute(eq(Keys.MODEL_KEY), eq(null));
        verify(ctx).sessionAttribute(eq(Keys.DB_KEY), eq(null));
        verify(ctx).sessionAttribute(eq(Keys.BREADCRUMB_KEY), eq(null));
    }

    @Test
    @DisplayName("Should create default variables if Database null and keys not null")
    void defaultVariablesDatabaseNullAndKeysNotNull() {
        User user = new User();
        user.setLanguage("en");
        doNothing().when(ctx).sessionAttribute(anyString(), any());
        when(ctx.sessionAttribute(anyString())).thenReturn("");
        when(ctx.sessionAttribute(eq(Keys.SESSION_CURRENT_USER_KEY))).thenReturn(user);
        when(ctx.sessionAttribute(eq(Keys.BREADCRUMB_KEY))).thenReturn(null);
        when(ctx.path()).thenReturn("");

        try (MockedStatic<JavalinLogger> loggerMockedStatic = mockStatic(JavalinLogger.class)) {

            ViewHelper.defaultVariables(ctx);

            loggerMockedStatic.verify(() -> JavalinLogger.info(eq(user.toString())));
            verify(ctx).sessionAttribute(eq(Keys.ERROR_KEY), eq(null));
            verify(ctx).sessionAttribute(eq(Keys.INFO_KEY), eq(null));
        }
    }

    @Test
    @DisplayName("Should create default variables if Database not null and keys null")
    void defaultVariablesDatabaseNotNullAndKeysNull() {
        User user = new User();
        user.setLanguage("en");
        Database database = new Database();
        doNothing().when(ctx).sessionAttribute(anyString(), any());
        when(ctx.sessionAttribute(anyString())).thenReturn("");
        when(ctx.sessionAttribute(eq(Keys.SESSION_CURRENT_USER_KEY))).thenReturn(user);
        when(ctx.sessionAttribute(eq(Keys.BREADCRUMB_KEY))).thenReturn(null);
        when(ctx.path()).thenReturn("database/1");
        when(ctx.sessionAttribute(eq(Keys.INFO_KEY))).thenReturn(null);
        when(ctx.sessionAttribute(eq(Keys.ERROR_KEY))).thenReturn(null);


        try (MockedStatic<JavalinLogger> loggerMockedStatic = mockStatic(JavalinLogger.class);
             MockedStatic<DatabaseService> databaseServiceMockedStatic = mockStatic(DatabaseService.class)) {

            databaseServiceMockedStatic.when(() -> DatabaseService.activeDatabase(user.getUsername(), "1")).thenReturn(database);

            ViewHelper.defaultVariables(ctx);


            loggerMockedStatic.verify(() -> JavalinLogger.info(eq(user.toString())));
            verify(ctx).sessionAttribute(eq(Keys.DB_KEY), eq(database));
        }
    }

    @Test
    @DisplayName("Should return pager")
    void pager() {
        String expected = "<nav>\n" +
                "<ul class=\"pagination\"><li class=\"page-item\"><a class=\"page-link\" href=\"?offset=1\">1</a></li>\n" +
                "<li class=\"page-item active\"><a class=\"page-link\" href=\"#\">2</a></li>\n" +
                "<li class=\"page-item\"><a class=\"page-link\" href=\"?offset=3\">3</a></li>\n" +
                "<li class=\"page-item\"><a class=\"page-link\" href=\"?offset=4\">4</a></li>\n" +
                "</ul>\n" +
                "</nav>";
        String actual = ViewHelper.pager(5, 2);
        assertEquals(expected, actual);
    }

    @Test
    void softError() {
        String message = new String();
        doReturn(null).when(ctx).header("Referer");
        ViewHelper.softError(message, ctx);
        verify(ctx).sessionAttribute(eq(Keys.ERROR_KEY), eq(message));
        verify(ctx).redirect(eq(ctx.header("Referer")));
    }
}