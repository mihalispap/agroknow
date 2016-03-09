package com.agroknow.search.web.controllers;

import com.agroknow.search.domain.entities.User;
import com.agroknow.search.domain.exceptions.PersistenceException;
import com.agroknow.search.domain.exceptions.ValidationException;
import com.agroknow.search.domain.services.UserService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author aggelos
 */
@Controller
public class SimpleController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = { "/", "" }, method = { RequestMethod.GET })
    public String home(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return "index";
    }

    @RequestMapping(value = { "/register" }, method = { RequestMethod.GET })
    public String registerForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return "register";
    }

    @RequestMapping(value = { "/register" }, method = { RequestMethod.POST }, produces = { "text/html" })
    public ModelAndView registerSaveHTML(HttpServletRequest request, HttpServletResponse response) {
        Map<String,String> model = new HashMap<String,String>(2);
        try {
            doRegister(request, response);
            response.setStatus(HttpStatus.CREATED.value());
            model = new HashMap<String,String>(2);
            model.put("status", "SUCCESS");
            model.put("message", "Registration completed successfully. Please check your email for instructions.");
        } catch(ValidationException ex) {
            response.setStatus(HttpStatus.METHOD_FAILURE.value());
            model.put("status", "INVALID");
            model.put("message", "Invalid input data.");
            model.putAll(ex.getErrorMessages());
        } catch(PersistenceException ex) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            model.put("status", "FAILURE");
            model.put("message", ex.getMessage());
        }

        return new ModelAndView("register", model);
    }

    @RequestMapping(value = { "/register" }, method = { RequestMethod.POST }, produces = { "application/json", "text/plain" })
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody Map<String,String> registerSaveAPI(HttpServletRequest request, HttpServletResponse response) {
        doRegister(request, response);
        Map<String,String> result = new HashMap<String,String>();
        result.put("message", "Registration completed successfully. Please check your email for instructions.");
        return result;
    }

    private void doRegister(HttpServletRequest request, HttpServletResponse response) {
        // get and validate email
        String email = request.getParameter("email");
        if(!EmailValidator.getInstance(true).isValid(email)) {
            throw new ValidationException().addErrorMessage("email", "Invalid email value.");
        }

        // create and save User
        User user = new User(email);
        userService.register(user);
    }

}
