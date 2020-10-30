package gui;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lintener.Listener;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import po.LinkPo;
import po.PackPo;
import util.Util;
import util.SshUtil;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author LiJinye
 * @date 2020/8/3
 */
public class Main {

    public static Main href;
    public static String path;

    public List<LinkPo> linkList = new ArrayList<>();
    public LinkedList<PackPo> packList = new LinkedList<>();
    public PackPo currPack = new PackPo();
    public Integer currPackIndex = 0;
    public JComboBox<String> comboBox;

    private JPanel main;

    private JTextArea console;

    private JButton LinkAddButton;
    private JButton LinkEditButton;
    private JButton LinkDelButton;
    private JButton LinkTestButton;
    private JList<String> packJList;
    private JCheckBox packAuto;
    private JTextField packName;
    private JTextField packLink;
    private JTextField packLocal;
    private JTextField packServer;
    private JButton packLinkButton;
    private JButton packLocalButton;
    private JButton packServerButton;
    private JButton packAddButton;
    private JButton packCopyButton;
    private JButton packDelButton;
    private JButton packSaveButton;
    private JButton clearLinkButton;
    private JButton uploadOnce;
    private JButton saveConfigButton;
    public JProgressBar progressBar;
    private JButton readConfigButton;
    private JTextField timeOutTextField;
    private JTextField listenerTimeTextField;
    private JCheckBox uploadAutoCheckBox;

    //通过uuid得到连接
    public LinkPo getLinkByUUID(String uuid) {
        if (StrUtil.isEmpty(uuid)) {
            return null;
        }
        for (LinkPo po : linkList) {
            if (uuid.equals(po.getUuid())) {
                return po;
            }
        }
        return null;
    }

    //设置包的属性
    public void setPackTexts(Integer index) {
        currPack = packList.get(index);
        packName.setText(currPack.getName());
        packLocal.setText(currPack.getLocal());
        packServer.setText(currPack.getServer());
        packLink.setText(currPack.getLinkInfo());
        packAuto.setSelected(currPack.isAuto());
    }

    //重新加载包列表,并设置索引为当前的
    public void setPackJList() {
        String[] strings = packList.stream().map(PackPo::getInfo).toArray(String[]::new);
        packJList.setListData(strings);
        packJList.setSelectedIndex(currPackIndex);
        setPackTexts(currPackIndex);
    }

    //设置当前包的链接
    public void setPackLink(Integer index) {
        LinkPo po = linkList.get(index);
        currPack.setLinkId(po.getUuid());
        packLink.setText(po.getInfo());
    }

    //设置服务器路径
    public void setPackServer(String path) {
        packServer.setText(path);
    }

    //重新加载连接的下拉选框
    public void setComboBox() {
        comboBox.removeAllItems();
        IntStream.range(0, linkList.size()).forEach(i -> comboBox.addItem(i + ":" + linkList.get(i).getInfo()));
        comboBox.setSelectedIndex(linkList.size() - 1);
    }

    //重新加载包列表,并设置索引为第一个
    public void reloadPackJList() {
        currPackIndex = 0;
        setPackJList();
    }
    //初始化
    public void init() {
        linkList.add(new LinkPo().setName("请选择"));
        // linkList.add(new LinkPo("新的服务器", "192.168.3.192", "22", "root", "scooper_2014"));
        setComboBox();
        packList.add(new PackPo("新的包", null, null, null));
        setPackJList();
        setPackTexts(currPackIndex);
        currPack = packList.get(currPackIndex);

        //获取路径
        path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        path = StrUtil.sub(path, 1, path.lastIndexOf("/"));
        info("启动路径: " + path);

        info("##########  使用说明  ##########");
        info("#使用步骤:");
        info("1.新增连接");
        info("2.新增包,并设置其属性,保存");
        info("3.1.点击'部署一次',可上传包");
        info("3.2.勾选'自动部署',将包加入监听,");
        info("     当文件发生变动,自动上传包");
        info("#注意事项:");
        info("1.修改完包需要保存才能生效");
        info("2.自动部署不会对之后的修改生效");
        info("3.启动和关闭时会自动读取和保存配置");
        info("############################");
        readSetting();
        autoUpload();
    }

    //监听器
    public Main() {
        LinkAddButton.addActionListener(e -> new LinkManager());
        LinkEditButton.addActionListener(e -> {
            if (comboBox.getSelectedIndex() == 0) {
                return;
            }
            int index = comboBox.getSelectedIndex();
            new LinkManager().edit(index);
        });
        LinkDelButton.addActionListener(e -> {
            int index = comboBox.getSelectedIndex();
            if (index != 0 && index != -1) {
                linkList.remove(index);
                setComboBox();

            }
        });
        LinkTestButton.addActionListener(e -> {
            int index = comboBox.getSelectedIndex();
            if (index != 0) {
                SshUtil.TestAndMsg(linkList.get(index));
            }
        });
        packLinkButton.addActionListener(e -> new LinkChooser());
        packAddButton.addActionListener(e -> {
            packList.addFirst(new PackPo().setName("新的包"));
            reloadPackJList();
        });
        packCopyButton.addActionListener(e -> {
            packList.addFirst(ObjectUtil.clone(currPack));
            reloadPackJList();
        });
        packDelButton.addActionListener(e -> {
            if (packList.size() <= 1) {
                Util.info("最少保留一个包");
                return;
            }
            packList.remove(currPackIndex.intValue());
            reloadPackJList();
        });
        packSaveButton.addActionListener(e -> {
            if ("新的包".equals(packName.getText())) {
                Util.error("请修改包名");
                return;
            }
            if (!StrUtil.isAllNotEmpty(packName.getText(), packLocal.getText(), packServer.getText())) {
                Util.error("请填写全部信息");
                return;
            }
            currPack.setName(packName.getText()).setLocal(packLocal.getText()).setServer(packServer.getText()).setAuto(packAuto.isSelected());
            setPackJList();
        });
        packJList.addListSelectionListener(e -> {
            //只在第一次选中时触发事件
            if (e.getValueIsAdjusting()) {
                currPackIndex = packJList.getSelectedIndex();
                setPackTexts(currPackIndex);
            }
        });
        packLocalButton.addActionListener(e -> {
            String s = Util.FileChooser();
            if (s != null) {
                packLocal.setText(s);
            }
        });
        packServerButton.addActionListener(e -> new LinkFileChooser(getLinkByUUID(currPack.getLinkId())));
        clearLinkButton.addActionListener(e -> {
            packName.setText("");
            packLink.setText("");
            packLocal.setText("");
            packServer.setText("");
        });
        uploadOnce.addActionListener(e -> SshUtil.uploadOnce(currPack));
        saveConfigButton.addActionListener(e -> saveSetting());
        readConfigButton.addActionListener(e -> readSetting());
        uploadAutoCheckBox.addActionListener(e -> autoUpload());
    }
    //全局自动部署
    private void autoUpload() {
        if (uploadAutoCheckBox.isSelected()) {
            //启动自动部署
            Integer listenerTime = Integer.valueOf(listenerTimeTextField.getText());
            info("准备启动全局自动部署,监听延时" + listenerTime + "秒");
            //筛选是自动部署的包
            List<PackPo> collect = packList.stream().filter(PackPo::isAuto).collect(Collectors.toList());
            if (CollUtil.isEmpty(collect)) {
                error("没有任何包勾选了自动部署,或勾选了但未保存");
                uploadAutoCheckBox.setSelected(false);
                return;
            }
            SshUtil.autoUpload(collect, listenerTime);
            success("自动部署启动成功!");
            info("############################");
        } else {
            //关闭自动部署
            SshUtil.autoUploadClose();
            info("已关闭全局自动部署");
        }
    }
    //保存设置
    public void saveSetting() {
        Map<String, Object> map = new HashMap<>();
        map.put("linkList", linkList);
        map.put("packList", packList);
        map.put("timeOut", timeOutTextField.getText());
        map.put("scanTime", listenerTimeTextField.getText());
        map.put("uploadAuto", uploadAutoCheckBox.isSelected());
        map.put("currPackIndex", currPackIndex);
        JSON parse = JSONUtil.parse(map);
        File touch = FileUtil.touch(path + "/lib/config.cfg");
        FileUtil.writeString(parse.toStringPretty(), touch, "UTF-8");
    }
    //读取设置
    private void readSetting() {
        String s;
        String cfgPath = path + "/lib/config.cfg";
        info("配置文件路径: " + cfgPath);
        try {
            s = FileUtil.readString(cfgPath, "UTF-8");
        } catch (Exception e) {
            error("读取配置失败");
            return;
        }
        if (StrUtil.isEmpty(s)) {
            error("读取配置失败");
            return;
        }
        JSONObject jsonObject = JSONUtil.parseObj(s);
        linkList = jsonObject.getJSONArray("linkList").toList(LinkPo.class);
        packList = new LinkedList<>(jsonObject.getJSONArray("packList").toList(PackPo.class));
        String timeOut = jsonObject.get("timeOut", String.class);
        timeOutTextField.setText(timeOut);
        String scanTime = jsonObject.get("scanTime", String.class);
        listenerTimeTextField.setText(scanTime);
        Boolean uploadAuto = jsonObject.get("uploadAuto", Boolean.class);
        uploadAutoCheckBox.setSelected(uploadAuto);
        currPackIndex = jsonObject.get("currPackIndex", Integer.class);
        setComboBox();
        setPackJList();
        info("读取配置成功!");
    }

    private void log(String text) {
        console.append(DateUtil.format(DateUtil.date(), "HH:mm:ss : ") + text + "\n");
        //滚动条跳转
        console.setCaretPosition(console.getText().length());
    }

    public void backInfo(String text) {
        try {
            int last = console.getLineCount() - 2;
            console.replaceRange(DateUtil.format(DateUtil.date(), "HH:mm:ss : ") + "[INFO]       " + text + "\n",
                    console.getLineStartOffset(last), console.getLineEndOffset(last));
        } catch (Exception ignored) {
        }
    }

    public void info(String text) {
        log("[INFO]       " + text);
    }

    public void error(String text) {
        log("[ERROR]    " + text);
    }

    public void success(String text) {
        log("[SUCCESS] " + text);
    }

    public static void main(String[] args) {
        InitGlobalFont(new Font("Microsoft YaHei", Font.PLAIN, 16));  //统一设置字体
        try {
            BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception ignored) {
        }
        UIManager.put("RootPane.setupButtonVisible", false);
        JFrame frame = new JFrame("包部署工具 r1.0.0.1 beta");
        href = new Main();
        href.init();
        frame.setContentPane(href.main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        //关闭窗口监听
        frame.addWindowListener(new Listener());

        //图标
        ImageIcon imageIcon = new ImageIcon(path + "/lib/favicon.png");
        frame.setIconImage(imageIcon.getImage());
    }

    private static void InitGlobalFont(Font font) {
        FontUIResource fontRes = new FontUIResource(font);
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }
    }
}
