package com.loscy.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loscy.wiki.domain.User;
import com.loscy.wiki.domain.UserExample;
import com.loscy.wiki.mapper.UserMapper;
import com.loscy.wiki.req.UserQueryReq;
import com.loscy.wiki.req.UserSaveReq;
import com.loscy.wiki.resp.UserQueryResp;
import com.loscy.wiki.resp.PageResp;
import com.loscy.wiki.util.CopyUtil;
import com.loscy.wiki.util.SnowFlake;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {


    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }

        PageHelper.startPage(req.getPage(), req.getSize());
        List<User> userlist = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userlist);

        //列表复制
        List<UserQueryResp> list = CopyUtil.copyList(userlist, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;

    }

    /*
    * 保存
    * */
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        if(ObjectUtils.isEmpty(req.getId())) {
            user.setId(snowFlake.nextId());
            userMapper.insert(user);
        }else {
            userMapper.updateByPrimaryKey(user);
        }
    }

    /*
    * 删除
    * */
    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }
}
