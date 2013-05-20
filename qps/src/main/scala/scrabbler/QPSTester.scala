package scrabbler


import scala.util.{Try,Failure,Success}
import java.io.File

/**
 * a console application to gather metrics for repeated queries
 */
object QPSTester {

  val usage = "usage: qps <index> <number of queries> <results per query> \n ie: 'qps words.bin 10000 3'"

  def checkArgs[T](t: Try[T], pred: (T => Boolean), msg: String = ""): Unit = t match {
      case Failure(exn: Exception) => { println("parsing argument failed with " + exn.getMessage)
                                      System.exit(0) }
      case Success(value: T) => {
        if(!pred(value)){
          println("bad argument: " + msg)
          println(usage)
          System.exit(0)
        }
      }
  }

  def checkArgs(t: Try[_]): Unit = t match {
      case Failure(exn: Exception) => { 
        println("parsing argument failed with " + exn.getMessage)
        println(usage)
        System.exit(0) 
      }
      case _ => ()
  }


  def main(args: Array[String]): Unit = {
    Console println "QPS tests"
    val input = Try(new File(args(0)))
    val numQueries = Try(args(1).toInt)
    val resultsPerQuery = Try(args(2).toInt)

    checkArgs(numQueries)

    checkArgs(resultsPerQuery)

    checkArgs(input,(file:File) => file.canRead, "index file is not readable")


    var queryLength = 0
    val queryStart = System.currentTimeMillis

    for(in <- input if in.canRead; numQ <- numQueries; rpq <- resultsPerQuery){
      val index = SerializationUtils.kryo_deserialize(in)
      val queries = SearchUtils.genSubstrings("papua") ++ SearchUtils.genSubstrings("mississippi") ++ SearchUtils.genSubstrings("abracadabra")
      for(i <- 1 to numQ){  

        val query = util.Random.shuffle(queries).head
        val results = index(query).take(rpq)
        queryLength += query.length
      }

      val totalQueryTime = (System.currentTimeMillis - queryStart) / 1000
      val avgQuerySz: Double = queryLength / numQ.asInstanceOf[Double]
      val qps: Double = numQ / totalQueryTime.asInstanceOf[Double] 
      val queriesPerSecondPerQuerySize = qps / avgQuerySz

      Console println s"total query time: $totalQueryTime seconds"
      Console println s"qps: $qps"
      Console println s"average query size: $avgQuerySz"
      Console println s"queries per second normalized by query size: $queriesPerSecondPerQuerySize"
    }
  }
}
