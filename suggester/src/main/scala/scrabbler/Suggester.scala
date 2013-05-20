package scrabbler

import scala.util.{Try,Success,Failure}
import java.io.File

object SuggestionApp {

  val usage = "usage: scrabble-suggester <index> <query> <number of results> \n ie: 'scrabble-suggester words.bin bat 5'"

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
    val input = Try(new File(args(0)))
    val query = Try(args(1))
    val num = Try(args(2).toInt)
    for(q <- query; n <- num; kin <- input if kin.canRead){
      val start = System.currentTimeMillis
      val index = SerializationUtils.kryo_deserialize(kin)
      //Console println s"heap size after deserializing second instance of index: ${Runtime.getRuntime().totalMemory()}"
      //Console println s"querying index with $q and returning max $n results"
      val results = index(q).take(n).mkString("\n\t\t")
      println(index(q).size + s" total results, top $n are:\n\t\t" + results)
    }
  }
}
