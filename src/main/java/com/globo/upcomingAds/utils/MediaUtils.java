package com.globo.upcomingAds.utils;

import lombok.NoArgsConstructor;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.ffmpeg.global.swscale;
import org.bytedeco.ffmpeg.swscale.SwsFilter;
import org.bytedeco.javacpp.DoublePointer;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@NoArgsConstructor
public class MediaUtils {

    public static double getMediaDuration(String mediaPath) throws Exception {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "csv=p=0",
                    mediaPath
            );

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String durationStr = reader.readLine();

            process.waitFor();

            return Double.parseDouble(durationStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static void startFfmpeg() {
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
        swscale.sws_getContext(0, 0, 0, 0, 0, 0, 0, new SwsFilter(), new SwsFilter(), new DoublePointer());
    }


}
