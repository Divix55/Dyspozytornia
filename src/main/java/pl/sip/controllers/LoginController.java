package pl.sip.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.sip.dao.MapPointerDAO;
import pl.sip.dao.MapPointerDAOImpl;
import pl.sip.dto.LoginUser;
import pl.sip.dto.NewMapPointer;
import pl.sip.dto.NewUser;
import pl.sip.services.MapPointerService;
import pl.sip.services.MapPointerServiceImpl;
import pl.sip.services.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;

@Controller
public class LoginController {

    private final UserService userService;
    private final MapPointerService pointerService;

    @Autowired
    public LoginController(UserService userService, MapPointerService pointerService) {
        this.userService = userService;
        this.pointerService = pointerService;
    }

    private void setUpSession(LoginUser user, HttpSession httpSession, int privileges){
        httpSession.setAttribute("user", user);
        httpSession.setAttribute("userName", user.getUserName());
        httpSession.setAttribute("userPrivileges", privileges);

    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model){
        model.addAttribute("loginForm", new LoginUser());
        return "login";
    }

    @GetMapping("/stores")
    public String showStores(ArrayList<NewMapPointer> mapPointer, Model model) {
        mapPointer = pointerService.showStoreTable();
        for(NewMapPointer point: mapPointer) {
            model.addAttribute("name" + point.getPointId(), point.getPointName());
            model.addAttribute("latitude" + point.getPointId(), point.getPointLatitude());
            model.addAttribute("longitude" + point.getPointId(), point.getPointLongitude());
        }
        return "stores";
    }

    @GetMapping("/shops")
    public String showShops(ArrayList<NewMapPointer> mapPointer, Model model) {
        mapPointer = pointerService.showShopTable();
        for(NewMapPointer point: mapPointer) {
            model.addAttribute("name" + point.getPointId(), point.getPointName());
            model.addAttribute("latitude" + point.getPointId(), point.getPointLatitude());
            model.addAttribute("longitude" + point.getPointId(), point.getPointLongitude());
        }
        return "shops";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String checkLogin(@ModelAttribute("loginForm") @Valid LoginUser form,
                             BindingResult result,
                             Model model,
                             HttpSession session){
        if(result.hasErrors()){
            model.addAttribute("error_msg", "Invalid format of username or password!");
            return "login";
        }
        else{
            if(userService.loginUser(form)){
                int userPrivilege = userService.checkUserPrivilege(form);
                setUpSession(form, session, userPrivilege);
                return "redirect:/index";
            }
            else{
                model.addAttribute("error_msg", "Wrong username or password!");
                return "login";
            }
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(Model model){
        model.addAttribute("registerForm", new NewUser());
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String checkRegister(@ModelAttribute("registerForm") @Valid NewUser form, BindingResult result, Model model){
        if(result.hasErrors()){
            model.addAttribute("error_msg", "Wrong credentials!");
            return "register";
        }
        else{
            userService.createUser(form);
            return "redirect:/index";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/index";
    }

    @RequestMapping("/account")
    public String showAccount() {
        return "account";
    }

    @RequestMapping("/admin-panel")
    public String showAdminPanel() {
        return "admin-panel";
    }
}
