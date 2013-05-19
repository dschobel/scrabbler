package scrabbler

import scala.util.Try
//import scala.concurrent._
//import scala.concurrent.duration._
//import ExecutionContext.Implicits.global
import java.io.File
import collection.SortedSet

object Indexer {

  type Score = Int
  type SubString = String
  type Word = String
  type Index = Map[SubString,SortedSet[(Score,Word)]]

  val orderByDescendingScore = Ordering.by[(Score, Word),Score](- _._1)
  //collection.SortedSet((3:Score,"foo": Word),(1:Score,"a":Word),(2:Score,"b":Word))(orderByDescendingScore)

  def normalize(str: String) = str.toUpperCase.trim

  def create(file: File): Index ={
    
    val lines = (io.Source.fromFile(file) getLines) toList

    Console println s"file is ${lines.length} lines long"

    create(lines) 
  }

  def create(words: List[String]): Index ={

    val data = for(word <- words par;
      score = SearchUtils.score(word);
      substring <- SearchUtils.genSubstrings(word)) yield (substring, score, word) 
      val emptySet = SortedSet.empty[(Score,Word)](orderByDescendingScore)
      val emptyMap = Map[SubString,SortedSet[(Score,Word)]]().withDefaultValue(emptySet)
    data.foldLeft(emptyMap){(map,entry) => { map.updated(entry._1: SubString,  map(entry._1) + Pair(entry._2, entry._3)) }} 
  }

}
