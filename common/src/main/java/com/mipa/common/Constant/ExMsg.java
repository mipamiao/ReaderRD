package com.mipa.common.Constant;

public class ExMsg {

    public final static String DB_CONSTRAIN_FAILED = "数据库完整性约束失败";

    public final static String EXIST = "已经存在";
    public final static String NOT_EXIST = "不存在";

    public final static String AND = "和";
    public final static String OR = "或";

    public final static String USERNAME_EXIST = "用户名" + EXIST;

    public final static String USER_NOT_EXIST = "用户" + NOT_EXIST;
    public final static String BOOK_NOT_EXIST = "书籍不存在";
    public final static String CHAPTER_NOT_EXIST = "章节不存在";
    public final static String CHAPTER_HAVEN_EXIST = "章节已经存在";
    public final static String CHAPTER_BOOK_MISMATCH = "章节与书籍不匹配";
    public final static String CHAPTER_ID_ORDER_MISMATCH = "章节编号和id不匹配";

    public final static String USER_OR_PWD_WRONG = "用户名或密码错误";
    public final static String NOT_AUTHOR = "不是本书作者";

    public final static String READPROGRESS_NOT_EXIST = "历史记录" + NOT_EXIST;
    public final static String READPROGRESS_USER_MISMATCH = "历史记录与用户不匹配";

    public final static String BOOKMARK_HAD_EXIST = "书签已存在";
    public final static String BOOKMARK_NOT_EXIST = "书签不存在";
    public final static String BOOKMARK_USER_MISMATCH = "书签与用户不匹配";

    public final static String BOOKSHELF_HAD_EXIST = "书籍已存在书架";
    public final static String BOOKSHELF_NOT_EXIST = "书籍不存在书架";
    public final static String BOOKSHELF_USER_MISMATCH = "书架与用户不匹配";

    public final static String COMMENT_HAD_EXIST = "评论已存在";
    public final static String COMMENT_NOT_EXIST = "评论不存在";
    public final static String COMMENT_USER_MISMATCH = "评论与用户不匹配";

    public final static String EMPTY_FILE = "文件为空";
    public final static String FILE_SAVE_ERROR = "文件存储出错";

    public final static String LACK_CHAPTER = "该书籍没有章节";

    public final static String PAGE_CACHE_PARAM_MISMATCH = "分页缓存参数缺失或出错";
    public final static String PAGE_CACHE_PARAM_CONFLICT = "分页缓存参数错位，保证第idIndex个参数为 id";

    public final static String JSON_PARSE_FAILED = "json解析出错";
    public final static String JSON_TRANS_FAILED = "json生成出错";
    public final static String CONTENT_PAGE_LACK = "内容页缺失";
    public final static String PARAM_ERROR_FOR_CONTENT = "用于内容同步的命令参数错误";

    public final static String JSON_PARSE_FAILED_IN_REDIS = "在获取redis中存储的对象时json解析出错";

    public final static String TASK_REJECT_LIMIT_SIZE = "任务被拒绝，因为超出上限";


    public static String And(String ...strs){
        var stringBuilder = new StringBuilder();
        for(String str: strs){
            if(!stringBuilder.isEmpty())stringBuilder.append(AND);
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

    public static String Or(String ...strs){
        var stringBuilder = new StringBuilder();
        for(String str: strs){
            if(!stringBuilder.isEmpty())stringBuilder.append(OR);
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }
}
