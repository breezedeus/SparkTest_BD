import org.apache.spark.mllib.classification.LogisticRegressionModel
import org.apache.spark.mllib.classification.{LogisticRegressionWithLBFGS, LogisticRegressionWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.regression.GeneralizedLinearAlgorithm
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.util.MLUtils

/**
 * Created by king on 15/6/11.
 */
object MLAppLR {

  def parseArguments(args: Array[String]) = {
    val options = args.map {
      arg =>
        arg.dropWhile(_ == '-').split('=') match {
          case Array(opt, v) => (opt -> v)
          case _ => throw new IllegalArgumentException("Invalid argument: " + arg)
        }
    }

    val arguments = scala.collection.mutable.Map[String, Any]("algName" -> "sgd")
    val numIterations = 50
    val regParam = 0.1
    val stepSize = 0.1
    val convergenceTol = 1e-5
    val miniBatchFraction = 0.01
    arguments += ("numIterations" -> numIterations, "regParam" -> regParam, "stepSize" -> stepSize,
      "convergenceTol" -> convergenceTol, "miniBatchFraction" -> miniBatchFraction)

    options.foreach {
      case ("train", v) => arguments("trainFileName") = v
      case ("test", v) => arguments("testFileName") = v
      case ("output", v) => arguments("outputFileName") = v
      case ("algName", v) => arguments("algName") = v
      case ("numIterations", v) => arguments("numIterations") = v.toInt
      case ("regParam", v) => arguments("regParam") = v.toDouble
      case ("stepSize", v) => arguments("stepSize") = v.toDouble
      case ("convergenceTol", v) => arguments("convergenceTol") = v.toDouble
      case ("miniBatchFraction", v) => arguments("miniBatchFraction") = v.toDouble
      case (opt, _) => throw new IllegalArgumentException("Invalid option: " + opt)
    }
    arguments
  }

  def getData(sc: SparkContext, arguments: scala.collection.mutable.Map[String, Any]) = {
    val trainFileName = arguments("trainFileName").toString
    val testFileName = arguments("testFileName").toString

    val train = MLUtils.loadLibSVMFile(sc, trainFileName)
    val test = MLUtils.loadLibSVMFile(sc, testFileName)
    (train, test)
  }

  def getAlg(arguments: scala.collection.mutable.Map[String, Any]): GeneralizedLinearAlgorithm[LogisticRegressionModel] = {
    val algName: String = arguments("algName").toString
    algName match {
      case "lbfgs" => {
        val lrAlg = new LogisticRegressionWithLBFGS()
        lrAlg.setIntercept(true)
        lrAlg.optimizer.setNumIterations(arguments("numIterations").asInstanceOf[Int])
          .setRegParam(arguments("regParam").asInstanceOf[Double])
          .setConvergenceTol(arguments("convergenceTol").asInstanceOf[Double])
        lrAlg
      }
      case _ => {
        val lrAlg = new LogisticRegressionWithSGD()
        lrAlg.setIntercept(true)
        lrAlg.optimizer.setNumIterations(arguments("numIterations").asInstanceOf[Int])
          .setRegParam(arguments("regParam").asInstanceOf[Double])
          .setMiniBatchFraction(arguments("miniBatchFraction").asInstanceOf[Double])
          .setStepSize(arguments("stepSize").asInstanceOf[Double])
        //.setUpdater(new SquaredL2Updater)
        lrAlg
      }
    }
  }


  def main (args: Array[String]) {
    val conf = new SparkConf().setAppName("MLAppLR_BD")
    val sc = new SparkContext(conf)
    val arguments = parseArguments(args)
    println(args.mkString(", "))
    println(arguments.mkString(", "))

    val (train, test) = getData(sc, arguments)
    printf("#train examples: %d, #features: %d\n", train.count, train.first().features.size)
    printf("#test examples: %d, #features: %d\n", test.count, test.first().features.size)

    val lrAlg = getAlg(arguments)

    val model = lrAlg.run(train)
    model.clearThreshold()
    println(model.weights)
    println(model.intercept)

    val scoreAndLabels = test.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }

    if (arguments.contains("outputFileName")) {
      scoreAndLabels.saveAsTextFile(arguments("outputFileName").toString)
    }

    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    val auROC = metrics.areaUnderROC()

    println("AUC for the test set = " + auROC)

  }

}
