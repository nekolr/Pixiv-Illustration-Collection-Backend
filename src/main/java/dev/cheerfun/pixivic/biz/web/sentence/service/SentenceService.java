package dev.cheerfun.pixivic.biz.web.sentence.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.sentence.mapper.SentenceMapper;
import dev.cheerfun.pixivic.biz.web.sentence.po.Sentence;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/30 9:19 PM
 * @description SentenceService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SentenceService {
    private final ObjectMapper objectMapper;
    private final SentenceMapper sentenceMapper;
    final private HttpClient httpClient;


}
