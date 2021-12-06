package edu.sumdu.tss.elephant.middleware;

import edu.sumdu.tss.elephant.controller.AbstractController;
import edu.sumdu.tss.elephant.controller.HomeController;
import edu.sumdu.tss.elephant.helper.UserRole;

import edu.sumdu.tss.elephant.model.User;
import io.javalin.core.util.JavalinLogger;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomAccessManagerTest {


    @Mock
    Handler handler;
    @Mock
    Context ctx;


    @Test
    @DisplayName("Should return if user role is null")
    @SneakyThrows
    void accessManagerUserRoleNullTest() {

        CustomAccessManager.accessManager.manage(handler, ctx, Set.of());
        verify(handler).handle(eq(ctx));

    }

    @Test
    @DisplayName("Should redirect if user without permissions ")
    @SneakyThrows
    void accessManagerRedirectWithoutPermissionTest() {

        User user = new User();
        user.setRole(2L);
        String path = "/";
        String stringFormat = String.format("Permission deny to %s for %s", path, user);


        try (MockedStatic<AbstractController> mockedStatic = mockStatic(AbstractController.class);
             MockedStatic<JavalinLogger> mockedLogger = mockStatic(JavalinLogger.class)) {
            mockedStatic.when(() -> AbstractController.currentUser(ctx)).thenReturn(user);
            when(ctx.contextPath()).thenReturn(path);

            CustomAccessManager.accessManager.manage(handler, ctx, Set.of(UserRole.ADMIN));

            mockedLogger.verify(() -> JavalinLogger.info(eq(stringFormat)));
            verify(ctx).redirect(eq(HomeController.BASIC_PAGE), eq(302));
        }
    }

    @Test
    @DisplayName("Should return if user has permission")
    @SneakyThrows
    void accessManagerUserRoleTest() {


        User user = new User();
        user.setRole(2L);

        try (MockedStatic<AbstractController> mockedStatic = mockStatic(AbstractController.class)) {
            mockedStatic.when(() -> AbstractController.currentUser(ctx)).thenReturn(user);

            CustomAccessManager.accessManager.manage(handler, ctx, Set.of(UserRole.BASIC_USER));

            verify(handler).handle(eq(ctx));
        }

    }


}