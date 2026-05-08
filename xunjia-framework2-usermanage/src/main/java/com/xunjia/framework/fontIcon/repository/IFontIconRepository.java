package com.xunjia.framework.fontIcon.repository;

import com.xunjia.framework.usermanage.entity.FontIcon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IFontIconRepository extends JpaRepository<FontIcon, String> {

    Page<FontIcon> findAll(Specification<FontIcon> spec, Pageable pageable);

    List<FontIcon> findByTypeCode(String typeCode);

    List<FontIcon> findByTypeName(String typeName);

    FontIcon findByCode(String code);

    void deleteByIdIn(String[] ids);

}
