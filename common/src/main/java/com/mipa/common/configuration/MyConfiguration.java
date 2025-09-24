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
}
