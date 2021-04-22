package com.loscy.wiki.service;

import com.loscy.wiki.mapper.EbookSnapshotMapperCust;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ebookSnapshotService {

    @Resource
    private EbookSnapshotMapperCust ebookSnapshotMapperCust;

    public void getSnap() {
        ebookSnapshotMapperCust.genSnapshot();
    }
}
