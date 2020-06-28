package dev.cheerfun.pixivic;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.StaxWriter;
import dev.cheerfun.pixivic.biz.sitemap.po.Url;
import dev.cheerfun.pixivic.biz.sitemap.po.UrlSet;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RunTest {

    int i = 0;
    volatile int j = 0;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Url url = new Url("1", "2", "3", "4");
        Url url2 = new Url("1", "2", "3", "4");
        UrlSet urlSet = new UrlSet();
        List<Url> urlList = new ArrayList<>();
        urlList.add(url);
        urlList.add(url2);
        urlSet.setUrlList(urlList);
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        Writer writer = new StringWriter();
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        xstream.toXML(urlSet, writer);
        System.out.println(writer.toString());
        //System.out.println(xstream.toXML(urlSet));
        System.out.println(new Date());
    }

}
