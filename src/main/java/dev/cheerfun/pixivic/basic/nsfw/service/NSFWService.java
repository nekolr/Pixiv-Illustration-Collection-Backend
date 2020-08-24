package dev.cheerfun.pixivic.basic.nsfw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/24 6:34 下午
 * @description NSFWService
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NSFWService {
    private final ComputationGraph model;

    public void check() throws IOException {
        NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
        INDArray[] output = model.output(loader.asMatrix(new File("/Users/oysterqaq/Desktop/t-1.jpg"), false).div(255));
        System.out.println(output);
    }

}
