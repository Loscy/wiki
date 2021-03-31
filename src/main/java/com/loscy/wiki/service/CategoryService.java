package com.loscy.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loscy.wiki.domain.Category;
import com.loscy.wiki.domain.CategoryExample;
import com.loscy.wiki.mapper.CategoryMapper;
import com.loscy.wiki.req.CategoryQueryReq;
import com.loscy.wiki.req.CategorySaveReq;
import com.loscy.wiki.resp.CategoryQueryResp;
import com.loscy.wiki.resp.PageResp;
import com.loscy.wiki.util.CopyUtil;
import com.loscy.wiki.util.SnowFlake;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryService {


    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;

    public List<CategoryQueryResp> all() {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        List<Category> categorylist = categoryMapper.selectByExample(categoryExample);

        //列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categorylist, CategoryQueryResp.class);

        return list;

    }
    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {
        CategoryExample categoryExample = new CategoryExample();
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        categoryExample.setOrderByClause("sort asc");
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Category> categorylist = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categorylist);

        //List<CategoryResp> respList = new ArrayList<>();
        //for (Category category : categorylist) {
        //    //CategoryResp categoryResp = new CategoryResp();
        //    //BeanUtils.copyProperties(category, categoryResp);
        //
        //    CategoryResp categoryResp = CopyUtil.copy(category, CategoryResp.class);
        //    respList.add(categoryResp);
        //}

        //列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categorylist, CategoryQueryResp.class);

        PageResp<CategoryQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;

    }

    /*
    * 保存
    * */
    public void save(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        if(ObjectUtils.isEmpty(req.getId())) {
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }else {
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    /*
    * 删除
    * */
    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }
}
