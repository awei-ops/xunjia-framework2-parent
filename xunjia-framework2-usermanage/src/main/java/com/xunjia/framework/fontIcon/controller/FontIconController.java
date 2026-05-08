package com.xunjia.framework.fontIcon.controller;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.dictionary.service.DicContentService;
import com.xunjia.framework.fontIcon.service.FontIconService;
import com.xunjia.framework.usermanage.entity.DicContent;
import com.xunjia.framework.usermanage.entity.FontIcon;
import com.xunjia.framework.utils.FileUtils;
import com.xunjia.framework.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/fontIcon")
public class FontIconController {

    @Autowired
    private FontIconService service;

    @Autowired
    private DicContentService dcService;

    @RequestMapping("/toAdd")
    public ModelAndView toAdd(){
        return new ModelAndView("framework/fontIcon/add");
    }

    @RequestMapping("/toEdit")
    public ModelAndView toEdit(){
        return new ModelAndView("framework/fontIcon/edit");
    }

    @RequestMapping("/toList")
    public ModelAndView toList(){
        return new ModelAndView("framework/fontIcon/list");
    }

    @RequestMapping("/toSelect")
    public ModelAndView toSelect(){
        ModelAndView mav = new ModelAndView("framework/fontIcon/select");
        List<DicContent> fontTypes = dcService.findByTypeCode("FONT_ICON_TYPE");
        List<FontIcon> fontIcons = service.findAll();

        int rows = 0;
        if (!ListUtils.isListEmpty(fontIcons)){
            rows = fontIcons.size() % 6 == 0 ? fontIcons.size() / 6 : fontIcons.size() / 6 + 1;
        }
        mav.addObject("fontIcons", fontIcons);
        mav.addObject("rows", rows);
        mav.addObject("fontTypes", fontTypes);
        return mav;
    }

    @RequestMapping("/save")
    public ResponseData<Boolean> save(FontIcon fontIcon){
        return service.save(fontIcon);
    }

    @RequestMapping("/update")
    public ResponseData<Boolean> update(FontIcon fontIcon){
        return service.update(fontIcon);
    }

    @RequestMapping("/delete")
    public ResponseData<Boolean> delete(@RequestParam(name="ids[]") String[] ids){
        return service.delete(ids);
    }

    @RequestMapping("/importIcons")
    public ResponseData<String> importIcons(MultipartRequest request){
        MultipartFile dataFile = request.getFile("dataFile");
        return service.importIcons(dataFile);
    }

    @RequestMapping("/findFontIcons")
    public PageVO<FontIcon> findFontIcons(String code, String type, int page, int rows){
        Page<FontIcon> pageData = service.findFontIcons(code, type, page, rows);
        PageVO<FontIcon> pageVO = new PageVO<>(pageData);
        return pageVO;
    }

    @RequestMapping("/findById")
    public FontIcon findById(String id){
        return service.findById(id);
    }

    @RequestMapping("/downloadImportTemplate")
    public void downloadImportTemplate(HttpServletResponse response){
        Resource resource = new ClassPathResource("static/template/图标导入模板.xlsx");
        try {
            //File resourceFile = resource.getFile();
            InputStream is = resource.getInputStream();
            FileUtils.downloadFile(response, is, "图标导入模板.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
