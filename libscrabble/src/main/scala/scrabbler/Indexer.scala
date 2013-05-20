package scrabbler

import java.io.File
import collection.SortedSet

object Indexer {


  val orderByDescendingScore = Ordering[(Core.Score, Core.Word)].on((x: Pair[Core.Score,Core.Word]) => (-x._1, x._2))

  def normalize(str: String) = str.toUpperCase.trim

  def create(file: File): Core.Index = create(io.Source.fromFile(file) getLines)
  

  def create(words: Iterator[String]): Core.Index ={

    val data = for(word <- words;
      score = SearchUtils.score(word);
      substring <- SearchUtils.genSubstrings(word)) yield (substring, score, word) 
      val emptySet = SortedSet.empty[(Core.Score,Core.Word)](orderByDescendingScore)
      val emptyMap = Map[Core.SubString,SortedSet[(Core.Score,Core.Word)]]().withDefaultValue(emptySet).asInstanceOf[Core.Index]
    data.foldLeft(emptyMap){(map,entry) => { map + ((entry._1, map(entry._1) + Pair(entry._2, entry._3))) }}
  }

}
