package dev.cheerfun.pixivic.biz.proxy.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/12 2:39 PM
 * @description VIPProxyServer
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VIPProxyServer {
    @JsonIgnore
    private Integer id;
    private String serverAddress;
    @JsonIgnore
    private Integer useFlag;
    @JsonIgnore
    private LocalDateTime createDate;
}
