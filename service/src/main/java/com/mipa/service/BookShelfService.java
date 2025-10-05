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
import com.mipa.validate.VerifyRelationShip;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public boolean addToBookShelf(String userId, String bookId) {
        var bookshelfOpt = readerBookShelfMapper.selectByUserIdAndBookId(userId, bookId);
        if (bookshelfOpt.isEmpty()) {
            var chapterOpt = chapterMapper.selectInfoByBookIdAndOrder(bookId, 1);
            if (chapterOpt.isPresent()) {
                var bookshelf = new ReaderBookShelf();
                bookshelf.setBookId(bookId);
                bookshelf.setUserId(userId);
                bookshelf.setChapterId(chapterOpt.get().getId());
                bookshelf.setChapterOrder(1);
                bookshelf.setId(IdUtil.uuid());
                readerBookShelfMapper.insert(bookshelf);
                return true;
            }
        }
        return false;
    }

    @Transactional
    @Override
    public boolean removeFromBookShelf(String userId, String bookId) {
        return readerBookShelfMapper.deleteByUserIdAndBookId(userId, bookId) > 0;
    }


    @Override
    public BookShelfVO getFromBookShelf(String userId, String bookId) {
        return readerBookShelfMapper.selectDetailAndCoverByUserIdAndBookId(userId, bookId).orElse(null);
    }

    @Transactional
    @Override
    //先暂时定为没有chapterid和order不能加入书架
    public boolean updateBookShelf(String userId, String bookId, BookShelfRequestDTO dto) {

        var vr = VerifyRelationShip.start()
                .verifyBookAndChapter(dto.getBookId(), dto.getChapterId(), chapterMapper)
                .verifyChapterIdAndOrder(dto.getChapterOrder(), dto.getChapterId(), chapterMapper);

        if (vr.isSucceed()) {
            removeFromBookShelf(userId, bookId);
            var bookshelf = CopyProperties.run(dto, ReaderBookShelf.class);
            bookshelf.setId(IdUtil.uuid());
            readerBookShelfMapper.insert(bookshelf);
            return true;
        }

        return false;
    }

    @Override
    public PageRecord<BookShelfVO> getUserBookShelf(String userId, Integer pageNumber, Integer pageSize) {
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

}
