package com.tacz.guns.util.helper;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProgressListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@SuppressWarnings("JavadocReference")
public class HttpUtilHelper {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ListeningExecutorService DOWNLOAD_EXECUTOR = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).setNameFormat("Downloader %d").build()));

    /**
     * {@link net.minecraft.util.HttpUtil#downloadTo}
     */
    public static CompletableFuture<?> downloadTo(File pSaveFile, URL pUrl, Map<String, String> pRequestProperties, int pMaxSize,
                                                  @Nullable ProgressListener pProgressListener, Proxy pProxy) {
        return CompletableFuture.supplyAsync(() -> {
            HttpURLConnection httpurlconnection = null;
            InputStream inputstream = null;
            OutputStream outputstream = null;
            if (pProgressListener != null) {
                pProgressListener.progressStart(Component.translatable("resourcepack.downloading"));
                pProgressListener.progressStage(Component.translatable("resourcepack.requesting"));
            }
            try {
                try {
                    byte[] abyte = new byte[4096];
                    httpurlconnection = (HttpURLConnection)pUrl.openConnection(pProxy);
                    httpurlconnection.setInstanceFollowRedirects(true);
                    float f1 = 0.0F;
                    float f = (float)pRequestProperties.entrySet().size();
                    for(Map.Entry<String, String> entry : pRequestProperties.entrySet()) {
                        httpurlconnection.setRequestProperty(entry.getKey(), entry.getValue());
                        if (pProgressListener != null) {
                            pProgressListener.progressStagePercentage((int)(++f1 / f * 100.0F));
                        }
                    }
                    inputstream = httpurlconnection.getInputStream();
                    f = (float)httpurlconnection.getContentLength();
                    int i = httpurlconnection.getContentLength();
                    if (pProgressListener != null) {
                        pProgressListener.progressStage(Component.translatable("resourcepack.progress", String.format(Locale.ROOT, "%.2f", f / 1000.0F / 1000.0F)));
                    }
                    if (pSaveFile.exists()) {
                        long j = pSaveFile.length();
                        if (j == (long)i) {
                            if (pProgressListener != null) {
                                pProgressListener.stop();
                            }
                            return null;
                        }
                        LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", pSaveFile, i, j);
                        FileUtils.deleteQuietly(pSaveFile);
                    } else if (pSaveFile.getParentFile() != null) {
                        pSaveFile.getParentFile().mkdirs();
                    }
                    outputstream = new DataOutputStream(new FileOutputStream(pSaveFile));
                    if (pMaxSize > 0 && f > (float)pMaxSize) {
                        if (pProgressListener != null) {
                            pProgressListener.stop();
                        }
                        throw new IOException("Filesize is bigger than maximum allowed (file is " + f1 + ", limit is " + pMaxSize + ")");
                    }
                    int k;
                    while((k = inputstream.read(abyte)) >= 0) {
                        f1 += (float)k;
                        if (pProgressListener != null) {
                            pProgressListener.progressStagePercentage((int)(f1 / f * 100.0F));
                        }
                        if (pMaxSize > 0 && f1 > (float)pMaxSize) {
                            if (pProgressListener != null) {
                                pProgressListener.stop();
                            }
                            throw new IOException("Filesize was bigger than maximum allowed (got >= " + f1 + ", limit was " + pMaxSize + ")");
                        }
                        if (Thread.interrupted()) {
                            LOGGER.error("INTERRUPTED");
                            if (pProgressListener != null) {
                                pProgressListener.stop();
                            }
                            return null;
                        }
                        outputstream.write(abyte, 0, k);
                    }
                    if (pProgressListener != null) {
                        pProgressListener.stop();
                        return null;
                    }
                } catch (Throwable throwable) {
                    LOGGER.error("Failed to download file", throwable);
                    if (httpurlconnection != null) {
                        InputStream inputstream1 = httpurlconnection.getErrorStream();
                        try {
                            LOGGER.error("HTTP response error: {}", (Object) IOUtils.toString(inputstream1, StandardCharsets.UTF_8));
                        } catch (IOException ioexception) {
                            LOGGER.error("Failed to read response from server");
                        }
                    }
                    if (pProgressListener != null) {
                        pProgressListener.stop();
                        return null;
                    }
                }
                return null;
            } finally {
                IOUtils.closeQuietly(inputstream);
                IOUtils.closeQuietly(outputstream);
            }
        }, DOWNLOAD_EXECUTOR);
    }

}
