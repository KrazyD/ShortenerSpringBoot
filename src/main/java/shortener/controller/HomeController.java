package shortener.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import shortener.entity.BaseEntity;
import shortener.entity.User;
import shortener.service.BaseService;
import shortener.service.IUserService;

import java.util.Objects;

@Controller
public class HomeController {

    private static final Logger logger = LogManager.getLogger(HomeController.class);

    @GetMapping("/")
    public String homeInit() {
        return "index";
    }

    public static String getExceptionMessage(Errors errors) {
        StringBuilder errorMessage = new StringBuilder("\"Some errors were caused: ");
        for (ObjectError err : errors.getAllErrors()) {
            errorMessage.append(err.getDefaultMessage()).append(" ");
        }
        errorMessage.append("\"");
        return errorMessage.toString();
    }

    public static BaseEntity handleErrors(TwoParamsFunction<BaseService, Object, BaseEntity> func, BaseService service, Object param) {
        BaseEntity resultEntity = null;

        try {
            resultEntity = func.apply(service, param);
        } catch (Exception ex) {
            String error = getExceptionMessage(ex);
            logger.error("!!!Error while handle request!!! - " + error);
        }

        return resultEntity;
    }

    public static String getExceptionMessage(Exception ex) {
        String error;
        if (ex.getCause() == null) {
            error = ex.getMessage();
        } else if(ex.getCause().getCause() == null) {
            error = ex.getCause().getMessage();
        } else {
            error = ex.getCause().getCause().getMessage();
        }
        return error;
    }

    public static boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    public static boolean isCurrentUserOwner(Long userId, IUserService userService) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!auth.isAuthenticated()) {
            return false;
        }

        User foundUser = (User) handleErrors((service, id) -> service.findById((Long) id), userService, userId);

        return Objects.equals(auth.getName(), foundUser.getLogin());
    }
}
