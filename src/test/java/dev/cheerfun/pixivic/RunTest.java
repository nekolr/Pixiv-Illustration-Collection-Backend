package dev.cheerfun.pixivic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-06-28 23:05
 * @description
 */
public class RunTest {
    public static void main(String[] args) {
        String body = "<div class=\"pos-r pd10 post-list box mar10-b content\">\n" +
                "<div class=\"pos-r cart-list\">\n" +
                "<div class=\"thumb pos-r\">\n" +
                "<span class=\"pos-a post-cap fs12 shadow\"></span>\n" +
                "<div style=\"background-image:url('https://sinaimg.acgmh.com/large/0068TvVBgy1g5rzwlavl4j30u015stng.jpg')\" class=\"preview thumb-in\"></div>\n" +
                "<a target=\"_blank\" href=\"https://www.acgmh.com/28678.html\" class=\"link-block\"></a>\n" +
                "</div>\n" +
                "<div class=\"post-info pos-r pd10 post-side\">\n" +
                "<div class=\"post-header pos-r mar10-b fs13\">\n" +
                "<span class=\"pos-a\">\n" +
                "<a href=\"https://www.acgmh.com/user/1\"><img src=\"https://www.acgmh.com/wp-content/uploads/bfi_thumb/1972fbe5888ce3_avatar-6qdrhqtrhgfvyf9xil4cgq3m3v6u3f04kqgiublp2aw.jpg\" class=\"avatar\" width=\"50\" height=\"50\" style=\"background-color:#02c793\"/></a>\n" +
                "</span>\n" +
                "<a id=\"user-1\" class=\"users\" href=\"https://www.acgmh.com/user/1\">柚子</a>\n" +
                "<span class=\"dot\"></span><span class=\"gray\"><time class=\"timeago\" datetime=\"2019-10-09T07:21:51+08:00\" data-timeago=\"2019-10-9 7:21:51\" ref=\"timeAgo\">2019-10-9 7:21:51</time>\n" +
                "</span>            \n" +
                "</div>\n" +
                "<h2 class=\"entry-title\"><a target=\"_blank\" href=\"https://www.acgmh.com/28678.html\" rel=\"bookmark\">【PV】《宝石商人理查德的谜鉴定》先导PV公开 2020年一月播出</a></h2>\n" +
                "<div class=\"mar10-b post-ex mar10-t mobile-hide\">《宝石商人理查德的谜鉴定》改编自辻村七子著，雪广うたこ插画同名小说，改作于2019年8月宣布动画化，动画由朱夏负责动画制作，并预定于...</div>\n" +
                "<div class=\"post-meta meta mar10-t clearfix\">\n" +
                "<span class=\"list-category-l hide5\">\n" +
                "<a class=\"list-category bg-blue-light color\" href=\"https://www.acgmh.com/category/news/comics\">漫画改编</a>\n" +
                "<span class=\"dot\"></span>\n" +
                "</span><i class=\"iconfont zrz-icon-font-eye\"></i>521<span class=\"dot\">\n" +
                "</span><i class=\"iconfont zrz-icon-font-pinglun\"></i>0<span class=\"dot\"></span>\n" +
                "<i class=\"iconfont zrz-icon-font-collect\"></i>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>";

        Document doc = Jsoup.parse(body);
        Elements elements = doc.getElementsByClass("pos-r pd10 post-list box mar10-b content");
        for (Element element : elements) {
            String style = element.getElementsByClass("preview thumb-in").get(0).attributes().get("style");
            String cover = style.substring(style.indexOf("('") + 2, style.length() - 2);
            String author = element.getElementsByClass("users").text();
            String createDate = element.getElementsByClass("timeago").text();
            Elements e = element.getElementsByClass("entry-title");
            String title = e.text();
            String refererUrl = e.get(0).getElementsByTag("a").get(0).attributes().get("href");
            String intro = element.getElementsByClass("mar10-b post-ex mar10-t mobile-hide").text();
            //System.out.println(cover+author+createDate+title+intro);




        }
    }
}
