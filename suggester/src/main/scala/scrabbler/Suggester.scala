package scrabbler

import scala.util.Try
import java.io.File

object SuggestionApp {

  def main(args: Array[String]): Unit = {
    Console println "hello from the suggester application!"
    val input = Try(new File(args(0)))
    val query = Try(args(1))
    val num = Try(args(2).toInt)
    for(q <- query; n <- num; kin <- input if kin.canRead){
      val start = System.currentTimeMillis

      val index = SerializationUtils.kryo_deserialize(kin)
      //Console println s"heap size after deserializing second instance of index: ${Runtime.getRuntime().totalMemory()}"
      //Console println s"querying index with $q and returning max $n results"
      val results = index(q).take(n).mkString(",")
      println(results.size + s" total results, top $n are" + results.toString)
    }
  }
}
