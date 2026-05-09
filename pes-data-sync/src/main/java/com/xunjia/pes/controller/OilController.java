package com.xunjia.pes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/oil")
public class OilController {

    @RequestMapping("/toList")
    public ModelAndView toList() {
        return new ModelAndView("basicDataManage/oil/list");
    }

}