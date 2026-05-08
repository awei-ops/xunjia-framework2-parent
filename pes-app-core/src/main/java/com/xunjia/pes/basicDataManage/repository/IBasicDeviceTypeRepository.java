package com.xunjia.pes.basicDataManage.repository;

import com.xunjia.pes.basicDataManage.entity.BasicDeviceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IBasicDeviceTypeRepository extends JpaRepository<BasicDeviceType, String> {

    BasicDeviceType findByDeviceTypeCodeAndDeviceCategoryAndDeleteFlag(String deviceTypeCode, String deviceCategory, int deleteFlag);

    @Modifying
    @Query("UPDATE BasicDeviceType SET deleteFlag = 1 WHERE id IN (?1)")
    void deleteByIds(String[] ids);

    Page<BasicDeviceType> findAll(Specification<BasicDeviceType> spec, Pageable pageable);

    List<BasicDeviceType> findByDeviceCategoryAndDeleteFlagOrderByDeviceTypeNameAsc(String deviceCategory, int deleteFlag);
}
