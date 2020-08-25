package dev.cheerfun.pixivic.basic.nsfw.service;

import dev.cheerfun.pixivic.basic.nsfw.domain.AnalysisResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
    private final NativeImageLoader loader;

    public AnalysisResult compute(String path) throws IOException {
        INDArray output = model.outputSingle(loader.asMatrix(new File(path), false).div(255));
        return new AnalysisResult(output.data().asFloat());
    }

}
