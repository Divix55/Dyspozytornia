package pl.sip.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping(value = {"/", "/home", "/index"})
    public String showIndex(){
        return "home";
    }

    @RequestMapping("/about")
    public String showAbout() {
        return "about";
    }

    @RequestMapping("/contact")
    public String showContact() { return "contact"; }

    @RequestMapping("/shops")
    public String showShops() {
        return "shops";
    }

    @RequestMapping("/supply")
    public String showSupply() { return "supply"; }
}
