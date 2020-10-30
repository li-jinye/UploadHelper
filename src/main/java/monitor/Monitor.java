package monitor;

import com.jcraft.jsch.SftpProgressMonitor;
import gui.Main;

/**
 * @author LiJinye
 * @date 2020/9/30
 */
public class Monitor implements SftpProgressMonitor {
    private long transfered;
    Main href = Main.href;

    private long startTime;
    private long fileSize;

    @Override
    public boolean count(long count) {
        transfered = transfered + count;
        long curr = transfered / 1024 / 1024;
        int progress = Math.toIntExact(100 * curr / fileSize);
        String text = "当前传输: " + progress + " % (" + curr + "/" + fileSize + "M)";
        href.backInfo(text);
        href.progressBar.setValue(progress);
        href.progressBar.setString(text);
        return true;
    }


    @Override
    public void end() {
        long usedTime = (System.currentTimeMillis() - startTime) / 1000;
        String text = "传输成功! 用时: " + usedTime + " S, 平均速度: " + fileSize / usedTime + " M/S";
        href.success(text);
        href.progressBar.setString(text);
    }


    @Override
    public void init(int op, String src, String dest, long max) {
        startTime = System.currentTimeMillis();
        fileSize = (max / 1024 / 1024);
        href.info("开始传输文件,总大小: " + fileSize + " M");
        href.info("");
    }

}