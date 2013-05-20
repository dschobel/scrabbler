package scrabbler

import scala.util.{Try,Success,Failure}
import java.io.File

/**
 * A console application to produce an inverted index for the substrings of a given word list
 * It takes two parameters, the first is  a data source
 * The second is filename to which the index file will be written
 */
object IndexApp {

  val usage = "usage:  <scrabble-indexer> <number of queries> <results per query> \n ie: 'qps words.bin 10000 3'"

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
    if(args.length < 2){
      Console println "application requires two command line arguments, the word list source and the desired index destination"
      System.exit(0)
    }

    val input = Try(new File(args(0)))
    val kryo_output = Try(new File(args(1)))
    Console println s"reading word list from ${args(0)}"
    Console println s"writing index to ${args(1)}"


    for(kout <- kryo_output if !kout.canWrite){
      kout.createNewFile
    }


    for(in <- input;  kout <- kryo_output; if in.canRead && kout.canWrite){
      Console println "creating index... "
      val start = System.currentTimeMillis
      val index = Indexer.create(in)
      //Console println s"heap size after creating index: ${Runtime.getRuntime().totalMemory()}"
      Console println "Index created with " + index.keys.size + s" keys in ${(System.currentTimeMillis - start) / 1000} seconds"
      Console println "Serializing index..."
      SerializationUtils.kryo_serialize(index, kout)
    }
  }
}
