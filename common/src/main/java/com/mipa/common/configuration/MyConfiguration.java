package com.mipa.common.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiguration {

    @Value("${my-settings.secret}")
    public String jwtSecretKey;

    @Value("${data.settings.book-cover-img.dstDir}")
    public String bookCoverImgsDstDir;

    @Value("${data.settings.book-cover-img.srcDir}")
    public String bookCoverImgsSrcDir;

    @Value("${data.settings.avatars.dstDir}")
    public String avatarsDstDir;

    @Value("${data.settings.avatars.srcDir}")
    public String avatarsSrcDir ;

    @Value("${data.settings.data-net.host}")
    public String dataNetHost;

    @Value("${my-settings.file-separator}")
    public String fileSeparator ;

    @Value("${my-settings.content-page-size}")
    public Integer contentPageSize ;

    @Value("${my-settings.content-page-top-scale}")
    public Double contentPageTopScale;

    @Value("${my-settings.content-page-bottom-scale}")
    public Double contentPageBottomScale;

    @Value("${my-settings.content-page-cache-expire}")
    public Integer contentPageCacheExpireTime;

    @Value("${my-settings.content-page-cache-data-max-size}")
    public Integer contentPageCacheDataMaxSize;


    @Value("${my-settings.content-page-cache.save-to-db-idle-time}")
    public Integer contentPageCacheSaveToDBIdleTime;

    @Value("${my-settings.websocket.writer-page.keep-alive-thread-num}")
    public Integer writerPageKeepAliveThreadNum;

    @Value("${my-settings.websocket.writer-page.heart-beat-span}")
    public Integer writerPageHeartBeatSpan;
}
