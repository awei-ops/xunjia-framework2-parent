package com.xunjia.framework.user.service;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.common.constant.DateTimeConst;
import com.xunjia.framework.common.response.LoginResponseData;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.org.repository.IOrganizationRepository;
import com.xunjia.framework.orgPermission.repository.IOrgPermissionRepository;
import com.xunjia.framework.resource.repository.IResourceRepository;
import com.xunjia.framework.resourcePermission.repository.IResourcePermissionRepository;
import com.xunjia.framework.role.repository.IRoleRepository;
import com.xunjia.framework.security.exception.PasswordExpiredException;
import com.xunjia.framework.sysSettings.auto.InitSysSettings;
import com.xunjia.framework.user.repository.*;
import com.xunjia.framework.usermanage.entity.*;
import com.xunjia.framework.utils.*;
//import com.spire.xls.ExcelVersion;
//import com.spire.xls.Workbook;
//import com.spire.xls.Worksheet;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户信息业务服务
 * 2020年5月9日
 *
 * @author 姜浩
 */
@Service
@Transactional
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private IUserRepository repo;

    @Autowired
    private IUserRoleMappingRepository urmRepo;

    @Autowired
    private IUserSettingsRepository usRepo;

    @Autowired
    private IResourceRepository resRepo;

    @Autowired
    private IResourcePermissionRepository resPermRepo;

    @Autowired
    private IOrgPermissionRepository orgPermRepo;

    @Autowired
    private IRoleRepository roleRepo;

    @Autowired
    private IOrganizationRepository orgRepo;

    @Autowired
    private ILoginAuditRepository loginAuditRepo;

    @Autowired
    private ILoginFailRecordRepository failRecordRepo;

    @Value("${com.xunjia.framework.baseUploadFolder}")
    private String uploadFolder;

    @Value("${com.xunjia.framework.security.loginFailShowVerifyCode}")
    private int loginFailShowVerifyCode;

    @Value("${com.xunjia.framework.security.loginFailEnableDisable}")
    private int loginFailEnableDisable;

    @Value("${com.xunjia.framework.security.defaultPassword}")
    private String defaultPassword;

    @Value("${com.xunjia.framework.security.passwordEffectiveDays}")
    private int passwordEffectiveDays;


    /**
     * 保存用户信息
     *
     * @param user
     * @return
     */
    public ResponseData<Boolean> save(User user, String[] roleIds) {
        ResponseData<Boolean> resp;
        try {
            User existUser = repo.findByUsername(user.getUsername());
            if (existUser != null) {
                resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_NAME_EXIST);
                return resp;
            }
            if (!StringUtils.isEmpty(user.getStaffCode())) {
                existUser = repo.findByStaffCode(user.getStaffCode());
                if (existUser != null) {
                    resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_CODE_EXIST);
                    return resp;
                }
            }
            if (!StringUtils.isEmpty(user.getEleEquipCode())) {
                existUser = repo.findByEleEquipCodeAndDeleteFlag(user.getEleEquipCode(), 0);
                if (existUser != null) {
                    resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_EQUIP_EXIST);
                    return resp;
                }
            }

            user.setPassword(MD5Pwd.MD5Pwd(user.getUsername(), defaultPassword));
            user.setEnable(1);
            user.setRealNamePyCode(StringLetterUtils.getFirstLetter(user.getRealName()));
            repo.save(user);

            //如果添加用户时选择了用户拥有的角色
            if (roleIds != null && roleIds.length > 0) {
                List<UserRoleMapping> mappings = new ArrayList<>(roleIds.length);
                for (String roleId : roleIds) {
                    UserRoleMapping urm = new UserRoleMapping();
                    Role r = new Role();
                    r.setId(roleId);
                    urm.setUser(user);
                    urm.setRole(r);
                    mappings.add(urm);
                }
                urmRepo.saveAll(mappings);
            }

            resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
        } catch (Exception e) {
            LOGGER.error("UserService.save方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    public ResponseData<Boolean> update(User user, String[] roleIds) {
        ResponseData<Boolean> resp;
        try {
            User existUser;
            if (!StringUtils.isEmpty(user.getEleEquipCode())) {
                existUser = repo.findByEleEquipCodeAndDeleteFlag(user.getEleEquipCode(), 0);
                if (existUser != null && !existUser.getId().equals(user.getId())) {
                    resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL_EQUIP_EXIST);
                    return resp;
                }
            }

            existUser = repo.getOne(user.getId());
            existUser.setAddress(user.getAddress());
            existUser.setEleEquipCode(user.getEleEquipCode());
            existUser.setEmail(user.getEmail());
            existUser.setIdCard(user.getIdCard());
            existUser.setOrderNo(user.getOrderNo());
            existUser.setOrg(user.getOrg());
            existUser.setPhone(user.getPhone());
            existUser.setRealName(user.getRealName());
            existUser.setRealNamePyCode(StringLetterUtils.getFirstLetter(user.getRealName()));
            if (!StringUtils.isEmpty(user.getHeadImage())) {
                FileUtils.deleteFile(uploadFolder + existUser.getHeadImage());
                existUser.setHeadImage(user.getHeadImage());
            }
            if (!StringUtils.isEmpty(user.getSignImage())) {
                FileUtils.deleteFile(uploadFolder + existUser.getSignImage());
                existUser.setSignImage(user.getSignImage());
            }

            repo.save(existUser);

            //如果添加用户时选择了用户拥有的角色
            if (roleIds != null && roleIds.length > 0) {
                List<UserRoleMapping> mappings = new ArrayList<>(roleIds.length);
                for (String roleId : roleIds) {
                    UserRoleMapping urm = new UserRoleMapping();
                    Role r = new Role();
                    r.setId(roleId);
                    urm.setUser(user);
                    urm.setRole(r);
                    mappings.add(urm);
                }
                urmRepo.deleteByUserIds(new String[]{existUser.getId()});
                urmRepo.saveAll(mappings);
            }

            resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
        } catch (Exception e) {
            LOGGER.error("UserService.update方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    /**
     * 更新当前登录用户的个人信息
     *
     * @param user
     * @return
     */
    public ResponseData<Boolean> updateProfile(User user, String defaultMenuId) {
        ResponseData<Boolean> resp;
        try {
            //更新用户偏好设置
            UserSettings userSettings = usRepo.findByUserId(user.getId());
            if (userSettings == null) {
                userSettings = new UserSettings();
            }
            Resource defaultMenu = null;
            if (!StringUtils.isEmpty(defaultMenuId)) {
                defaultMenu = resRepo.findById(defaultMenuId).get();
            }
            userSettings.setDefaultMenu(defaultMenu);
            usRepo.save(userSettings);
            Context.setCurrentUserSettings(userSettings);

            //更新用户个人信息
            User existUser = repo.getOne(user.getId());
            existUser.setAddress(user.getAddress());
            existUser.setEmail(user.getEmail());
            existUser.setIdCard(user.getIdCard());
            existUser.setPhone(user.getPhone());

            if (!StringUtils.isEmpty(user.getHeadImage())) {
                existUser.setHeadImage(user.getHeadImage());
            }
            if (!StringUtils.isEmpty(user.getSignImage())) {
                existUser.setSignImage(user.getSignImage());
            }
            existUser.setUserSettings(userSettings);
            repo.save(existUser);

            resp = ResponseData.getFail(ResponseMsg.UPDATE_SUCCESS);
        } catch (Exception e) {
            LOGGER.error("UserService.updateProfile方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    /**
     * 根据用户id批量删除用户信息
     *
     * @param ids
     * @return
     */
    public ResponseData<Boolean> deleteByIds(String[] ids) {
        ResponseData<Boolean> resp;
        for (String id : ids) {
            if (id.equals("0")) {
                resp = ResponseData.getFail(ResponseMsg.DELETE_FAIL_ADMIN);
                return resp;
            }
        }
        try {
            urmRepo.deleteByUserIds(ids);
            resPermRepo.deleteByOwner("U", ids);
            orgPermRepo.deleteByOwner("U", ids);
            repo.deleteByIds(ids);
            resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
        } catch (Exception e) {
            LOGGER.error("UserService.deleteByIds方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    /**
     * 批量更新用户可用状态
     *
     * @param enable
     * @param ids
     * @return 操作响应信息
     */
    public ResponseData<Boolean> updateEnableState(int enable, String[] ids) {
        ResponseData<Boolean> resp;
        try {
            repo.updateEnableState(enable, ids);
            resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
        } catch (Exception e) {
            LOGGER.error("UserService.updateEnableState方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    /**
     * 更新当前登录用户的密码
     *
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 操作响应信息
     */
    public ResponseData<Boolean> updatePassword(String oldPassword, String newPassword) {
        ResponseData<Boolean> resp;

        User currUser = Context.getCurrentUser();
        try {
            newPassword = MD5Pwd.MD5Pwd(currUser.getUsername(), newPassword);
            oldPassword = MD5Pwd.MD5Pwd(currUser.getUsername(), oldPassword);
            if (!currUser.getPassword().equals(oldPassword)) {
                resp = ResponseData.getFail(ResponseMsg.OLD_PASSWORD_WRONG);
                return resp;
            }

            Date passwordExpireDate = null;
            if (passwordEffectiveDays > 0){
                //更新密码到期时间
                Calendar ca = Calendar.getInstance();
                ca.set(Calendar.DATE, passwordEffectiveDays);
                passwordExpireDate = ca.getTime();
            }

            repo.updatePassword(newPassword, passwordExpireDate, currUser.getId());
            Context.getCurrentUser().setPassword(newPassword);
            resp = ResponseData.getSuccess("操作成功。");
        } catch (Exception e) {
            LOGGER.error("UserService.updatePassword方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    /**
     * 重置当前登录用户的密码和初始化状态
     *
     * @param userIds 用户id
     * @return 操作响应信息
     */
    public ResponseData<Boolean> resetPassword(String[] userIds) {
        ResponseData<Boolean> resp;
        try {
            List<User> users = repo.findByIdInAndDeleteFlag(userIds, 0);
            for (User user : users) {
                String password = MD5Pwd.MD5Pwd(user.getUsername(), defaultPassword);
                user.setPassword(password);
                user.setInitedFlag(0);
                user.setPasswordExpireDate(null);
            }
            repo.saveAll(users);
            resp = ResponseData.getSuccess(ResponseMsg.RESET_SUCCESS);
        } catch (Exception e) {
            LOGGER.error("UserService.updatePassword方法异常。", e);
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    public ResponseData<Boolean> userInit(String username, String password) {
        ResponseData<Boolean> resp;
        password = MD5Pwd.MD5Pwd(username, password);
        Date passwordExpireDate = null;
        if (passwordEffectiveDays > 0){
            //更新密码到期时间
            Calendar ca = Calendar.getInstance();
            ca.set(Calendar.DATE, passwordEffectiveDays);
            passwordExpireDate = ca.getTime();
        }
        try {
            User user = repo.findByUsername(username);
            user.setPassword(password);
            user.setInitedFlag(1);
            user.setPasswordExpireDate(passwordExpireDate);
            resp = ResponseData.getSuccess("用户初始化成功，请使用新密码重新登录。");
        } catch (Exception e) {
            resp = ResponseData.getError(e);
        }
        return resp;
    }

    /**
     * 用户登录
     * @param username  用户名
     * @param password  密码
     * @param clientIp  客户端ip
     * @return 登录响应
     * 登录成功时：
     *      ResponseData.data = 0 正常
     *      ResponseData.data = 1 账户需要初始化
     *      ResponseData.data = 2 密码临近有效期，询问是否修改密码
     * 登录失败时：
     *      ResponseData.data = 0 正常
     *      ResponseData.data = 1 需要显示验证码
     *      ResponseData.data = 2 密码到期，提示修改密码password
     */
    public LoginResponseData<String> userLogin(String username, String password, boolean isPwdEncrypted, String clientIp) {
        LoginResponseData<String> resp = null;
        boolean loginResult = false;

        LoginFailRecord failRecord = null;
        String defaultDesktopUrl = "/index";
        try {
            failRecord = failRecordRepo.findByIp(clientIp);
            if (failRecord != null && failRecord.getNextLoginTime() != null) {
                LocalDateTime nextLoginTime = failRecord.getNextLoginTime();
                LocalDateTime now = LocalDateTime.now();
                if (now.isBefore(nextLoginTime)) {
                    //如果当前时间早于允许登录的时间，直接返回提示信息
                    long nextLoginTimeMills = nextLoginTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                    long nowTimeMills = now.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                    String message = "登录失败次数过多，请在" + DateUtils.getDurationBreakdown(nextLoginTimeMills - nowTimeMills) + "后尝试登录。";
                    String verifyCode = failRecord.getLoginFailCount() > this.loginFailShowVerifyCode ? "1" : "0";
                    //resp = ResponseData.getFail(message, verifyCode);
                    resp = LoginResponseData.get(false, message, verifyCode, null);
                    return resp;
                }
            }

            List<SysSetting> sysSettings = InitSysSettings.sysSettings;
            Optional<SysSetting> menuPositionOptional = sysSettings.stream().filter(c -> c.getKey().equals("菜单位置")).findFirst();
            if (menuPositionOptional.isPresent()){
                SysSetting sysSetting = menuPositionOptional.get();
                if ("顶部".equals(sysSetting.getValue())){
                    defaultDesktopUrl = "/indexForHorizontalMenu";
                }
            }

            if (!isPwdEncrypted){
                password = MD5Pwd.MD5Pwd(username, password);
            }

            // 从SecurityUtils里边创建一个 subject
            Subject subject = SecurityUtils.getSubject();
            // 在认证提交前准备 token（令牌）
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);

            // 执行认证登录
            subject.login(token);
            if (subject.isAuthenticated()) {
                //认证成功
                loginResult = true;
                //删除客户端ip的登录失败记录
                failRecordRepo.deleteByIp(clientIp);
                //resp = ResponseData.getSuccess("", "0");
                resp = LoginResponseData.get(true, "", "0", defaultDesktopUrl);

                User currUser = Context.getCurrentUser();
                if (currUser.getPasswordExpireDate() != null && passwordEffectiveDays > 0){
                    //判断当前登录用户的密码距离过期时间还有几天
                    double passwordExpireDateMillis = currUser.getPasswordExpireDate().getTime();
                    double nowMillis = new Date().getTime();
                    int days = new BigDecimal((passwordExpireDateMillis - nowMillis) / DateTimeConst.DAY).setScale(0, RoundingMode.HALF_UP).intValue();

                    if (days <= 1){
                        resp.setMsg("您的密码将于今天过期，是否修改密码？");
                        resp.setData("2");
                    } else if (days <= 5){
                        resp.setMsg("您的密码将于" + days + "天后过期，是否修改密码？");
                        resp.setData("2");
                    }
                }
            }
        } catch (UnknownAccountException | IncorrectCredentialsException e) {
            if (failRecord == null) {
                failRecord = this.saveLoginFailRecord(clientIp);
            } else {
                this.updateLoginFailRecord(failRecord);
            }
            //根据用户登录失败次数设置页面是否显示验证码
            String verifyCode = failRecord.getLoginFailCount() > this.loginFailShowVerifyCode ? "1" : "0";
            //resp = ResponseData.getFail("登录失败，用户名或密码错误。", verifyCode);
            resp = LoginResponseData.get(false, "登录失败，用户名或密码错误。", verifyCode, defaultDesktopUrl);
        } catch (LockedAccountException e) {
            //resp = ResponseData.getFail("登录失败，该账户已被禁用。");
            resp = LoginResponseData.get(false, "登录失败，该账户已被禁用。", null, defaultDesktopUrl);
        } catch (DisabledAccountException e) {
            //账户需要初始化，提示用户修改初始密码
            //resp = ResponseData.getSuccess("", "1");
            resp = LoginResponseData.get(true, "", "1", defaultDesktopUrl);
        } catch (PasswordExpiredException e){
            //账户密码已到期，提示修改密码
            //resp = ResponseData.getFail("登录失败，您的密码已到期，请修改密码。", "2");
            resp = LoginResponseData.get(false, "登录失败，您的密码已到期，请修改密码。", "2", defaultDesktopUrl);
        } catch (Exception e){
            resp = LoginResponseData.get(false, e.getMessage(), null, defaultDesktopUrl);
        } finally {
            this.saveLoginAudit(username, clientIp, "WEB", loginResult);
        }

        return resp;
    }

    public Integer findNextOrderNo(String orgId) {
        if (StringUtils.isEmpty(orgId)) return 1;
        Integer maxOrderNo = repo.findOrgMaxOrderNo(orgId);
        return maxOrderNo == null ? 1 : maxOrderNo + 1;
    }

    /**
     * 根据id查询用户信息
     *
     * @param id
     * @return
     */
    public User findById(String id) {
        return repo.findById(id).get();
    }

    public List<User> findByIds(String[] ids) {
        return repo.findByIdInAndDeleteFlag(ids, 0);
    }

    public List<User> findByIds(String ids) {
        String[] userIds = ids.split(",");
        return repo.findByIdInAndDeleteFlag(userIds, 0);
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username
     * @return
     */
    public User findByUsername(String username) {
        return repo.findByUsername(username);
    }

    /**
     * 查询用户分页信息
     *
     * @param username
     * @param realNamePyCode
     * @param enable
     * @param orgId
     * @param pageIndex
     * @param rows
     * @return
     */
    public Page<User> findUsers(String username, String realNamePyCode, int enable, String orgId, int pageIndex, int rows) {
        Specification<User> spec = (Specification<User>) (root, query, cb) -> {

            List<Predicate> predicates = new LinkedList<>();
            Predicate deletePredicate = cb.equal(root.get("deleteFlag").as(Integer.class), 0);
            predicates.add(deletePredicate);
            if (!StringUtils.isEmpty(username)) {
                Predicate predicate = cb.like(root.get("username").as(String.class), "%" + username + "%");
                predicates.add(predicate);
            }
            if (!StringUtils.isEmpty(realNamePyCode)) {
                Predicate predicate = cb.like(root.get("realNamePyCode").as(String.class), realNamePyCode + "%");
                predicates.add(predicate);
            }
            if (enable != -1) {
                Predicate predicate = cb.equal(root.get("enable").as(Integer.class), enable);
                predicates.add(predicate);
            }
            if (!StringUtils.isEmpty(orgId)) {
                Predicate predicate = cb.equal(root.get("org").get("id").as(String.class), orgId);
                predicates.add(predicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = Sort.by(Direction.ASC, "orderNo");
        Pageable pageable = PageRequest.of(pageIndex - 1, rows, sort);
        Page<User> pageData = null;
        try {
            pageData = repo.findAll(spec, pageable);
        } catch (Exception e) {
            LOGGER.error("UserService.findUsers方法异常。", e);
        }
        return pageData;
    }

    public ResponseData<Boolean> importUsers(MultipartFile file) {
        ResponseData<Boolean> resp = null;
//        User currentUser = Context.getCurrentUser();
//
//        Workbook workbook = new Workbook();
//        List<String> repeateUserNames = new ArrayList<>();
//        try (InputStream is = file.getInputStream()) {
//            workbook.loadFromStream(is, ExcelVersion.Version2013);
//            Worksheet sheet = workbook.getWorksheets().get(0);
//            int lastRowIndex = sheet.getLastRow();
//            if (lastRowIndex > 0) {
//                //分批次导入
//                int pageSize = 100;
//                int pageCount = lastRowIndex % pageSize == 0 ? lastRowIndex / pageSize : lastRowIndex / pageSize + 1;
//
//                for (int page = 0; page < pageCount; page++) {
//                    int startPos = page * pageSize + 1;
//                    int endPos = startPos + pageSize;
//                    if (endPos > lastRowIndex + 1) {
//                        endPos = lastRowIndex + 1;
//                    }
//
//                    // 第一行表头，起始pos需要加1
//                    if (page == 0) {
//                        startPos += 1;
//                    }
//
//                    //取得当前批次用户名，查询数据库中是否存在重复
//                    //取得组织机构名称和角色名称，预查询
//                    List<String> userNames = new ArrayList<>(endPos - startPos);
//                    Set<String> roleNames = new HashSet<String>();
//                    Set<String> orgNames = new HashSet<>();
//                    for (int i = startPos; i < endPos; i++) {
//                        String username = sheet.get(i, 1).getValue().trim();
//                        String orgName = sheet.get(i, 3).getValue().trim();
//                        String defaultRoleName = sheet.get(i, 10).getValue().trim();
//                        userNames.add(username);
//                        if (!StringUtils.isEmpty(orgName)) {
//                            Collections.addAll(orgNames, orgName.split("/"));
//                        }
//                        if (!StringUtils.isEmpty(defaultRoleName)) {
//                            Collections.addAll(roleNames, defaultRoleName.split("/"));
//                        }
//                    }
//                    List<User> sameNameUsers = repo.findByUsernameIn(userNames);
//                    List<Organization> existOrgs = orgRepo.findByNameInAndDeleteFlag(orgNames.toArray(new String[0]), 0);
//                    List<Role> roles = roleRepo.findByNameIn(roleNames.toArray(new String[0]));
//
//                    List<User> newUsers = new ArrayList<>();
//                    List<UserRoleMapping> newUserRoleMappings = new ArrayList<>();
//                    for (int i = startPos; i < endPos; i++) {
//                        String userName = sheet.get(i, 1).getValue().trim();
//                        String realName = sheet.get(i, 2).getValue().trim();
//                        String orgNameStr = sheet.get(i, 3).getValue().trim();
//
//                        //必填项验证
//                        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(realName) || StringUtils.isEmpty(orgNameStr)) {
//                            continue;
//                        }
//
//                        //用户名是否重复
//                        if (sameNameUsers.stream().anyMatch(c -> c.getUsername().equals(userName))
//                                || repeateUserNames.stream().anyMatch(c -> c.equals(userName))) {
//                            continue;
//                        } else {
//                            repeateUserNames.add(userName);
//                        }
//
//                        String password = MD5Pwd.MD5Pwd(userName, defaultPassword);
//                        String staffCode = sheet.get(i, 4).getValue().trim();
//                        String email = sheet.get(i, 5).getValue().trim();
//                        String phone = sheet.get(i, 6).getValue().trim();
//                        String idNumber = sheet.get(i, 7).getValue().trim();
//                        String address = sheet.get(i, 8).getValue().trim();
//                        String orderNoStr = sheet.get(i, 9).getValue().trim();
//                        String defaultRoleName = sheet.get(i, 10).getValue().trim();
//                        int orderNo = 100;
//                        try {
//                            orderNo = Integer.parseInt(orderNoStr);
//                        } catch (NumberFormatException e) {
//                        }
//
//                        //查询所属组织
//                        //如果组织不存在，或上下级关系有误则结束本次循环，读取下一行
//                        //如果当前登录用户非admin，判断该组织是否处于登录用户所属组织或下级组织
//                        boolean isParentOrgExist = false;
//                        boolean isParentOrgAllowed = false;    //为true时，表示可以保存该组织下的用户
//                        Organization parentOrg = null;
//                        String[] parentOrgNames = orgNameStr.split("/");
//                        for (int k = 0; k < parentOrgNames.length; k++) {
//                            final String queryOrgName = parentOrgNames[k];
//                            List<Organization> parentOrgs = existOrgs.stream().filter(c -> c.getName().equals(queryOrgName)).collect(Collectors.toList());
//                            if (ListUtils.isListEmpty(parentOrgs)) {
//                                break;
//                            }
//
//                            if (parentOrgs.size() == 1) {
//                                parentOrg = parentOrgs.get(0);
//                                if (!currentUser.getUsername().equals("admin") && currentUser.getOrg().getId().equals(parentOrg.getId())) {
//                                    isParentOrgAllowed = true;
//                                }
//                            } else {
//                                final Organization queryOrg = parentOrg;
//                                Optional<Organization> currParentOrgOptional = parentOrgs.stream().filter(c -> c.getParent() != null && c.getParent().getId().equals(queryOrg.getId())).findFirst();
//                                if (currParentOrgOptional.isPresent()) {
//                                    parentOrg = currParentOrgOptional.get();
//                                    if (!currentUser.getUsername().equals("admin") && currentUser.getOrg().getId().equals(parentOrg.getId())) {
//                                        isParentOrgAllowed = true;
//                                    }
//                                } else {
//                                    break;
//                                }
//                            }
//
//                            if (k == parentOrgNames.length - 1) {
//                                isParentOrgExist = true;
//                            }
//                        }
//
//                        if (!isParentOrgExist) {
//                            continue;
//                        }
//                        if (!currentUser.getUsername().equals("admin") && !isParentOrgAllowed) {
//                            continue;
//                        }
//
//                        //初始化用户
//                        User user = new User();
//                        user.setUsername(userName);
//                        user.setPassword(password);
//                        user.setRealName(realName);
//                        user.setRealNamePyCode(StringLetterUtils.getFirstLetter(realName));
//                        user.setStaffCode(staffCode);
//                        user.setAddress(address);
//                        user.setEmail(email);
//                        user.setEnable(1);
//                        user.setIdCard(idNumber);
//                        user.setPhone(phone);
//                        user.setOrderNo(orderNo);
//                        user.setOrg(parentOrg);
//                        user.setDeleteFlag(0);
//                        user.setInitedFlag(0);
//                        newUsers.add(user);
//
//                        //初始化用户角色关系
//                        if (!StringUtils.isEmpty(defaultRoleName)) {
//                            String[] defaultRoleNames = defaultRoleName.split("/");
//                            for (String roleName : defaultRoleNames) {
//                                Role role = this.findMyRole(roles, user.getOrg(), roleName, currentUser.getOrg());
//                                if (role != null) {
//                                    UserRoleMapping urm = new UserRoleMapping();
//                                    urm.setUser(user);
//                                    urm.setRole(role);
//                                    newUserRoleMappings.add(urm);
//                                }
//                            }
//                        }
//                    }
//
//                    if (!ListUtils.isListEmpty(newUsers)) {
//                        repo.saveAll(newUsers);
//                    }
//                    if (!ListUtils.isListEmpty(newUserRoleMappings)) {
//                        urmRepo.saveAll(newUserRoleMappings);
//                    }
//                }
//            }
//
//            resp = ResponseData.getSuccess(ResponseMsg.IMPORT_SUCCESS);
//        } catch (IOException e) {
//            e.printStackTrace();
//            resp = ResponseData.getError(e);
//        }
        return resp;
    }

    private void saveLoginAudit(String username, String ip, String from, boolean loginResult) {
        LoginAudit loginAudit = new LoginAudit(username, ip, new Date(), from);
        loginAudit.setResult(loginResult ? 1 : 0);
        loginAuditRepo.save(loginAudit);
    }

    private LoginFailRecord saveLoginFailRecord(String ip) {
        LoginFailRecord record = new LoginFailRecord();
        record.setIp(ip);
        record.setLastFailTime(LocalDateTime.now());
        record.setLoginFailCount(1);
        record.setNextLoginTime(null);
        failRecordRepo.save(record);
        return record;
    }

    private void updateLoginFailRecord(LoginFailRecord record) {
        LocalDateTime now = LocalDateTime.now();
        record.setLoginFailCount(record.getLoginFailCount() + 1);
        record.setLastFailTime(now);
        int retryFrequency = record.getLoginFailCount() - this.loginFailEnableDisable;
        if (retryFrequency > 0) {
            long disableTimeMills = this.getDisableTimeMills(retryFrequency);
            LocalDateTime nextLoginTime = now.plus(disableTimeMills, ChronoUnit.MILLIS);
            record.setNextLoginTime(nextLoginTime);
        }
        failRecordRepo.save(record);
    }

    private long getDisableTimeMills(int failCount) {
        long disableTimeMills = 0;
        switch (failCount) {
            case 1:
                disableTimeMills = DateTimeConst.SECOND * 30;
                break;
            case 2:
                disableTimeMills = DateTimeConst.MINUTE;
                break;
            case 3:
                disableTimeMills = DateTimeConst.MINUTE * 5;
                break;
            case 4:
                disableTimeMills = DateTimeConst.MINUTE * 10;
                break;
            case 5:
                disableTimeMills = DateTimeConst.MINUTE * 20;
                break;
            case 6:
                disableTimeMills = DateTimeConst.MINUTE * 30;
                break;
            case 7:
                disableTimeMills = DateTimeConst.HOUR;
                break;
            case 8:
            case 9:
            case 10:
                disableTimeMills = DateTimeConst.HOUR * (failCount - 2);
                break;
            default:
                disableTimeMills = DateTimeConst.DAY;
        }
        return disableTimeMills;
    }

    /**
     * 递归查询用户默认角色
     *
     * @param roles        角色集合
     * @param currOrg      在当前组织下查询角色
     * @param roleName     要查找的角色名称
     * @param managerOrg   管理员所在组织
     * @return
     */
    private Role findMyRole(List<Role> roles, Organization currOrg, String roleName, Organization managerOrg) {
        Role r = null;

        if (ListUtils.isListEmpty(roles)) {
            return null;
        }

        if (managerOrg == null) {
            Optional<Role> roleOptional = roles.stream().filter(c -> c.getOrganization() == null && c.getName().equals(roleName)).findAny();
            r = roleOptional.orElse(null);
        } else {
            if (currOrg.getLevel() >= managerOrg.getLevel()){
                //仅在当前组织层级在管理员所属组织下级或平级时进行查找
                Optional<Role> currOrgRoleOptional = roles.stream().
                        filter(c -> c.getOrganization() != null && c.getOrganization().getId().equals(currOrg.getId()) && c.getName().equals(roleName))
                        .findAny();
                if (currOrgRoleOptional.isPresent()){
                    r = currOrgRoleOptional.get();
                } else if (currOrg.getParent() != null) {
                    r = this.findMyRole(roles, currOrg.getParent(), roleName, managerOrg);
                }
            }
        }
        return r;
    }
}