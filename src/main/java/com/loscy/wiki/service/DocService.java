package com.loscy.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loscy.wiki.domain.Doc;
import com.loscy.wiki.domain.DocExample;
import com.loscy.wiki.mapper.DocMapper;
import com.loscy.wiki.req.DocQueryReq;
import com.loscy.wiki.req.DocSaveReq;
import com.loscy.wiki.resp.DocQueryResp;
import com.loscy.wiki.resp.PageResp;
import com.loscy.wiki.util.CopyUtil;
import com.loscy.wiki.util.SnowFlake;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {


    @Resource
    private DocMapper docMapper;

    @Resource
    private SnowFlake snowFlake;

    public List<DocQueryResp> all() {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        List<Doc> doclist = docMapper.selectByExample(docExample);

        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(doclist, DocQueryResp.class);

        return list;

    }
    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        docExample.setOrderByClause("sort asc");
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> doclist = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(doclist);

        //List<DocResp> respList = new ArrayList<>();
        //for (Doc doc : doclist) {
        //    //DocResp docResp = new DocResp();
        //    //BeanUtils.copyProperties(doc, docResp);
        //
        //    DocResp docResp = CopyUtil.copy(doc, DocResp.class);
        //    respList.add(docResp);
        //}

        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(doclist, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;

    }

    /*
    * 保存
    * */
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        if(ObjectUtils.isEmpty(req.getId())) {
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        }else {
            docMapper.updateByPrimaryKey(doc);
        }
    }

    /*
    * 删除
    * */
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }
}
