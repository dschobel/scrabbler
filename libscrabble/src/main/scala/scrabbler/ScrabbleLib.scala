package scrabbler

object Core {
  type Score = Int
  type SubString = String
  type Word = String
  type Index = Map[SubString,collection.immutable.SortedSet[(Score,Word)]]
}

object SearchUtils {
  //type Substring = String

  lazy val CharScores = Map[Char,Core.Score]( 'A' -> 1, 'E' -> 1, 'I' -> 1, 'L' -> 1,
                                              'N' -> 1, 'O' -> 1, 'R' -> 1, 'S' -> 1,
                                              'T' -> 1, 'U' -> 1, 'D' -> 2, 'G' -> 2,
                                              'B' -> 3, 'C' -> 3, 'M' -> 3, 'P' -> 3,
                                              'F' -> 4, 'H' -> 4, 'V' -> 4, 'W' -> 4,
                                              'Y' -> 4, 'K' -> 5, 'J' -> 8, 'X' -> 8,
                                              'Q' -> 10, 'Z' -> 10)


  def score(str: String) = str.foldLeft(0){(acc,c) => acc + CharScores(c.toUpper)}

  def genSubstrings(str: String) = {
    def aux(cs: List[Char], acc: List[List[Char]] = Nil): List[List[Char]] = cs match{
      case Nil      => acc
      case xs       => {
        val res = ((0 until xs.length) toList).map{ i => xs drop i }
        aux(cs.take(cs.length-1), res ++ acc)
      }
    }
    aux((str toList)).map{_.mkString}
  }    
}