package com.mipa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mipa.common.dto.bookshelfdto.BookShelfRequestDTO;
import com.mipa.common.utils.CopyProperties;
import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.BookShelfVO;
import com.mipa.mapper.*;
import com.mipa.model.*;
import com.mipa.service.api.IBookShelfService;
import com.mipa.utils.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookShelfService implements IBookShelfService {
    @Autowired
    ReaderBookShelfMapper readerBookShelfMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    BookMapper bookMapper;

    @Autowired
    ChapterMapper chapterMapper;

    public boolean addToBookShelf(String userId, String bookId) {
        var userOpt = userMapper.selectById(userId);
        if(userOpt.isPresent()){
            var bookOpt = bookMapper.selectById(bookId);
            if(bookOpt.isPresent()){
                var bookshelfOpt = readerBookShelfMapper.selectByUserIdAndBookId(userId, bookId);
                if(bookshelfOpt.isEmpty()){
                    addToLibrary(userId, bookId);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeFromBookShelf(String userId, String bookId) {
        var userOpt = userMapper.selectById(userId);
        if(userOpt.isPresent()){
            var bookOpt = bookMapper.selectById(bookId);
            if(bookOpt.isPresent()){
                var bookshelfOpt = readerBookShelfMapper.selectByUserIdAndBookId(userId, bookId);
                if(bookshelfOpt.isPresent()){
                    readerBookShelfMapper.delete(bookshelfOpt.get().getId());
                    return true;
                }
            }
        }
        return false;
    }

    public BookShelfVO getFromBookShelf(String userId, String bookId) {
        var userOpt = userMapper.selectById(userId);
        if(userOpt.isPresent()){
            var bookOpt = bookMapper.selectById(bookId);
            if(bookOpt.isPresent()){
                var bookshelfOpt = readerBookShelfMapper.selectByUserIdAndBookId(userId, bookId);
                if(bookshelfOpt.isPresent()){
                    var res = CopyProperties.run(bookshelfOpt.get(), BookShelfVO.class);
                    res.setCoverUrl(bookOpt.get().getCoverUrl());
                    return res;
                }
            }
        }
        return null;
    }

    //先暂时定为没有chapterid和order不能加入书架
    public boolean updateBookShelf(String userId, String bookId, BookShelfRequestDTO dto) {
        var userOpt = userMapper.selectById(userId);
        if (userOpt.isPresent()) {
            var bookOpt = bookMapper.selectById(bookId);
            if (bookOpt.isPresent()) {
                var chapterOpt = chapterMapper.selectInfoById(dto.getChapterId());

                if (chapterOpt.isPresent() && chapterOpt.get().getBookId().equals(bookId)) {
                    var bookshelfOpt = readerBookShelfMapper.selectByUserIdAndBookId(userId, bookId);

                    if (bookshelfOpt.isPresent()) {
                        bookshelfOpt.get().setChapterId(dto.getChapterId());
                        bookshelfOpt.get().setChapterOrder(dto.getChapterOrder());//还得校验chapterid和order是一致的,这可以使用id上的关联
                        readerBookShelfMapper.update(bookshelfOpt.get());
                        return true;
                    } else {
                        var bookshelf = CopyProperties.run(dto, ReaderBookShelf.class);
                        bookshelf.setId(IdUtil.uuid());
                        readerBookShelfMapper.insert(bookshelf);
                        return true;
                    }
                }

            }
        }
        return false;
    }

    public PageRecord<BookShelfVO> getUserBookShelf(String userId, Integer pageNumber, Integer pageSize) {
        var userOpt = userMapper.selectById(userId);
        if (userOpt.isPresent()) {
            PageHelper.startPage(pageNumber, pageSize);
            var bookshelfs = readerBookShelfMapper.selectByUserId(userId);
            var page = new PageInfo<>(bookshelfs);
            var ids = page.getList().stream().map(item -> item.getBookId()).toList();
            var bookId2bookMap = bookMapper.selectAllByIds(ids).stream().collect(Collectors.toMap(Book::getId, o -> o));
            var res = bookshelfs.stream().map(item -> {
                var newItem = CopyProperties.run(item, BookShelfVO.class);
                newItem.setCoverUrl(
                        Optional.ofNullable(bookId2bookMap.get(item.getBookId())).map(Book::getCoverUrl).orElse("")
                );
                return newItem;
            }).toList();
            return PageRecord.of(res, page);
        }
        return null;
    }

    private Boolean addToLibrary(String userId, String bookId){
        var bookshelf = new ReaderBookShelf();
        bookshelf.setBookId(bookId);
        bookshelf.setUserId(userId);
        bookshelf.setChapterOrder(0);
        bookshelf.setId(IdUtil.uuid());
        readerBookShelfMapper.insert(bookshelf);
        return true;
    }


    //record class ResultData(boolean result, User user, Book book, ReaderBookShelf readerBookShelf)
}
