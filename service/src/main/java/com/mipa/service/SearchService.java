package com.mipa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mipa.common.utils.CopyProperties;
import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.BookWithAuthorVO;
import com.mipa.common.vo.BookWithTagAndAuthorNameVO;
import com.mipa.common.vo.BookWithTagsVO;
import com.mipa.mapper.BookMapper;
import com.mipa.mapper.BookTagMapper;
import com.mipa.service.api.ISearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SearchService implements ISearchService {
    @Autowired
    BookMapper bookMapper;

    @Autowired
    BookTagMapper bookTagMapper;

    @Override
    public PageRecord<BookWithTagAndAuthorNameVO> searchBooks(String keyword, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        var bookWithAuthors = bookMapper.selectAllBookAndAuthorByKeyword("%" + keyword +"%");
        var pageInfo = new PageInfo<>(bookWithAuthors);
        if (pageInfo.getList().isEmpty()) return PageRecord.of(Collections.emptyList(), pageInfo);
        var bookWithTags = bookTagMapper.selectBookAndTagsByBookIds(bookWithAuthors.stream().map(item -> item.getId()).toList());

        return PageRecord.of(combine(bookWithAuthors, bookWithTags), pageInfo);
    }

    private List<BookWithTagAndAuthorNameVO> combine(List<BookWithAuthorVO> bookWithAuthors, List<BookWithTagsVO> bookWithTags) {
        Map<String, BookWithTagsVO> bookWithTagsMap = bookWithTags.stream()
                .collect(Collectors.toMap(BookWithTagsVO::getId, o -> o));
        return bookWithAuthors.stream().map(
                item -> {
                    var newItem = CopyProperties.run(item, BookWithTagAndAuthorNameVO.class);
                    List<String> tagNames = Optional.ofNullable(bookWithTagsMap.get(newItem.getId()))
                            .map(BookWithTagsVO::getTagNames)
                            .orElse(Collections.emptyList());
                    newItem.setTagNames(tagNames);
                    return newItem;
                }).toList();
    }
}
