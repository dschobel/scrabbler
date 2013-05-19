package scrabbler

import java.io.File
import collection.SortedSet

object Indexer {


  val orderByDescendingScore = Ordering.by[(Core.Score, Core.Word),Core.Score](- _._1)

  def normalize(str: String) = str.toUpperCase.trim

  def create(file: File): Core.Index = create(io.Source.fromFile(file) getLines)
  

  def create(words: Iterator[String]): Core.Index ={

    val data = for(word <- words;
      score = SearchUtils.score(word);
      substring <- SearchUtils.genSubstrings(word)) yield (substring, score, word) 
      val emptySet = SortedSet.empty[(Core.Score,Core.Word)](orderByDescendingScore)
      val emptyMap = Map[Core.SubString,SortedSet[(Core.Score,Core.Word)]]().withDefaultValue(emptySet).asInstanceOf[Core.Index]
    data.foldLeft(emptyMap){(map,entry) => { map.updated(entry._1: Core.SubString,  map(entry._1) + Pair(entry._2, entry._3)) }}
  }

}
