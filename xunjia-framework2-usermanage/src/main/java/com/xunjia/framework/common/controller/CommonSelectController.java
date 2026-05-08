package com.xunjia.framework.common.controller;

import com.xunjia.framework.org.service.OrganizationService;
import com.xunjia.framework.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/commonSelect")
public class CommonSelectController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService orgService;

    @RequestMapping("/toSelectUser")
    public ModelAndView toSelectUser(){
        return new ModelAndView("framework/commonSelect/selectUser");
    }

    @RequestMapping("/toSelectOrg")
    public ModelAndView toSelectOrg(){
        return new ModelAndView("framework/commonSelect/selectOrg");
    }

}
