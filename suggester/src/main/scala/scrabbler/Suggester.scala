package scrabbler


import scala.util.{Try,Success,Failure}
import java.io.{File,FileInputStream}

object SuggestionApp {

  def deserialize(file: File): Core.Index = {
    import java.io.{ObjectInputStream,FileInputStream}
    val in = new FileInputStream(file)
    val reader = new ObjectInputStream(in)
    reader.readObject().asInstanceOf[Core.Index]
  }

  def main(args: Array[String]): Unit = {
    Console println "hello from the suggester application!"
    val input: Try[File] = Try(new File(args(0)))
    val query = Try(args(1))
    val num = Try(args(2).toInt)
    for(q <- query; n <- num; in <- input if in.canRead){
      val start = System.currentTimeMillis

      val index = deserialize(in)

      println("deserialized index with " + index.keys.size + " keys in" + (System.currentTimeMillis - start)/1000 + " seconds")
      Console println s"querying index with $q and returning max $n results"
      val results = index(q).take(n).mkString(",")
      println(results.size + s" total results, top $n are" + results.toString)
    }
  }
}
