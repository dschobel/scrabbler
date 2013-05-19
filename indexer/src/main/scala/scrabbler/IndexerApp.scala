package scrabbler

import scala.util.Try
import java.io.File

/**
 * A console application to produce an inverted index for the substrings of a given word list
 * It takes two parameters, the first is  a data source
 * The second is filename to which the index file will be written
 */
object IndexApp {

  def main(args: Array[String]): Unit = {
    Console println s"reading data from ${args(0)}"
    Console println s"writing index to ${args(1)}"

    val input = Try(new File(args(0)))
    val kryo_output = Try(new File(args(1)))

    for(kout <- kryo_output if !kout.canWrite){
      kout.createNewFile
    }


    for(in <- input;  kout <- kryo_output; if in.canRead && kout.canWrite){
      Console println "creating index..."
      val start = System.currentTimeMillis
      val index = Indexer.create(in)
      //Console println s"heap size after creating index: ${Runtime.getRuntime().totalMemory()}"
      Console println s"Index created in ${(System.currentTimeMillis - start) / 1000} seconds"
      println("Created index with " + index.keys.size + " keys")
      Console println "Serializing index..."
      SerializationUtils.kryo_serialize(index, kout)
    }
  }
}
