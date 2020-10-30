package po;

import cn.hutool.core.clone.CloneSupport;
import gui.Main;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author LiJinye
 * @date 2020/9/30
 */

@Data
@Accessors(chain = true)
public class PackPo extends CloneSupport<PackPo> {
    private String name;
    private String linkId;
    private String local;
    private String server;
    private boolean isAuto = false;
    private Date lastDate;

    public PackPo(String name, String linkId, String local, String server) {
        this.name = name;
        this.linkId = linkId;
        this.local = local;
        this.server = server;
    }

    public PackPo() {
    }

    public String getInfo() {
        LinkPo link = Main.href.getLinkByUUID(linkId);
        if ("新的包".equals(name)) {
            return name;
        }
        if (link == null) {
            return name + " 连接无效";
        }
        return "包名: " + name + "  " + link.getName() + "(" + link.getIp() + ":" + link.getPort() +
                ")  文件名:" + getFileName() + (isAuto ? " 自动" : "");
    }

    public String getLinkInfo() {
        LinkPo link = Main.href.getLinkByUUID(linkId);
        return link == null ? "" : link.getInfo();
    }

    public String getFileName() {
        int max = Math.max(local.lastIndexOf("/"), local.lastIndexOf("\\"));
        return local.substring(max + 1);
    }
}
