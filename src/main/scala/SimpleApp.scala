import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by king on 15/6/6.
 */
/* SimpleApp.scala */

object SimpleApp {
  def main(args: Array[String]) {
    val logFile = "/Users/king/Documents/Useful_Softwares/spark-1.2.0-bin-hadoop2.4/README.md" // Should be some file on your system
    val conf = new SparkConf().setMaster("local").setAppName("Simple Application")
    val sc = new SparkContext(conf)
    val logData = sc.textFile(logFile, 2).cache()
    println("first line: %s".format(logData.first()))
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
    val fm = logData.flatMap(line => line.split(" "))
    fm.saveAsTextFile("/Users/king/Documents/WhatIHaveDone/Test/xxx")
    println(fm.collect().toString)
  }
}

