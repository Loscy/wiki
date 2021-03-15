package com.loscy.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loscy.wiki.domain.Ebook;
import com.loscy.wiki.domain.EbookExample;
import com.loscy.wiki.mapper.EbookMapper;
import com.loscy.wiki.req.EbookReq;
import com.loscy.wiki.resp.EbookResp;
import com.loscy.wiki.util.CopyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {


    @Resource
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req){
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%"+req.getName()+"%");
        }

        PageHelper.startPage(1,3);
        List<Ebook> ebooklist= ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebooklist);
        pageInfo.getTotal();
        pageInfo.getPages();

        //List<EbookResp> respList = new ArrayList<>();
        //for (Ebook ebook : ebooklist) {
        //    //EbookResp ebookResp = new EbookResp();
        //    //BeanUtils.copyProperties(ebook, ebookResp);
        //
        //    EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);
        //    respList.add(ebookResp);
        //}

        List<EbookResp> list = CopyUtil.copyList(ebooklist, EbookResp.class);
        return list;

    }
}
