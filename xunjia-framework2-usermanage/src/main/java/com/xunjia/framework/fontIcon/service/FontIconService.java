package com.xunjia.framework.fontIcon.service;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.dictionary.repository.IDicContentRepository;
import com.xunjia.framework.fontIcon.repository.IFontIconRepository;
import com.xunjia.framework.usermanage.entity.DicContent;
import com.xunjia.framework.usermanage.entity.FontIcon;
import com.xunjia.framework.utils.StringUtils;
//import com.spire.xls.ExcelVersion;
//import com.spire.xls.Workbook;
//import com.spire.xls.Worksheet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class FontIconService {

    @Autowired
    private IFontIconRepository repo;

    @Autowired
    private IDicContentRepository dicContentRepo;

    public ResponseData<Boolean> save(FontIcon fontIcon){
        ResponseData<Boolean> resp;
        try {
            FontIcon existIcon = repo.findByCode(fontIcon.getCode());
            if (existIcon == null){
                repo.save(fontIcon);
                resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
            } else {
                resp = ResponseData.getFail("保存失败，系统中已存在相同图标。");
            }
        } catch (Exception e){
            log.error(e.getMessage(), e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> update(FontIcon fontIcon){
        ResponseData<Boolean> resp;
        try{
            repo.save(fontIcon);
            resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> delete(String[] ids){
        ResponseData<Boolean> resp;
        try{
            repo.deleteByIdIn(ids);
            resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<String> importIcons(MultipartFile dataFile){
        ResponseData<String> resp = null;
//        Workbook workbook = new Workbook();
//        try (InputStream is = dataFile.getInputStream()) {
//            workbook.loadFromStream(is, ExcelVersion.Version2013);
//            Worksheet sheet = workbook.getWorksheets().get(0);
//            int lastRowIndex = sheet.getLastRow();
//            if (lastRowIndex > 0) {
//                //分批次导入
//                int pageSize = 100;
//                int pageCount = lastRowIndex % pageSize == 0 ? lastRowIndex / pageSize : lastRowIndex / pageSize + 1;
//                List<DicContent> fontIconTypes = dicContentRepo.findByType_codeOrderByOrderNoAsc("FONT_ICON_TYPE");
//
//                for (int page = 0; page < pageCount; page++) {
//                    int startPos = page * pageSize + 1;
//                    int endPos = startPos + pageSize;
//                    if (endPos > lastRowIndex + 1) {
//                        endPos = lastRowIndex + 1;
//                    }
//
//                    if (page == 0) {
//                        startPos += 1;
//                    }
//
//                    List<FontIcon> fontIcons = new ArrayList<>(endPos - startPos);
//                    for (int i = startPos; i < endPos; i++) {
//                        String code = sheet.get(i, 1).getValue().trim();
//                        String type = sheet.get(i, 2).getValue().trim();
//                        Optional<DicContent> typeOptional = fontIconTypes.stream().filter(c -> c.getName().equals(type)).findFirst();
//
//                        if (typeOptional.isPresent()){
//                            FontIcon fontIcon = new FontIcon();
//                            fontIcon.setCode(code);
//                            fontIcon.setTypeName(type);
//                            fontIcon.setTypeCode(typeOptional.get().getCode());
//                            fontIcons.add(fontIcon);
//                        }
//                    }
//                    if (fontIcons.size() > 0) {
//                        repo.saveAll(fontIcons);
//                    }
//                }
//            }
//
//            resp = ResponseData.getSuccess(ResponseMsg.IMPORT_SUCCESS);
//        } catch (IOException e) {
//            log.error("DicContentService.importContents方法异常。", e);
//            resp = ResponseData.getError(e);
//        }
        return resp;
    }

    public FontIcon findById(String id){
        return repo.findById(id).get();
    }

    public List<FontIcon> findByTypeCode(String typeCode){
        return repo.findByTypeCode(typeCode);
    }

    public List<FontIcon> findByTypeName(String typeName){
        return repo.findByTypeName(typeName);
    }

    public List<FontIcon> findAll(){
        return repo.findAll();
    }

    public Page<FontIcon> findFontIcons(String code, String type, int page, int rows){
        Specification<FontIcon> specification = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();

            if (!StringUtils.isEmpty(code)){
                Predicate predicate = cb.equal(root.get("code").as(String.class), code);
                predicates.add(predicate);
            }
            if (!StringUtils.isEmpty(type)){
                Predicate predicate = cb.equal(root.get("type").as(String.class), type);
                predicates.add(predicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page - 1, rows, sort);
        org.springframework.data.domain.Page<FontIcon> pageData = null;
        try {
            pageData = repo.findAll(specification, pageable);
        } catch (Exception e) {
            log.error("FontIconService.findFontIcons方法异常。", e);
        }
        return pageData;
    }
}
