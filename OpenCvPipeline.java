package org.example;

import org.opencv.core.Mat;

public abstract class OpenCvPipeline {

    public abstract Mat processFrame(Mat input);
}
