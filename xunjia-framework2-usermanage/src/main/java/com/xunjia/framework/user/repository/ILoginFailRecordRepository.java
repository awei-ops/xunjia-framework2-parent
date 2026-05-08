package com.xunjia.framework.user.repository;

import com.xunjia.framework.usermanage.entity.LoginFailRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface ILoginFailRecordRepository extends JpaRepository<LoginFailRecord, String> {

    public LoginFailRecord findByIp(String ip);

    @Modifying
    @Query("UPDATE LoginFailRecord SET loginFailCount = loginFailCount + 1, loginFailTime = ?1, nextLoginTime = ?2 WHERE ip = ?3")
    public void updateLoginFail(LocalDate loginFailTime, LocalDate nextLoginTime, String ip);

    public void deleteByIp(String ip);

}
