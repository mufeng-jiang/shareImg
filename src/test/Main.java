package test;

import util.SharedImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 用于测试
 *
 * @author zhangkeke
 * @since 2017/10/18 15:31
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // 获取类所在项目路径，获取方式有多种
        String address = Main.class.getResource("/").getPath();

        // 头像
        String avastar = address + "img/avastar.jpg";
        // 二维码
        String qrcode = address + "img/qrcode.jpg";
        // 二维码识别图
        String point = address + "img/point.png";
        // 背景图片
        String background = address + "img/background.jpg";

        // 1. 通过背景图片构建 BufferedImage
        BufferedImage zoomPicture = ImageIO.read(new File(background));
        // 2. 头像裁剪成圆形
        BufferedImage roundedImage = SharedImageUtils.createRoundedImage(avastar, SharedImageUtils.AVATAR_SIZE);
        // 3. 合并头像，昵称
        BufferedImage mergeImage = SharedImageUtils.mergePicture(zoomPicture, roundedImage, "骑着乌龟去看海");
        // 4. 合并二维码及二维码识别图
        mergeImage = SharedImageUtils.mergeQrcode(mergeImage, qrcode, point);
        // 5. 生成分享图
        ImageIO.write(mergeImage, "jpg", new File(address + "img/result.jpg"));
    }

}
