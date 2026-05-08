package com.xunjia.framework.user.service;

import com.xunjia.framework.user.repository.ILoginFailRecordRepository;
import com.xunjia.framework.usermanage.entity.LoginFailRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoginFailRecordService {

    @Autowired
    private ILoginFailRecordRepository repo;

    public LoginFailRecord findByIp(String ip){
        return repo.findByIp(ip);
    }
}
