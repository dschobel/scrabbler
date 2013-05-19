package scrabbler


import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter

class ScrabbleLibTests extends FunSpec with BeforeAndAfter {
  describe("getSubstrings function"){
    it("handles empty strings"){
      assert(SearchUtils.genSubstrings("").isEmpty)
    }

    it("is correct #1"){
      assert(SearchUtils.genSubstrings("a").contains("a"))
    }

    it("is correct #2"){
      val res = SearchUtils.genSubstrings("ab")
      assert(res.contains("a"))
      assert(res.contains("ab"))
      assert(res.contains("b"))
      assert(res.size === 3)
    }

    it("is correct #3"){
      val res = SearchUtils.genSubstrings("abc")
      assert(res.contains("a"))
      assert(res.contains("ab"))
      assert(res.contains("abc"))
      assert(res.contains("bc"))
      assert(res.contains("b"))
      assert(res.contains("c"))
      assert(res.size === 6)
    }
  }

  describe("score function") {
    it("scores a zero character word as zero") {
      assert(SearchUtils.score("") === 0)
    }

    it("returns the correct score for every character of the alphabet"){
      assert(SearchUtils.score("a") === 1)
      assert(SearchUtils.score("b") === 3)
      assert(SearchUtils.score("c") === 3)
      assert(SearchUtils.score("d") === 2)
      assert(SearchUtils.score("e") === 1)
      assert(SearchUtils.score("f") === 4)
      assert(SearchUtils.score("g") === 2)
      assert(SearchUtils.score("h") === 4)
      assert(SearchUtils.score("i") === 1)
      assert(SearchUtils.score("j") === 8)
      assert(SearchUtils.score("k") === 5)
      assert(SearchUtils.score("l") === 1)
      assert(SearchUtils.score("m") === 3)
      assert(SearchUtils.score("n") === 1)
      assert(SearchUtils.score("o") === 1)
      assert(SearchUtils.score("p") === 3)
      assert(SearchUtils.score("q") === 10)
      assert(SearchUtils.score("r") === 1)
      assert(SearchUtils.score("s") === 1)
      assert(SearchUtils.score("t") === 1)
      assert(SearchUtils.score("u") === 1)
      assert(SearchUtils.score("v") === 4)
      assert(SearchUtils.score("w") === 4)
      assert(SearchUtils.score("x") === 8)
      assert(SearchUtils.score("y") === 4)
      assert(SearchUtils.score("z") === 10)
    }

    it("returns the correct score for words with multiple characters"){
      assert(SearchUtils.score("cat") === 5)
      assert(SearchUtils.score("aaa") === 3)
    }
  }
}
