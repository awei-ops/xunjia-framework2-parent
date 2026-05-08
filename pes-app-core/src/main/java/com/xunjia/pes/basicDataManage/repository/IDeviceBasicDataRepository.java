package com.xunjia.pes.basicDataManage.repository;

import com.xunjia.pes.basicDataManage.entity.DeviceBasicData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IDeviceBasicDataRepository extends JpaRepository<DeviceBasicData,String> {

    DeviceBasicData findByEvaluationIndexNameAndDeviceTypeCodeAndDeviceCategoryAndDeleteFlag(String evaluationIndexName, String deviceTypeCode,String deviceCategory, int deleteFlag);

    @Modifying
    @Query("UPDATE DeviceBasicData SET deleteFlag = 1 WHERE id IN (?1)")
    void deleteByIds(String[] ids);

    Page<DeviceBasicData> findAll(Specification<DeviceBasicData> spec, Pageable pageable);
}
