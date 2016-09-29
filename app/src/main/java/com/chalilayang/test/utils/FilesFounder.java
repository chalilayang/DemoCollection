package com.chalilayang.test.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by chalilayang on 2016/9/26.
 */

public class FilesFounder {
    private String rootPath;
    private List<String> mFiles = new LinkedList<>();

    class Worker implements Callable<Void> {
        private String root;
        public Worker(String path) {
            root = path;
        }
        @Override
        public Void call() throws Exception {
            return null;
        }
    }
}
