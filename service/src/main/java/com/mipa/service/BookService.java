package com.mipa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mipa.common.Constant.ExMsg;
import com.mipa.common.annotation.PageCacheChild;
import com.mipa.common.annotation.PageCacheCut;
import com.mipa.common.annotation.PageCacheRoot;
import com.mipa.common.dto.bookdto.BookRequestDTO;
import com.mipa.common.configuration.MyConfiguration;
import com.mipa.common.exception.BizException;
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

import com.mipa.validate.VerifyRelationShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mipa.common.vo.BookWithTagAndAuthorNameVO;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
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

    @PageCacheRoot(fieldName = "BookService", pageNumberParamIndex = "p0", pageSizeParamIndex = "p1")
    @Transactional(readOnly = true)
    @Override
    public PageRecord<BookWithTagAndAuthorNameVO> findByPageable(int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        var bookWithAuthors = bookMapper.selectAllBookAndAuthor();
        var pageInfo = new PageInfo<>(bookWithAuthors);
        if (pageInfo.getList().isEmpty()) return PageRecord.of(Collections.emptyList(), pageInfo);
        var bookWithTags = bookTagMapper.selectBookAndTagsByBookIds(bookWithAuthors.stream().map(item -> item.getId()).toList());
        return PageRecord.of(combine(bookWithAuthors, bookWithTags), pageInfo);
    }

    @PageCacheRoot(fieldName = "BookService", pageNumberParamIndex = "p1", pageSizeParamIndex = "p2", extraFieldInfo = "category_${p0}")
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
    public BookWithTagAndAuthorNameVO findById(String bookId) {
        var bookWithAuthor = bookMapper.selectBookAndAuthorById(bookId);
        if (bookWithAuthor.isEmpty())
            throw new BizException(HttpStatus.BAD_REQUEST, ExMsg.BOOK_NOT_EXIST);

        var res = CopyProperties.run(bookWithAuthor.get(), BookWithTagAndAuthorNameVO.class);
        var bookWithTag = bookTagMapper.selectBookAndTagsByBookIds(List.of(bookId));
        if (!bookWithTag.isEmpty()) res.setTagNames(bookWithTag.get(0).getTagNames());

        return res;
    }

    @Transactional
    @Override
    public Boolean addBook(BookRequestDTO bookRequestDTO, String userId) {
        var book = CopyProperties.run(bookRequestDTO, Book.class);
        var bookId = IdUtil.uuid();
        book.setId(bookId);
        book.setAuthorId(userId);

        try {
            bookMapper.insert(book);
            addBookTag(bookRequestDTO.getTags(), bookId);
        } catch (DataIntegrityViolationException e) {
            throw new BizException(HttpStatus.BAD_REQUEST, ExMsg.DB_CONSTRAIN_FAILED);
        }
        return true;
    }

    @PageCacheChild(fieldName = "BookService")
    @Transactional
    @Override
    public void updateBook(String bookId, BookRequestDTO bookRequestDTO, String userId ) {
        var vr = VerifyRelationShip.start()
                .verifyAuthorAndBook(userId, bookId, bookMapper);
        if (vr.isSucceed()) {
            var book = vr.get(Book.class);
            book.setName(bookRequestDTO.getName());
            book.setDescription(bookRequestDTO.getDescription());
            book.setCategory(bookRequestDTO.getCategory());
            try {
                bookMapper.update(book);
                updateBookTag(bookRequestDTO.getTags(), bookId);
            } catch (DataIntegrityViolationException e) {
                throw new BizException(HttpStatus.BAD_REQUEST, ExMsg.DB_CONSTRAIN_FAILED);
            }
        } else {
            throw new BizException(HttpStatus.BAD_REQUEST, ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.NOT_AUTHOR));
        }
    }

    @PageCacheCut(fieldName = "BookService")
    @Transactional
    @Override
    public void deleteBook(String bookId, String userId) {
        var vr = VerifyRelationShip.start()
                .verifyAuthorAndBook(userId, bookId, bookMapper);
        if(vr.isSucceed()){
            try{
                bookMapper.delete(bookId);
                bookTagMapper.deleteByBookId(bookId);
                clearBookTags(bookId);
            }catch (DataIntegrityViolationException e) {
                throw new BizException(HttpStatus.BAD_REQUEST, ExMsg.DB_CONSTRAIN_FAILED);
            }
        }else {
            throw new BizException(HttpStatus.BAD_REQUEST, ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.NOT_AUTHOR));
        }

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


    @PageCacheChild(fieldName = "BookService", idIndex = 1)
    @Transactional
    @Override
    public String updateCoverImage(MultipartFile file, String bookId, String userId) {
        if (file.isEmpty())
            throw new BizException(HttpStatus.BAD_REQUEST, ExMsg.EMPTY_FILE);

        var vr = VerifyRelationShip.start()
                .verifyAuthorAndBook(userId, bookId, bookMapper);

        if (vr.isSucceed()) {
            var book = vr.get(Book.class);

            fileService.createDirIfNotExist(config.bookCoverImgsDstDir);
            String newFilename = fileService.generateUniqueFileName(
                    file.getOriginalFilename(), bookId
            );

            Path path = Paths.get(fileService.combinePath(config.bookCoverImgsDstDir, newFilename));
            if (!fileService.saveSmall(file, path))
                throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.FILE_SAVE_ERROR);

            var resultUrl = fileService.combinePath( config.bookCoverImgsSrcDir, newFilename);
            if (book.getCoverUrl() != null) {
                var oldCoverPath = book.getCoverUrl().replace(config.bookCoverImgsSrcDir, config.bookCoverImgsDstDir);
                fileService.deleteSmall(oldCoverPath);
            }
            try{
                bookMapper.updateCoverUrl(bookId, resultUrl);
            }catch (DataIntegrityViolationException e) {
                throw new BizException(HttpStatus.BAD_REQUEST, ExMsg.DB_CONSTRAIN_FAILED);
            }
            return resultUrl;
        }else {
            throw new BizException(HttpStatus.BAD_REQUEST, ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.NOT_AUTHOR));
        }
    }

    private boolean updateBookTag(List<String> tagNames, String book_id){
        return addOrUpdateBookTag(tagNames, book_id, true);
    }

    private boolean addBookTag(List<String> tagNames, String book_id) {
        return addOrUpdateBookTag(tagNames, book_id, false);
    }

    @Transactional
    private boolean addOrUpdateBookTag(List<String> tagNames, String bookId, boolean needDel) {
        if(tagNames.isEmpty())return true;
        List<Tag> existingTags = tagMapper.selectByNames(tagNames);
        Map<String, Tag> nameToTag = existingTags.stream()
                .collect(Collectors.toMap(Tag::getName, Function.identity()));

        List<Tag> tagsToInsert = new ArrayList<>();
        for (String tagName : tagNames) {
            if (!nameToTag.containsKey(tagName)) {
                Tag tag = new Tag();
                tag.setId(IdUtil.uuid());
                tag.setName(tagName);
                tagsToInsert.add(tag);
                nameToTag.put(tagName, tag);
            }
        }

        if (!tagsToInsert.isEmpty()) tagMapper.insertBatch(tagsToInsert);
        List<BookTag> bookTags = nameToTag.values().stream()
                .map(tag -> new BookTag(IdUtil.uuid(), bookId, tag.getId()))
                .toList();
        if (needDel) bookTagMapper.deleteByBookId(bookId);
        if (!bookTags.isEmpty())bookTagMapper.insertBatch(bookTags);
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
