package com.xunjia.framework.resource.vo;

import com.xunjia.framework.usermanage.entity.Resource;
import com.xunjia.framework.utils.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class HorizontalMenuVO {

    private Resource menu;

    private boolean hasSub;

    private List<HorizontalMenuVO> subMenus;

    private int level;

    public HorizontalMenuVO setMenu(Resource menu){
        if (menu != null){
            String url = menu.getUrl();
            if (StringUtils.isNotEmpty(url)){
                if (url.contains("?")){
                    url += "&menuId=" + menu.getId();
                } else {
                    url += "?menuId=" + menu.getId();
                }
                menu.setUrl(url);
            }
        }
        this.menu = menu;
        return this;
    }
}
