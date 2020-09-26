package dev.cheerfun.pixivic;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/16 3:01 下午
 * @description Consumer
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        Map<String, String> collect =
                Arrays.stream("a=a&b=b&c=c".split("&"))
                        .map(e -> Arrays.asList(e.split("=")))
                        .collect(Collectors.toMap(e -> e.get(0), e -> e.get(1)));
        System.out.println(collect);
        System.out.println(10000 * 1000 / 1024 / 1024);
        System.out.println(4 * 1000 * 10000 / 1024 / 1024);
      /*  try (Graph g = new Graph()) {
            final String value = "Hello from " + TensorFlow.version();

            // Construct the computation graph with a single operation, a constant
            // named "MyConst" with a value "value".
            try (Tensor t = Tensor.create(value.getBytes("UTF-8"))) {
                // The Java API doesn't yet include convenience functions for adding operations.
                g.opBuilder("Const", "MyConst").setAttr("dtype", t.dataType()).setAttr("value", t).build();
            }

            // Execute the "MyConst" operation in a Session.
            try (Session s = new Session(g);
                 // Generally, there may be multiple output tensors,
                 // all of them must be closed to prevent resource leaks.
                 Tensor output = s.runner().fetch("MyConst").run().get(0)) {
                System.out.println(new String(output.bytesValue(), "UTF-8"));
            }
        }*/
       /* ComputationGraph m = importFunctionalModelH5Test("/Users/oysterqaq/.keras/models/vgg16_weights_tf_dim_ordering_tf_kernels_notop.h5",null,false);
        System.out.println(m.getLayer(1).getConfig().getLayerName());*/
        //ComputationGraph m =KerasModelImport.importKerasModelAndWeights("/Users/oysterqaq/Desktop/model-resnet_custom_v4.h5",false);
        //m.init();
  /*      ZooModel zooModel = VGG16.builder().build();
        ComputationGraph vgg16 = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
        for (Layer layer : vgg16.getLayers()) {
            System.out.println(layer.getConfig().getLayerName());
        }
        String path = "/Users/oysterqaq/Desktop/1.jpg";
        File file = new File(path);
        NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
        INDArray image = loader.asMatrix(file);
        DataNormalization scaler = new VGG16ImagePreProcessor();
        scaler.transform(image);

// Feed Forward
        long startTime = System.currentTimeMillis();
        Map<String, INDArray> stringINDArrayMap = vgg16.feedForward(image, false);
        long stopTime = System.currentTimeMillis();

        System.out.println("time elapsed : " + (stopTime-startTime));

// Extract fc7  features
        INDArray resultfc7 = stringINDArrayMap.get("block5_pool");
        System.out.println("block5_pool"+resultfc7.shapeInfoToString());
*/
/*
                ZooModel zooModel =  VGG16.builder().build();
        ComputationGraph pretrainedNet = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
        for (Layer layer : pretrainedNet.getLayers()) {
            System.out.println(layer.getConfig().getLayerName());
        }
        //NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
        NativeImageLoader loader = new NativeImageLoader(224, 224, 3, new ColorConversionTransform(COLOR_BGR2RGB));
        INDArray image = loader.asMatrix(new File("/Users/oysterqaq/Desktop/1.jpg"),false);
        Nd4j.getExecutioner().execAndReturn(new BroadcastSubOp(image.dup(), Nd4j.create(new double[] {103.939, 116.779, 123.68}), image, 1));
        DataNormalization scaler = new VGG16ImagePreProcessor();
        scaler.transform(image);
        INDArray[] output = pretrainedNet.output(false,image);
        System.out.println(output.length);
        float[] floats = output[0].data().asFloat();
        for (int i = 0; i < floats.length; i++) {
            System.out.println(round(floats[i], 3));
        }*/



        /*FineTuneConfiguration fineTuneConf = new FineTuneConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Nesterovs(5e-5))
              //  .seed(seed)
                .build();
        ComputationGraph vgg16Transfer = new TransferLearning.GraphBuilder(pretrainedNet)
                .fineTuneConfiguration(fineTuneConf)
                .setFeatureExtractor("fc2")
                .removeVertexKeepConnections("predictions")

                .addLayer("predictions",
                        new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                                .nIn(4096).nOut(numClasses)
                                .weightInit(WeightInit.XAVIER)
                                .activation(Activation.SOFTMAX).build(), "fc2")
                .build();*/

    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

}

