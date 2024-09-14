package com.globo.upcomingAds.configs;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.ffmpeg.global.swscale;
import org.bytedeco.ffmpeg.swscale.SwsFilter;
import org.bytedeco.javacpp.DoublePointer;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
public class FFmpegConfig {

    @PostConstruct
    public void initializeFFmpeg() {
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
        swscale.sws_getContext(0, 0, 0, 0, 0, 0, 0, new SwsFilter(), new SwsFilter(), new DoublePointer());
        log.info("Inicializando bibliotecas do FFmpeg 6.1.1...");
    }
}
