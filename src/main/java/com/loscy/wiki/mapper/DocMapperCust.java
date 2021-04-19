package com.loscy.wiki.mapper;

import com.loscy.wiki.domain.Test;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DocMapperCust {

    void increaseViewCount (@Param("id") Long id);

    void increaseVoteCount (@Param("id") Long id);
}
