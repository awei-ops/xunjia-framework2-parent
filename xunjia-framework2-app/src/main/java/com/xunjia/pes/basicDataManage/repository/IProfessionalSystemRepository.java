package com.xunjia.pes.basicDataManage.repository;

import com.xunjia.pes.basicDataManage.entity.ProfessionalSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IProfessionalSystemRepository extends JpaRepository<ProfessionalSystem,String> {
    ProfessionalSystem findByEvaluationIndexNameAndProfessionalTypeAndDeleteFlag(String evaluationIndexName,String professionalType, int deleteFlag);

    @Modifying
    @Query("UPDATE ProfessionalSystem SET deleteFlag = 1 WHERE id IN (?1)")
    void deleteByIds(String[] ids);

    Page<ProfessionalSystem> findAll(Specification<ProfessionalSystem> spec, Pageable pageable);
}
