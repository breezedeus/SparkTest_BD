package main.scala

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.reflect.ClassTag

/**
 * Created by king on 15/6/11.
 */
object GraphXApp {

  def createGraph(sc: SparkContext): Graph[Map[VertexId, (Int, Double)], String] = {
    val initNumAndWeight: (Int, Double) = (3, 1.0)
    // Create an RDD for the vertices
    val users: RDD[(VertexId, Map[VertexId, (Int, Double)])] =
      sc.parallelize(Array((3L, Map(3L -> initNumAndWeight)), (2L, Map(2L -> initNumAndWeight)), (5L, Map(5L -> initNumAndWeight)),
        (7L, Map(7L -> initNumAndWeight)), (1L, Map(1L -> initNumAndWeight)), (4L, Map(4L -> initNumAndWeight))
        ))
    // Create an RDD for edges
    val relationships: RDD[Edge[String]] =
      sc.parallelize(Array(Edge(3L, 1L, "collab"), Edge(5L, 4L, "advisor"), Edge(2L, 7L, "advisor"), Edge(2L, 1L, "advisor"), Edge(3L, 4L, "collab"),
        Edge(7L, 5L, "colleague"), Edge(1L, 5L, "pi"), Edge(4L, 2L, "pi"), Edge(7L, 3L, "collab")))
    // Build the initial Graph
    val graph = Graph(users, relationships)
    graph
  }

  def readGraphFromFile(sc: SparkContext, fileName: String) = {
    val initNumAndWeight: (Int, Double) = (3, 1.0)
    val graph = GraphLoader.edgeListFile(sc, fileName)
    graph.mapVertices((vertexId, _) => Map(vertexId -> initNumAndWeight))
    //val newVertexRDD = graph.vertices.mapValues((vertexId, _) => Map(vertexId -> initNumAndWeight))
    //Graph(newVertexRDD, graph.edges)
  }

  def transmitNeighbor[E: ClassTag](vertices: VertexRDD[Map[VertexId, (Int, Double)]], edges: EdgeRDD[E], transmitNum: Int) = {
    val newGraph2 = Graph[Map[VertexId, (Int, Double)], E](vertices, edges).aggregateMessages[Map[VertexId, (Int, Double)]] (
      triplet => {
        if (triplet.srcAttr != null) {
          for ((key, vAttrEle) <- triplet.srcAttr) {
            if (vAttrEle._1 == transmitNum)
              triplet.sendToDst(Map(key -> (vAttrEle._1 - 1, vAttrEle._2)))
          }
        }
      },
      (a, b) => {
        val merged = scala.collection.mutable.Map(a.toSeq: _*)
        for ((vertexId, numAndWeight) <- b) {
          val (_, oldWeight) = merged.getOrElse(vertexId, (-1, .0))
          merged(vertexId) = (numAndWeight._1, numAndWeight._2 + oldWeight)
        }
        Map(merged.toSeq: _*)
      }
    )
    newGraph2
  }

  def main (args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("GraphXApp")
    val sc = new SparkContext(conf)
    //val graph = createGraph(sc)
    val graph = readGraphFromFile(sc, "data/graphxapp")

    var graphVertices: VertexRDD[Map[VertexId, (Int, Double)]] = graph.vertices
    for (transmitNum <- (1 to 3).reverse) {
      graphVertices = transmitNeighbor(graphVertices, graph.edges, transmitNum)
      graphVertices.collect.foreach(x => printf("dst: %d;\t src: %s\n", x._1, x._2.mkString(" ")))
      println("Step " + transmitNum)
    }
  }

}
