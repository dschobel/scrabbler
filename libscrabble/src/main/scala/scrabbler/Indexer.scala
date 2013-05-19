package scrabbler

import scala.util.Try
import java.io.File
import collection.SortedSet

object Indexer {


  val orderByDescendingScore = Ordering.by[(Core.Score, Core.Word),Core.Score](- _._1)

  def normalize(str: String) = str.toUpperCase.trim

  def create(file: File): Core.Index ={
    
    val lines = (io.Source.fromFile(file) getLines) toList

    Console println s"file is ${lines.length} lines long"

    create(lines) 
  }

  def create(words: List[String]): Core.Index ={

    val data = for(word <- words par;
      score = SearchUtils.score(word);
      substring <- SearchUtils.genSubstrings(word)) yield (substring, score, word) 
      val emptySet = SortedSet.empty[(Core.Score,Core.Word)](orderByDescendingScore)
      val emptyMap = Map[Core.SubString,SortedSet[(Core.Score,Core.Word)]]().withDefaultValue(emptySet).asInstanceOf[Core.Index]
    data.foldLeft(emptyMap){(map,entry) => { map.updated(entry._1: Core.SubString,  map(entry._1) + Pair(entry._2, entry._3)) }}
  }

}
