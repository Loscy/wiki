package com.loscy.wiki.service;

import com.loscy.wiki.mapper.EbookSnapshotMapperCust;
import com.loscy.wiki.resp.StatisticResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookSnapshotService {

    @Resource
    private EbookSnapshotMapperCust ebookSnapshotMapperCust;

    public void getSnap() {
        ebookSnapshotMapperCust.genSnapshot();
    }

    /*
    * 获取首页数值数据
    * */

    public List<StatisticResp> getStatistic() {
        return ebookSnapshotMapperCust.getStatistic();
    }
}
