package po;

import cn.hutool.core.lang.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author LiJinye
 * @date 2020/9/30
 */

@Data
@Accessors(chain = true)
public class LinkPo {
    private String uuid= UUID.randomUUID().toString();
    private String name;
    private String ip;
    private String port;
    private String username;
    private String password;

    public String getInfo() {
        return "请选择".equals(name) ? "请选择" : name + " :" + ip + ":" + port + " :" + username;
    }

    public LinkPo(String name, String ip, String port, String username, String password) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public LinkPo() {
    }
}
