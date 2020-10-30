import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.RuntimeUtil;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Scanner;

/**
 * @author LiJinye
 * @date 2020/8/9
 */
public class Test1 {
    public static void main(String[] args) {
        File file = FileUtil.file("E:\\svn\\school\\school\\target\\school.war");
        WatchMonitor.createAll(file, new DelayWatcher(new SimpleWatcher() {
            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                Console.log("EVENT modify");
            }
        }, 500)).start();
        while (true) {

        }
    }

    @Test
    public void test1() {

    }
}