package com.xunjia.pes.basicDataManage.repository;

import com.xunjia.pes.basicDataManage.entity.StationSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IStationSystemRepository extends JpaRepository<StationSystem,String> {

    StationSystem findByEvaluationIndexNameAndStationTypeAndDeleteFlag(String evaluationIndexName, String stationType, int deleteFlag);

    @Modifying
    @Query("UPDATE StationSystem SET deleteFlag = 1 WHERE id IN (?1)")
    void deleteByIds(String[] ids);

    Page<StationSystem> findAll(Specification<StationSystem> spec, Pageable pageable);
}
