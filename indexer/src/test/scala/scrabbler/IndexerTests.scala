package scrabbler


import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter

class IndexerTests extends FunSpec with BeforeAndAfter {
  describe("create"){
    it("one word index is correct"){
      val index  = Indexer.create(List("cat") toIterator)

      assert(index.contains("c"))
      assert(index.contains("a"))
      assert(index.contains("t"))
      assert(index.contains("ca"))
      assert(index.contains("cat"))
      assert(index.contains("at"))
      assert(index.keys.size === 6)
    }

    it("two word index is correct"){
      val index  = Indexer.create(List("cat","bat") toIterator)

      assert(index.contains("c"))
      assert(index.contains("a"))
      assert(index.contains("t"))
      assert(index.contains("ca"))
      assert(index.contains("cat"))
      assert(index.contains("at"))
      assert(index.contains("b"))
      assert(index.contains("ba"))
      assert(index.contains("bat"))
      assert(index("a").size === 2)
      assert(index("at").size === 2)
      assert(index("t").size === 2)
      assert(index.keys.size === 9)
    }
  }
}
