package dev.cheerfun.pixivic;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/16 3:01 下午
 * @description Consumer
 */
public class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

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

        int a = 123265;
        int b = 2;
        long l = System.currentTimeMillis();
        System.out.println(l);
        for (int i = 0; i < 10000; i++) {
            b = a * b * a;
        }
        System.out.println(b);
        System.out.println(System.currentTimeMillis() - l);
    }
}

