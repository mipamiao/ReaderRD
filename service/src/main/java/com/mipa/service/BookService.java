package com.mipa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mipa.common.dto.bookdto.BookRequestDTO;
import com.mipa.common.configuration.MyConfiguration;
import com.mipa.common.utils.CopyProperties;
import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.BookWithAuthorVO;
import com.mipa.common.vo.BookWithTagsVO;

import com.mipa.mapper.BookMapper;
import com.mipa.mapper.BookTagMapper;
import com.mipa.mapper.TagMapper;
import com.mipa.mapper.UserMapper;
import com.mipa.model.*;
import com.mipa.service.api.IBookService;
import com.mipa.utils.IdUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mipa.common.vo.BookWithTagAndAuthorNameVO;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService implements IBookService {


    @Autowired
    BookMapper bookMapper;

    @Autowired
    BookTagMapper bookTagMapper;

    @Autowired
    TagMapper tagMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    FileService fileService;

    @Autowired
    MyConfiguration config;

    @Transactional(readOnly = true)
    @Override
    public PageRecord<BookWithTagAndAuthorNameVO> findByPageable(int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        var bookWithAuthors = bookMapper.selectAllBookAndAuthor();
        var pageInfo = new PageInfo<>(bookWithAuthors);
        if(pageInfo.getList().isEmpty()) return PageRecord.of(Collections.emptyList(), pageInfo);
        var bookWithTags = bookTagMapper.selectBookAndTagsByBookIds(bookWithAuthors.stream().map(item -> item.getId()).toList());
        return PageRecord.of(combine(bookWithAuthors, bookWithTags), pageInfo);
    }

    @Transactional(readOnly = true)
    @Override
    public PageRecord<BookWithTagAndAuthorNameVO> findByCategory(String category, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        var bookWithAuthors = bookMapper.selectAllBookAndAuthorByCategory(category);
        var pageInfo = new PageInfo<>(bookWithAuthors);
        if(pageInfo.getList().isEmpty()) return PageRecord.of(Collections.emptyList(), pageInfo);
        var bookWithTags = bookTagMapper.selectBookAndTagsByBookIds(bookWithAuthors.stream().map(item -> item.getId()).toList());
        return PageRecord.of(combine(bookWithAuthors, bookWithTags), pageInfo);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<BookWithTagAndAuthorNameVO> findById(String bookId) {
        var bookWithAuthor = bookMapper.selectBookAndAuthorById(bookId);
        if (bookWithAuthor.isPresent()) {
            var res = CopyProperties.run(bookWithAuthor.get(), BookWithTagAndAuthorNameVO.class);
            var bookWithTag = bookTagMapper.selectBookAndTagsByBookIds(List.of(bookId));
            if (!bookWithTag.isEmpty()) res.setTagNames(bookWithTag.get(0).getTagNames());
            return Optional.of(res);
        }
        return Optional.empty();
    }

    @Transactional
    @Override
    public Boolean addBook(BookRequestDTO bookRequestDTO, String userId) {
        var book = CopyProperties.run(bookRequestDTO, Book.class);
        var bookId = IdUtil.uuid();
        book.setId(bookId);
        book.setAuthorId(userId);
        bookMapper.insert(book);
        addBookTag(bookRequestDTO.getTags(), bookId);
        return true;
    }

    @Transactional
    @Override
    public Boolean updateBook(BookRequestDTO bookRequestDTO, String userId, String bookId) {
        var userOpt = userMapper.selectById(userId) ;
        if(userOpt.isPresent()){
            var user = userOpt.get();
            var bookOpt = bookMapper.selectById(bookId);
            if(bookOpt.isPresent()){
                var existingBook = bookOpt.get();
                if( existingBook.getAuthorId().equals(userId)){
                    // 仅更新允许修改的字段
                    existingBook.setName(bookRequestDTO.getName());
                    existingBook.setDescription(bookRequestDTO.getDescription());
                    existingBook.setCoverUrl(bookRequestDTO.getCoverUrl());
                    existingBook.setCategory(bookRequestDTO.getCategory());
                    existingBook.setChapterCount(bookRequestDTO.getChapterCount());
                    bookMapper.update(existingBook);

                    //更新tag

                    return updateBookTag(bookRequestDTO.getTags(), bookId);
                }
            }
        }
        return false;
    }

    @Transactional
    @Override
    public Boolean deleteBook(String bookId, String userId) {
        var userOpt = userMapper.selectById(userId);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            var bookOpt = bookMapper.selectById(bookId);
            if (bookOpt.isPresent()) {
                var existingBook = bookOpt.get();
                if (existingBook.getAuthorId().equals(userId)) {
                    bookMapper.delete(bookId);
                    bookTagMapper.deleteByBookId(bookId);
                    clearBookTags(bookId);
                    return true;
                }
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    @Override
    public PageRecord<BookWithTagAndAuthorNameVO> getBooksByUserId(String userId, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        var bookWithAuthors = bookMapper.selectAllBookAndAuthorByAuthorId(userId);
        var pageInfo = new PageInfo<>(bookWithAuthors);
        if(pageInfo.getList().isEmpty()) return PageRecord.of(Collections.emptyList(), pageInfo);
        var bookWithTags = bookTagMapper.selectBookAndTagsByBookIds(bookWithAuthors.stream().map(item -> item.getId()).toList());
        return PageRecord.of(combine(bookWithAuthors, bookWithTags), pageInfo);
    }


    @Transactional
    @Override
    public String updateCoverImage(MultipartFile file, String bookId, String userId) {
        if (file.isEmpty()) return null;

        var userOpt = userMapper.selectById(userId);
        if (userOpt.isEmpty()) return null;
        var user = userOpt.get();

        var bookOpt = bookMapper.selectById(bookId);
        if (bookOpt.isEmpty()) return null;
        var book = bookOpt.get();

        if (book.getAuthorId().equals(userId)) {//todo n+1

            fileService.createDirIfNotExist(config.bookCoverImgsDstDir);
            String newFilename = fileService.generateUniqueFileName(
                    file.getOriginalFilename(), bookId
            );

            Path path = Paths.get(fileService.combinePath(config.bookCoverImgsDstDir, newFilename));
            if (!fileService.saveSmall(file, path)) return null;

            var resultUrl = fileService.combinePath( config.bookCoverImgsSrcDir, newFilename);
            if (book.getCoverUrl() != null) {
                var oldCoverPath = book.getCoverUrl().replace(config.bookCoverImgsSrcDir, config.bookCoverImgsDstDir);
                fileService.deleteSmall(oldCoverPath);
            }
            if (bookMapper.updateCoverUrl(bookId, resultUrl) == 1)
                return resultUrl;
        }
        return null;
    }

    private boolean updateBookTag(List<String> tagNames, String book_id){
        return add_or_update_book_tag(tagNames, book_id, true);
    }

    private boolean addBookTag(List<String> tagNames, String book_id) {
        return add_or_update_book_tag(tagNames, book_id, false);
    }

    //todo 改一下为批量插入
    private boolean add_or_update_book_tag(List<String> tagNames, String book_id, boolean need_del) {
        var tags = tagNames.stream().map(tagName -> {
            var tagOpt = tagMapper.selectByName(tagName);
            if (tagOpt.isEmpty()) {
                var tag = new Tag();
                tag.setId(IdUtil.uuid());
                tag.setName(tagName);
                tagMapper.insert(tag);
                return tag;
            } else
                return tagOpt.get();
        }).toList();

        var bookTags = tags.stream().map(tag -> {
            return new BookTag(IdUtil.uuid(), book_id, tag.getId());
        }).toList();

        if (need_del) bookTagMapper.deleteByBookId(book_id);
        bookTagMapper.insertBatch(bookTags);
        return true;
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

    /**
     * 删除书籍的同时要删除booktag记录，而要是一个tag没有书籍在使用，则也要删除
     */
    private void clearBookTags(String bookId){

    }
}
