package com.example.dog_identifier.ai
//import org.deeplearning4j
//
//import org.deeplearning4j.datasets.iterator._
//import org.deeplearning4j.datasets.iterator.impl._
//import org.deeplearning4j.nn.api._
//import org.deeplearning4j.nn.multilayer._
//import org.deeplearning4j.nn.graph._
//import org.deeplearning4j.nn.conf._
//import org.deeplearning4j.nn.conf.inputs._
//import org.deeplearning4j.nn.conf.layers._
//import org.deeplearning4j.nn.weights._
//import org.deeplearning4j.optimize.listeners._
//import org.deeplearning4j.datasets.datavec.RecordReaderMultiDataSetIterator
//import org.nd4j.evaluation.classification._

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer


//
//import org.nd4j.linalg.learning.config._ // for different updaters like Adam, Nesterovs, etc.
//import org.nd4j.linalg.activations.Activation // defines different activation functions like RELU, SOFTMAX, etc.
//import org.nd4j.linalg.lossfunctions.LossFunctions // mean squared error, multiclass cross entropy, etc.

class DogFinder {


    /*
    NOTES ON FIXING CANNOT COPY BYTEBUFFER THING:
    -DataType.FLOAT32 is whats actually present, they gave UINT8 (possible issue)
    -NEXT: CHECK THE SIZE THING (currently intArrayOf(1, 1001)), figure out what its supposed to be:
    https://github.com/tensorflow/examples/blob/master/lite/examples/image_classification/android/EXPLORE_THE_CODE.md
    int[] probabilityShape =
    tflite.getOutputTensor(probabilityTensorIndex).shape();
     */
    fun doStuff(image: Bitmap, context: Context): Map<String, Float>? {


        val tImage = loadImage(image)

        // Create a container for the result and specify that this is a quantized model.
        // Hence, the 'DataType' is defined as UINT8 (8-bit unsigned integer)
        val probabilityBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 1001), DataType.FLOAT32)

        // Initialise the model
        try {
            val tfliteModel = FileUtil.loadMappedFile(
                context,
                "mobilenet_v1_1.0_224.tflite"
            ) as ByteBuffer


            val tflite = Interpreter(tfliteModel)

            val type = tflite.getOutputTensor(0).dataType()
            Log.e("wack", "Types: " + type.toString() + " and " + DataType.UINT8.toString())


            tflite.run(tImage.buffer, probabilityBuffer.buffer)

        } catch (e: IOException) {
            Log.e("tfliteSupport", "Error reading model", e)
        }


        val ASSOCIATED_AXIS_LABELS = "labels.txt"

        var associatedAxisLabels: List<String>? = null

        try {
            associatedAxisLabels = FileUtil.loadLabels(context, ASSOCIATED_AXIS_LABELS)
        } catch (e: IOException) {
            Log.e("tfliteSupport", "Error reading label file", e)
        }

        // Post-processor which dequantize the result

        // Post-processor which dequantize the result
        val probabilityProcessor = TensorProcessor.Builder().add(NormalizeOp(0f, 255f)).build()

        if (null != associatedAxisLabels) {
            // Map of labels and their corresponding probability
            val labels = TensorLabel(
                associatedAxisLabels,
                probabilityProcessor.process(probabilityBuffer)
            )

            // Create a map to access the result based on label
            return labels.mapWithFloatValue
        }

        return null

//        val width = image.width
//        val height = image.height
//        val rotateDegrees = 0
//
//        val size = if (height > width) width else height

//        val imageProcessor: ImageProcessor = ImageProcessor.Builder()
//            // Center crop the image to the largest square possible
//            .add(ResizeWithCropOrPadOp(size, size))
//        // Resize using Bilinear or Nearest neighbour
//        .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
//        // Rotation counter-clockwise in 90 degree increments
//            .add(Rot90Op(rotateDegrees / 90))
////            .add(NormalizeOp(127.5, 127.5))
//        .add(QuantizeOp(128f, 1/128f))
//        .build()


    }

    private fun loadImage(bitmap: Bitmap): TensorImage {

        val tImage = TensorImage(DataType.UINT8)

        tImage.load(bitmap)

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(bitmap.height, bitmap.width, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        return imageProcessor.process(tImage)
    }


}