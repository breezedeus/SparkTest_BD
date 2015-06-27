
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.stat.{Statistics, MultivariateStatisticalSummary}
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by king on 15/6/9.
 */
object MLApp {
  def main (args: Array[String]) {
    val conf = new SparkConf().setAppName("Simple Application")
    val sc = new SparkContext(conf)
    val dataDir = "data"
    val train_file = dataDir + "/svmguide1"
    val examples: RDD[LabeledPoint] = MLUtils.loadLibSVMFile(sc, train_file)
    println(examples.count())
    val obs = { for (ele <- examples) yield ele.features }
    val summary: MultivariateStatisticalSummary = Statistics.colStats(obs)
    println(summary.mean)
    println(summary.variance)
    println(summary.numNonzeros)
  }

}
