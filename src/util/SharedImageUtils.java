package util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

/**
 * 生成朋友圈分享图相关接口
 * 用于测试，不用太过于关注代码规范
 *
 * @author zhangkeke
 * @since 2017/10/18 15:31
 */
public class SharedImageUtils {
    /* 要放置的二维码大小 */
    private  static final int QRCODE_SIZE = 300;
    /* 要放置的二维码高度 */
    private static final int QRCODE_Y = 540;
    /* 要放置的头像半径 */
    public static final int AVATAR_SIZE = 160;
    /* 要放置的头像y坐标 */
    private  static final int AVATAR_Y = 150;
    /* 昵称的高度 */
    private  static final int FONT_Y = 370;
    /* 推广文案的高度 */
    private  static final int COPYWRITER = 415;
    /* 二维码识别图案高度 */
    private  static final int RECOGNITION_QRCODE_Y = 890;
    /* 二维码识别图案大小 */
    private  static final int RECOGNITION_QRCODE_SIZE = 260;

    /**
     * 裁剪图片
     *
     * @param img          the img
     * @param originWidth  the origin width
     * @param originHeight the origin height
     * @return the buffered image
     * @throws Exception the exception
     */
    public static BufferedImage cutPicture(BufferedImage img, int originWidth, int originHeight) throws IOException {
        int width = img.getWidth();  // 原图的宽度
        int height = img.getHeight();  //原图的高度

        int newImage_x = 0; // 要截图的坐标
        int newImage_y = 0; // 要截图的坐标
        if (width > originWidth) {
            newImage_x = (width - originWidth) / 2;
        }
        if (height > originHeight) {
            newImage_y = height - originHeight;
        }
        return cutJPG(img, newImage_x, newImage_y, originWidth, originHeight);
    }

    /**
     * 图片拉伸
     *
     * @param originalImage the original image
     * @param originWidth   the origin width
     * @param originHeight  the origin height
     * @return the buffered image
     * @throws Exception the exception
     */
    public static BufferedImage zoomPicture(String originalImage, int originWidth, int originHeight) throws Exception {
        // 原来的图片
        BufferedImage img = ImageIO.read(new File(originalImage));

        int width = img.getWidth();  // 原图的宽度
        int height = img.getHeight();  //原图的高度

        int scaledWidth = width;
        int scaledHeight = height;
        // 如果不是正方形
        if (width == height) {
            // 按照originHeight进行缩放
            scaledWidth = originHeight;
            scaledHeight = originHeight;
        } else {
            if (width > height) {
                // 按照originHeight进行缩放
                scaledWidth = (scaledWidth * originHeight) / scaledHeight;
                scaledHeight = originHeight;
            } else {
                // 宽高比例
                int originPercent = (originHeight * 100) / originWidth;
                int newPercent = (height * 100) / width;
                if (newPercent >= originPercent) {
                    // 按照originWidth进行缩放
                    scaledWidth = originWidth;
                    scaledHeight = (originHeight * scaledWidth) / scaledWidth;
                } else {
                    // 按照originHeight进行缩放
                    scaledWidth = (scaledWidth * originHeight) / scaledHeight;
                    scaledHeight = originHeight;
                }
            }
        }
        Image schedImage = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        // 新的图片
        BufferedImage bufferedImage = new BufferedImage(scaledWidth, scaledHeight, img.getType());
        Graphics2D g = bufferedImage.createGraphics();
        // 绘制
        g.drawImage(schedImage, 0, 0, null);
        g.dispose();
        return bufferedImage;
    }

    /**
     * 进行裁剪操作
     *
     * @param originalImage the original image
     * @param x             the x
     * @param y             the y
     * @param width         the width
     * @param height        the height
     * @return the buffered image
     * @throws IOException the io exception
     */
    public static BufferedImage cutJPG(BufferedImage originalImage, int x, int y, int width, int height) throws IOException {
        Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("jpg");
        ImageReader reader = iterator.next();
        // 转换成字节流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", outputStream);
        InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

        ImageInputStream iis = ImageIO.createImageInputStream(is);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
        Rectangle rect = new Rectangle(x, y, width, height);
        param.setSourceRegion(rect);
        BufferedImage bi = reader.read(0, param);
        return bi;
    }

    /**
     * 合并头像和昵称
     *
     * @param baseImage the base image
     * @param topImage  the top image
     * @return the buffered image
     * @throws Exception the exception
     */
    public static BufferedImage mergePicture(BufferedImage baseImage, BufferedImage topImage, String nickName) throws IOException {
        int width = baseImage.getWidth(null); //底图的宽度
        int height = baseImage.getHeight(null); //底图的高度
        // 按照底图的宽高生成新的图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        g.drawImage(baseImage, 0, 0, width, height, null);

        int smallWidth = topImage.getWidth(null);   // 上层图片的宽度
        // 设置上层图片放置的位置的坐标及大小
        g.drawImage(topImage, (width - smallWidth) / 2, AVATAR_Y, AVATAR_SIZE, AVATAR_SIZE, null);

        // 普通字体
        Font font = new Font("微软雅黑", Font.PLAIN, 30);
        g.setFont(font);
        g.setColor(new Color(68, 68, 68));
        FontMetrics fm = g.getFontMetrics(font);
        // 昵称长度和放置的位置
        int textWidth = fm.stringWidth(nickName);
        g.drawString(nickName, (width - textWidth) / 2, FONT_Y);
        g.dispose();

        return image;
    }


    /**
     * 按指定的字节数截取字符串（一个中文字符占3个字节，一个英文字符或数字占1个字节）
     *
     * @param sourceString 源字符串
     * @param cutBytes     要截取的字节数
     * @return
     */
    public static String cutString(String sourceString, int cutBytes) {
        if (sourceString == null || "".equals(sourceString.trim())) {
            return "";
        }
        int lastIndex = 0;
        boolean stopFlag = false;
        int totalBytes = 0;
        for (int i = 0; i < sourceString.length(); i++) {
            String s = Integer.toBinaryString(sourceString.charAt(i));
            if (s.length() > 8) {
                totalBytes += 3;
            } else {
                totalBytes += 1;
            }
            if (!stopFlag) {
                if (totalBytes == cutBytes) {
                    lastIndex = i;
                    stopFlag = true;
                } else if (totalBytes > cutBytes) {
                    lastIndex = i - 1;
                    stopFlag = true;
                }
            }
        }
        if (!stopFlag) {
            return sourceString;
        } else {
            return sourceString.substring(0, lastIndex + 1);
        }
    }

    /**
     * 合并二维码及二维码识别图
     *
     * @param baseImage   the base image
     * @param qrcodeImage the qrcode image
     * @param topImage    the top image
     * @return the buffered image
     * @throws IOException
     */
    public static BufferedImage mergeQrcode(BufferedImage baseImage, String qrcodeImage, String topImage) throws IOException {

        BufferedImage recogBufferImage = ImageIO.read(new File(topImage));
        BufferedImage qrcodeBufferImage = ImageIO.read(new File(qrcodeImage));

        int width = baseImage.getWidth(null); //底图的宽度
        int height = baseImage.getHeight(null); //底图的高度

        // 按照底图的宽高生成新的图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        g.drawImage(baseImage, 0, 0, width, height, null);

        // 设置上层图片放置的位置的坐标及大小，坐标居中
        int topWidth = recogBufferImage.getWidth();
        g.drawImage(recogBufferImage, (width - topWidth) / 2, RECOGNITION_QRCODE_Y, RECOGNITION_QRCODE_SIZE, RECOGNITION_QRCODE_SIZE, null);
        g.drawImage(qrcodeBufferImage, (width - QRCODE_SIZE) / 2, QRCODE_Y, QRCODE_SIZE, QRCODE_SIZE, null);
        g.dispose();
        return image;
    }

    /**
     * 图片上添加文字
     *
     * @param src        the src
     * @param copywriter the copywriter
     * @return the buffered image
     * @throws Exception the exception
     */
    public static BufferedImage drawTextInImage(BufferedImage src, String copywriter) throws IOException {
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(src, 0, 0, width, height, null);

        // 长度和位置
        Font font = new Font("微软雅黑", Font.PLAIN, 26);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        int textWidth = fm.stringWidth(copywriter);
        g.setColor(new Color(85, 85, 85));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 先按字节来换行，英文单词空格问题暂时未考虑
        if (copywriter.getBytes().length > 63) {
            String firstLine = cutString(copywriter, 63);
            String secondLine = copywriter.substring(firstLine.length(), copywriter.length());
            g.drawString(firstLine, (width - fm.stringWidth(firstLine)) / 2, COPYWRITER);
            g.drawString(secondLine, (width - fm.stringWidth(secondLine)) / 2, COPYWRITER + 35);
        } else {
            g.drawString(copywriter, (width - textWidth) / 2, COPYWRITER);
        }
        g.dispose();

        return image;
    }

    /**
     * 方形转为圆形
     *
     * @param img    the img
     * @param radius the radius 半径
     * @return the buffered image
     * @throws Exception the exception
     */
    public static BufferedImage convertRoundedImage(BufferedImage img, int radius) throws IOException {
        BufferedImage result = new BufferedImage(radius, radius, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        //在适当的位置画图
        g.drawImage(img, (radius - img.getWidth(null)) / 2, (radius - img.getHeight(null)) / 2, null);

        //圆角
        RoundRectangle2D round = new RoundRectangle2D.Double(0, 0, radius, radius, radius * 2, radius * 2);
        Area clear = new Area(new Rectangle(0, 0, radius, radius));
        clear.subtract(new Area(round));
        g.setComposite(AlphaComposite.Clear);

        //抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.fill(clear);
        g.dispose();

        return result;
    }

    /**
     * 图像等比例缩放
     *
     * @param img     the img
     * @param maxSize the max size
     * @param type    the type
     * @return the scaled image
     */
    private static BufferedImage getScaledImage(BufferedImage img, int maxSize, int type) {
        int w0 = img.getWidth();
        int h0 = img.getHeight();
        int w = w0;
        int h = h0;
        // 头像如果是长方形：
        // 1:高度与宽度的最大值为maxSize进行等比缩放,
        // 2:高度与宽度的最小值为maxSize进行等比缩放
        if (type == 1) {
            w = w0 > h0 ? maxSize : (maxSize * w0 / h0);
            h = w0 > h0 ? (maxSize * h0 / w0) : maxSize;
        } else if (type == 2) {
            w = w0 > h0 ? (maxSize * w0 / h0) : maxSize;
            h = w0 > h0 ? maxSize : (maxSize * h0 / w0);
        }
        Image schedImage = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(schedImage, 0, 0, null);
        return bufferedImage;
    }

    /**
     * 对头像处理
     *
     * @param image  the image
     * @param radius the radius
     * @return the buffered image
     * @throws Exception the exception
     */
    public static BufferedImage createRoundedImage(String image, int radius) throws Exception {
        BufferedImage img = ImageIO.read(new File(image));
        // 1. 按原比例缩减
        BufferedImage fixedImg = getScaledImage(img, radius, 2);
        // 2. 居中裁剪
        fixedImg = cutPicture(fixedImg, radius, radius);
        // 3. 把正方形生成圆形
        BufferedImage bufferedImage = convertRoundedImage(fixedImg, radius);
        return bufferedImage;
    }


}