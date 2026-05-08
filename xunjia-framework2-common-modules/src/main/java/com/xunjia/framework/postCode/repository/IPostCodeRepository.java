package com.xunjia.framework.postCode.repository;

import com.xunjia.framework.common.entity.PostCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IPostCodeRepository extends JpaRepository<PostCode, Integer> {

    @Query(value = "SELECT DISTINCT province FROM b_postcode", nativeQuery = true)
    String[] findProvinces();

    @Query(value = "SELECT DISTINCT city FROM b_postcode WHERE province = ?1", nativeQuery = true)
    String[] findCities(String province);

    @Query(value = "SELECT DISTINCT area FROM b_postcode WHERE city = ?1", nativeQuery = true)
    String[] findAreas(String city);
}
