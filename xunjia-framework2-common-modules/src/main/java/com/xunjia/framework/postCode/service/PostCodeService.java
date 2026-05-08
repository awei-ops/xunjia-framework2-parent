package com.xunjia.framework.postCode.service;

import com.xunjia.framework.postCode.repository.IPostCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class PostCodeService {

    @Autowired
    private IPostCodeRepository postCodeRepository;

    public String[] findProvinces(){
        return postCodeRepository.findProvinces();
    }

    public String[] findCities(String province){
        return postCodeRepository.findCities(province);
    }

    public String[] findAreas(String city){
        return postCodeRepository.findAreas(city);
    }
}
