package com.xunjia.framework.org.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.xunjia.framework.common.vo.PageVO;
import com.xunjia.framework.common.vo.TreeVO;
import com.xunjia.framework.usermanage.entity.Organization;
import com.xunjia.framework.usermanage.entity.User;
import com.xunjia.framework.usermanage.vo.OrgTreeVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.org.service.OrganizationService;
import com.xunjia.framework.orgPermission.service.OrgPermissionService;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.framework.utils.StringUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "组织机构控制器")
@RestController
@RequestMapping("/org")
public class OrganizationController {

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private OrgPermissionService permService;

    @ApiOperation(value = "跳转至添加页面", httpMethod = "GET")
    @RequestMapping("/toAdd")
    @RequiresPermissions("org:save")
    public ModelAndView toAdd() {
        return new ModelAndView("framework/org/add");
    }

    @ApiOperation(value = "跳转至编辑页面", httpMethod = "GET")
    @RequestMapping("/toEdit")
    @RequiresPermissions("org:update")
    public ModelAndView toEdit() {
        ModelAndView mav = new ModelAndView("framework/org/edit");
        return mav;
    }

    @ApiOperation(value = "跳转至数据列表页", httpMethod = "GET")
    @RequestMapping("/toList")
    @RequiresPermissions("org:list")
    public ModelAndView toList() {
        return new ModelAndView("framework/org/list");
    }

    @ApiOperation(value = "保存组织机构信息", httpMethod = "POST")
    @RequestMapping("/save")
    @RequiresPermissions("org:save")
    public ResponseData<Boolean> save(
            @ApiParam(value = "组织机构信息") Organization org,
            @ApiParam(value = "上级组织id") String parentId) {
        if (!StringUtils.isEmpty(parentId)) {
            Organization parentOrg = new Organization();
            parentOrg.setId(parentId);
            org.setParent(parentOrg);
        }
        return orgService.save(org);
    }

    @ApiOperation(value = "更新组织机构信息", httpMethod = "POST")
    @RequestMapping("/update")
    @RequiresPermissions("org:update")
    public ResponseData<Boolean> update(
            @ApiParam(value = "组织机构信息") Organization org,
            @ApiParam(value = "上级组织id") String parentId,
            @ApiParam(value = "原组织代码") String originalCode) {
        if (!StringUtils.isEmpty(parentId)) {
            Organization parentOrg = new Organization();
            parentOrg.setId(parentId);
            org.setParent(parentOrg);
        }
        return orgService.update(org, originalCode);
    }

    @ApiOperation(value = "批量删除组织机构信息", httpMethod = "POST")
    @RequestMapping("/delete")
    @RequiresPermissions("org:delete")
    public ResponseData<Boolean> delete(
            @ApiParam(value = "组织id数组") @RequestParam(name = "ids[]") String[] ids) {
        return orgService.deleteByIds(ids);
    }

    @ApiOperation(value = "更新组织机构可用状态", httpMethod = "POST")
    @RequestMapping("/updateEnableState")
    @RequiresPermissions({"org:enable", "org:disable"})
    public ResponseData<Boolean> updateEnableState(
            @ApiParam(value = "可用状态") int enable,
            @ApiParam(value = "组织id数组") @RequestParam(name = "ids[]") String[] ids) {
        return orgService.updateEnableState(enable, ids);
    }

    @RequestMapping("/importOrganizations")
    @RequiresPermissions("org:import")
    public ResponseData<Boolean> importOrganizations(MultipartRequest request) {
        MultipartFile file = request.getFile("orgFile");
        return orgService.importOrganizations(file);
    }

    @RequestMapping("/findDefaultInfo")
    public Map<String, String> findDefaultInfo(String orgId) {
        Map<String, String> defaultInfo = new HashMap<>();
        Integer nextOrderNo = orgService.findNextOrderNo(orgId);
        defaultInfo.put("nextOrderNo", String.valueOf(nextOrderNo));
        return defaultInfo;
    }

    @ApiOperation(value = "根据给定id查询组织机构信息", httpMethod = "GET")
    @RequestMapping("/findById")
    public Organization findById(@ApiParam(value = "组织id") String id) {
        return orgService.findById(id);
    }

    @ApiOperation(value = "根据给定Code查询组织机构信息", httpMethod = "GET")
    @RequestMapping("/findByCode")
    public Organization findByCode(String code) {
        return orgService.findByCode(code);
    }

    @ApiOperation(value = "查询组织机构分页数据", httpMethod = "GET")
    @RequestMapping("/findOrganizations")
    @RequiresPermissions("org:list")
    public PageVO<Organization> findOrganizations(
            @ApiParam(value = "组织名称") String name,
            @ApiParam(value = "组织代码") String code,
            @ApiParam(value = "拼音码") String pyCode,
            @ApiParam(value = "上级组织id") String parentId,
            @ApiParam(value = "组织分类id") String typeId,
            @ApiParam(value = "组织可用状态") String enable,
            @ApiParam(value = "页号") int page,
            @ApiParam(value = "每页显示条数") int rows) {

        String queryId = null;
        if (StringUtils.isEmpty(parentId)) {
            User currUser = Context.getCurrentUser();
            if (!currUser.getUsername().equals("admin")) {
                queryId = currUser.getOrg().getId();
            }
        } else {
            queryId = parentId;
        }

        int enabled = -1;
        if (!StringUtils.isEmpty(enable)) {
            enabled = Integer.parseInt(enable);
        }

        Page<Organization> pageData = orgService.findOrganizations(name, code, pyCode, queryId, typeId, enabled, page, rows);
        PageVO<Organization> pageVo = new PageVO<Organization>(pageData);
        return pageVo;
    }

    @ApiOperation(value = "查询可用的组织机构分页数据", httpMethod = "GET")
    @RequestMapping("/findEnabledOrganizations")
    @RequiresPermissions("authc")
    public PageVO<Organization> findEnabledOrganizations(
            @ApiParam(value = "组织名称") String name,
            @ApiParam(value = "组织代码") String code,
            @ApiParam(value = "拼音码") String pyCode,
            @ApiParam(value = "上级组织id") String parentId,
            @ApiParam(value = "组织分类id") String typeId,
            @ApiParam(value = "页号") int page,
            @ApiParam(value = "每页显示条数") int rows) {

        String queryId = null;
        if (StringUtils.isEmpty(parentId)) {
            User currUser = Context.getCurrentUser();
            if (!currUser.getUsername().equals("admin")) {
                queryId = currUser.getOrg().getId();
            }
        } else {
            queryId = parentId;
        }

        Page<Organization> pageData = orgService.findOrganizations(name, code, pyCode, queryId, typeId, 1, page, rows);
        PageVO<Organization> pageVo = new PageVO<Organization>(pageData);
        return pageVo;
    }

    @ApiOperation(value = "获取组织机构的树", httpMethod = "GET")
    @RequestMapping("/getOrgTree")
    public List<TreeVO> getOrgTree(@ApiParam(value = "上级组织id") String id) {

        List<TreeVO> treeNodes = new LinkedList<TreeVO>();
        List<Organization> orgList = null;
        User currUser = Context.getCurrentUser();

        String queryId = null;
        if (StringUtils.isEmpty(id)) {    //未传入上级组织id
            if (currUser.getUsername().equals("admin")) {
                orgList = orgService.findByParentId(null);
            } else {
                orgList = orgService.findByParentWithCurrOrg(currUser.getOrg().getId());
                if (currUser.getOrg().getParent() != null) {
                    queryId = currUser.getOrg().getParent().getId();
                }
            }
        } else {
            queryId = id;
            orgList = orgService.findByParentId(queryId);
        }

        return initOrgTreeRoot(id, queryId, orgList, treeNodes);
    }

    @ApiOperation(value = "获取可用组织机构的树(分级管理员授权)", httpMethod = "GET")
    @RequestMapping("/getEnableOrgTree")
    public List<TreeVO> getEnableOrgTree(
            @ApiParam(value = "上级组织id") String id) {
        List<TreeVO> treeNodes = new LinkedList<TreeVO>();

        List<Organization> orgList = null;
        User currUser = Context.getCurrentUser();

        String queryId = null;
        if (StringUtils.isEmpty(id)) {    //未传入上级组织id
            if (currUser.getUsername().equals("admin")) {
                orgList = orgService.findEnableOrgByParentId(null);
            } else {
                orgList = orgService.findEnableOrgByParentWithCurrOrg(currUser.getOrg().getId());
                if (currUser.getOrg().getParent() != null) {
                    queryId = currUser.getOrg().getParent().getId();
                }
            }
        } else {
            queryId = id;
            orgList = orgService.findEnableOrgByParentId(queryId);
        }

        return initOrgTreeRoot(id, queryId, orgList, treeNodes);
    }

    @ApiOperation(value = "获取有权组织机构的树(用户可见树)", httpMethod = "GET")
    @RequestMapping("/getRightOrgTree")
    public List<TreeVO> getRightOrgTree(@ApiParam(value = "上级组织id") String id) {
        List<TreeVO> treeNodes = new LinkedList<TreeVO>();
        List<Organization> authorizedOrgs = null;
        User currUser = Context.getCurrentUser();

        String queryId = null;
        if (StringUtils.isEmpty(id)) {    //未传入上级组织id
            if (currUser.getUsername().equals("admin")) {
                authorizedOrgs = orgService.findEnableOrgByParentId(null);
            } else {
                authorizedOrgs = permService.findAuthorizedOrganizationsWithoutParent(currUser.getId(), currUser.getUsername());
                if (currUser.getOrg().getParent() != null) {
                    queryId = currUser.getOrg().getParent().getId();
                }
            }
        } else {
            queryId = id;
            authorizedOrgs = permService.findAuthorizedOrganizations(currUser.getId(), currUser.getUsername(), id);
        }

        return initOrgTreeRoot(id, queryId, authorizedOrgs, treeNodes);
    }

    private List<TreeVO> initOrgTree(List<Organization> orgList, String parentId) {
        List<TreeVO> treeNodes = new LinkedList<TreeVO>();
        List<Organization> orgs = null;
        if (StringUtils.isEmpty(parentId)) {
            orgs = orgList.stream().filter(c -> c.getParent() == null).collect(Collectors.toList());
        } else {
            orgs = orgList.stream().filter(c -> c.getParent() != null && c.getParent().getId().equals(parentId))
                    .collect(Collectors.toList());
        }

        if (!ListUtils.isListEmpty(orgs)) {
            treeNodes = initOrgTreeNodes(orgs);
        }

        return treeNodes;
    }

    private List<TreeVO> initOrgTreeNodes(List<Organization> orgList) {
        List<TreeVO> treeNodes = null;
        if (!ListUtils.isListEmpty(orgList)) {
            treeNodes = new ArrayList<TreeVO>(orgList.size());
            for (Organization org : orgList) {
                TreeVO treeNode = new OrgTreeVO(org);
                treeNode.setChildren(initOrgTree(orgList, org.getId()));
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("code", org.getCode());
                map.put("typeName", org.getType().getName());
                if (org.getParent() != null) {
                    map.put("parentCode", org.getParent().getCode());
                }
                treeNode.setAttributes(map);
                treeNodes.add(treeNode);
            }
        }
        return treeNodes;
    }

    private List<TreeVO> initOrgTreeRoot(String parentId, String queryId, List<Organization> orgList, List<TreeVO> treeNodes) {
        if (!ListUtils.isListEmpty(orgList)) {
            List<TreeVO> subTreeNodes = initOrgTree(orgList, queryId);
            if (StringUtils.isEmpty(parentId)) {
                TreeVO treeNode = new TreeVO("", "组织机构", TreeVO.OPEN, null);
                treeNode.setChildren(subTreeNodes);
                treeNodes.add(treeNode);
            } else {
                treeNodes.addAll(subTreeNodes);
            }
        }
        return treeNodes;
    }
}
