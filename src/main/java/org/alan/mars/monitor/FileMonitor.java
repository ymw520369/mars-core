package org.alan.mars.monitor;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2017/6/2.
 *
 * @author Alan
 * @since 1.0
 */
public class FileMonitor extends FileAlterationListenerAdaptor {
    private Logger log = LoggerFactory.getLogger(getClass());

    private FileAlterationMonitor monitor = new FileAlterationMonitor();

    private Map<File, FileLoader> fileLoaders = new HashMap<>();

    public FileMonitor() {
        try {
            monitor.start();
        } catch (Exception e) {
            log.warn("文件监听器器动失败...");
        }
    }

    public void monitor(String fileName, FileLoader loader, boolean load) {
        File file = new File(fileName);
        monitor(file, loader, load);
    }

    public void monitor(File file, FileLoader loader, boolean load) {
        try {
            FileAlterationObserver observer = null;
            if (file.isFile()) {
                observer = new FileAlterationObserver(file.getParentFile(), f -> f.equals(file));
            } else {
                observer = new FileAlterationObserver(file);
            }
            observer.addListener(this);
            observer.initialize();
            monitor.addObserver(observer);
            fileLoaders.put(file, loader);
            if (load) {
                loader.load(file);
            }
        } catch (Exception e) {
            log.warn("load file err,file={}", file.getName(), e);
        }
    }

    private void loadFile(File file) {
        FileLoader fileLoader = fileLoaders.get(file);
        if (fileLoader != null) {
            fileLoader.load(file);
        }
    }


    @Override
    public void onFileChange(File file) {
        log.info("on file change filename = {}", file.getName());
        loadFile(file);
    }

    @Override
    public void onFileCreate(File file) {
        log.info("on file create filename = {}", file.getName());
        loadFile(file);
    }
}
