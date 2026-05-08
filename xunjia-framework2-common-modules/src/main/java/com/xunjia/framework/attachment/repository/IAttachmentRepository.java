package com.xunjia.framework.attachment.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.xunjia.framework.attachment.entity.Attachment;

public interface IAttachmentRepository extends JpaRepository<Attachment, String> {

	public Page<Attachment> findAll(Specification<Attachment> spec, Pageable pageable);
	
	public List<Attachment> findByCodeIn(String[] codes);
	
	@Modifying
	@Query("UPDATE Attachment SET downloadCount = downloadCount + 1 WHERE code = ?1")
	public void updateDownloadCountAuto(String code);
	
	@Modifying
	@Query("DELETE FROM Attachment WHERE code IN (?1)")
	public void deleteByCodes(String[] codes);
	
	public List<Attachment> findByBusinessAndBusinessId(String business, String businessId);
	
	public List<Attachment> findByBusinessAndBusinessIdAndBusinessSubType(String business, String businessId, String businessSubType);
}
