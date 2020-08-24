package dev.cheerfun.pixivic.basic.nsfw.config;

import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/24 6:34 下午
 * @description NSFWConfig
 */
@Slf4j
@Configuration
public class NSFWConfig {
    @Value("${nsfw.modelPath}")
    private String modelPath;

    @Bean
    public ComputationGraph computationGraph() {
        ComputationGraph model = null;
        try {
            log.info("开始初始化nsfw模型");
            model = KerasModelImport.importKerasModelAndWeights(modelPath);
        } catch (IOException | UnsupportedKerasConfigurationException | InvalidKerasConfigurationException e) {
            log.error("初始化nsfw模型失败");
            e.printStackTrace();
        }
        log.info("初始化nsfw模型成功");
        return model;
    }
}
