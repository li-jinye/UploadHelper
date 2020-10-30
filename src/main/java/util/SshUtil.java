package util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchServer;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.extra.ssh.Sftp;
import com.jcraft.jsch.Session;
import gui.Main;
import monitor.Monitor;
import po.LinkPo;
import po.PackPo;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * @author LiJinye
 * @date 2020/10/21
 */
public class SshUtil {
    public static Main href = Main.href;
    public static String msg = null;
    public static Boolean used = false;
    public static Integer timeOut = 3;
    public static List<WatchMonitor> listenerList = new LinkedList<>();

    public static Boolean TestConnect(LinkPo po) {
        return TestConnect(po.getIp(), Integer.parseInt(po.getPort()), po.getUsername(), po.getPassword());
    }

    public static Boolean TestConnect(String sshHost, int sshPort, String sshUser, String sshPass) {
        try {
            Session session = JschUtil.createSession(sshHost, sshPort, sshUser, sshPass);
            session.setTimeout(timeOut * 1000);
            session.connect();
            JschUtil.close(session);
            return true;
        } catch (Exception e) {
            msg = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    public static void TestAndMsg(LinkPo po) {
        TestAndMsg(po.getIp(), Integer.parseInt(po.getPort()), po.getUsername(), po.getPassword());
    }

    public static void TestAndMsg(String sshHost, int sshPort, String sshUser, String sshPass) {
        if (TestConnect(sshHost, sshPort, sshUser, sshPass)) {
            JOptionPane.showMessageDialog(null, "连接成功！");
        } else {
            JOptionPane.showMessageDialog(null, "连接失败！ 错误信息: " + SshUtil.msg);
        }
    }

    public static void uploadOnce(PackPo pack) {
        if (!used) {
            Thread ts = new Thread(new runner(pack));
            ts.start();
            used = true;
        } else {
            href.error("正在上传中");
        }

    }

    static class runner implements Runnable {
        private final PackPo pack;
        Main href = Main.href;

        public runner(PackPo pack) {
            this.pack = pack;
        }

        public void run() {
            try {
                LinkPo link = href.getLinkByUUID(pack.getLinkId());
                if (link == null) {
                    Util.error("连接设置有误,请重新设置连接");
                    return;
                }
                href.info("准备建立连接,超时时间: " + timeOut + "秒");
                Session session = JschUtil.createSession(link.getIp(), Integer.parseInt(link.getPort()), link.getUsername(), link.getPassword());
                session.setTimeout(5000);
                session.connect();
                href.info("本地文件: " + pack.getLocal());
                href.info("上传路径: " + pack.getServer());
                Sftp sftp = JschUtil.createSftp(session);
                sftp.put(pack.getLocal(), pack.getServer(), new Monitor(), Sftp.Mode.OVERWRITE);
                sftp.close();
                JschUtil.close(session);
            } catch (Exception e) {
                href.error("上传异常,错误信息:" + e.getMessage());
            }
            used = false;
        }
    }

    public static void autoUpload(List<PackPo> list, Integer listenerTime) {
        list.forEach(e -> {
            File file = FileUtil.file(e.getLocal());
            WatchMonitor monitor = WatchMonitor.createAll(file, new DelayWatcher(new SimpleWatcher() {
                private final PackPo pack = e;

                @Override
                public void onModify(WatchEvent<?> event, Path currentPath) {
                    href.info("监听到文件变动: " + pack.getName() + " " + pack.getFileName());
                    while (used) {
                        href.info("有文件正在上传中,等待5秒");
                        href.info("有文件正在上传中,等待5秒");
                        sleep(5000);
                    }
                    uploadOnce(pack);
                }
            }, listenerTime * 1000));
            listenerList.add(monitor);
            monitor.start();
            href.info("监听成功: " + e.getName() + " " + e.getFileName());
        });
    }

    public static void autoUploadClose() {
        listenerList.forEach(WatchServer::close);
        listenerList.clear();
    }

    private static void sleep(Integer i) {
        try {
            Thread.sleep(i);//毫秒
        } catch (InterruptedException ignored) {
        }
    }
}
