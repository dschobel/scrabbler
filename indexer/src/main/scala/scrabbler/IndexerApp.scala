package scrabbler


import scala.util.{Try,Success,Failure}
import java.io.{File,FileInputStream}


object IndexApp {
  def serialize(index: Core.Index, destination: File): Unit = {
    import java.io.{FileOutputStream,ObjectOutputStream}

    assert(destination.canWrite)
    for (fos <- Try(new FileOutputStream(destination)); 
           o <- Try(new ObjectOutputStream(fos))){ o.writeObject(index) }
  }

  def main(args: Array[String]): Unit = {
    Console println "hello from the Indexer application!"
    Console println s"reading data from ${args(0)}"
    Console println s"writing index to ${args(1)}"

    val input:  Try[File] = Try(new File(args(0)))
    val output: Try[File] = Try(new File(args(1)))

    for(out <- output if !out.canWrite){
      out.createNewFile
    }

    for(in <- input; out <- output; if in.canRead && out.canWrite){
      Console println "creating index..."
      var start = System.currentTimeMillis
      val index = Indexer.create(in)
      Console println s"done. Index created in ${(System.currentTimeMillis - start) / 1000} seconds"
      println("done. Created index with " + index.keys.size + " keys")
      //Console println s"first ten entries:" 
      //index.take(10).foreach{case(k,v) => {
       // println(k + "\t" + v.mkString(","))
      //}}
      //index.foreach{case(k,v) => {
       // println(k + "\t" + v.mkString(","))
      //}}

      Console println "serializing index..."
      start = System.currentTimeMillis
      serialize(index,out)
      Console println s"done. Serialized data in ${(System.currentTimeMillis - start) / 1000} seconds"
      Console println "done"
    }
  }
}
