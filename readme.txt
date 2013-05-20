Scrabble Suggester - Design Doc and Empirical Results

Daniel Schobel
dschobel@gmail.com

Attribution: All source code packaged herein is my own work except for two methods. kryo_deserialize and kryo_serialize (in SerializationUtils.scala) are two snippets of code I lifted from a StackOverflow answer on how to make the kryo library interop with scala's immutable collections. Their purpose is to serialize/deserialize my index and I take no credit for them.


Design Goals
===================================

To meet the goal of supporting fast substring queries I investigated creating an inverted index keyed by the possible substrings of every word in the scrabble dictionary.  

This decision to build one master index will relegate my solution to client/server architectures since loading large indexes into memory makes fast client start up times significantly more complicated. 

I think this is a reasonable decision given the context of a scrabble game where the index will presumably be queried repeatedly over the course of a long-lived game (or many such games if used in hosted contexts). If client startup time is a concern, we could generate one data file per entry in our index, effectively sharding the data. This means that if the query were 'cat', we'd only load the relevant data file.

For the sake of simplicity, I instead chose to build a monolithic index but the application pays the costs on load times (see discussion in results), but again, given the use-case of a long-lived game I think the decision is justifiable.



The index will be built as follows; given the word 'cat', I created a map with six keys: 'c','ca','cat','a','at','t'.

The performance rationale for the inverted index is that given any substring query for which 'cat' would be a possible answer, the index gives us O(1) access to the list* containing 'cat' (along with all the other matches for that substring). 

The obvious concern is that this approach will generate an asymptotically unpleasant number of entries in our index.

So let's estimate the potential keyspace.


*not really a list


Keyspace Estimate
===========================

Let's estimate the key-space as a function of the number of characters we're indexing.

Given a word of length n, we produce n distinct substrings for the first character of the word. For the second character, n-1 more substrings. For the third, n-2, etc. 

If we reverse the order of the expansion, we see that this is just the sum from 1 to n AKA the value (n(n+1) / 2)

In other words, we face a quadratic explosion of potential keys. 

How bad is this? 

Well the word list has approximately 10^6 characters which means we could potentially have on the order of 10^12 keys in our index. 

This is massive amount of data and would ruin the practicality of this solution, but when we remember that we're limited to a 26 character alphabet (assuming the English alphabet) and that we're not dealing with uniformly random data but English words which have regular patterns (and therefore recurring substrings) we can plausibly hope that the patterns in the data will lead to a substantially smaller keyspace in practice. 

Since we don't need to prove properties for a general word list but rather for a static and known word list we can punt on the combinatorics and determine the number empirically. In practice I found the full scrabble word list happens to produce a very tractable 558k distinct substrings.

Knowing that a full-index for all conceivable substrings is in play, I had to choose a data structure to maintain the ordered set of words (ordered by scrabble score) which correspond to a substring. This is another potential bottleneck of the index, particularly for the shorter substrings such as the single character entry 'a' which matches 56k of the 113k words in the list. 

I needed a data-structure which maintained ordering over the dataset and supported fast insert and in-order traversal operations. This means either a skip-list or balanced binary search tree. Either one will give you O(log(n)) inserts and O(n) traversals, while maintaining the order invariant.
This means that even for the biggest sets in our index, those corresponding to single characters, we can still insert in a reasonable amount of time. 

For example, in the case of the 'a' entry with its 56k values, we have to visit 16 nodes for a further insert.

Results
======================================================
setup: 2011 MBA with SSD and 4GB of memory


Memory/Storage:
Given the choices above, I produced an index which takes 650MB of heap space and serializes to 83MB on disk. 


Indexing:
Practically, I was able to index the data-set reliably in the 10 - 20 second time range. 

Asymptotically, the approach of generating all substrings is terrifying (see discussion above) but the features of this particular data-set let us get away with it. 
Larger alphabets and words which consist of uniformly random characters would quickly become unmanageable. This solution is decidedly not a general one.

Serialization:
Serializing the index was an unexpected bottle-neck. Using Java's built in object serializer it would take approximately a minute to serialize the index data to disk and about the same amount of time to deserialize; while producing an index which was about 40% larger on disk than that produced by kryo.
I switched to the kryo library to handle serialization and the time dropped by an order of magnitude and now takes about 10 seconds.

Querying: 
Since we have the full index in memory this is extremely fast. Asymptotically, I can produce results in time linear on the number of desired results (bounded by the number of available results). 
Sample timing results are below, but on my test setup I regularly saw in excess of 600,000 queries per second.

Client Launch:
As mentioned in the introduction, this solution is definitely in client-server territory since not only is reading 83MB on client launch going to be slow, but rehydrating 650MB of object data is going to be slow as well. This means that time to run the 'scrabble-suggester' command is going to be dominated by JVM startup time and deserializing the index.  For example; the sample query run below below takes 23 seconds of wall time, only 5ms of which is the actual query time. As was mentioned in the introduction, if client start times are a concern, sharding the data is a viable alternative.


Sample runs using the full scrabble word list: 

Indexer:
➜  time ./scrabble-indexer words.txt words.bin

    word list will be read from words.txt
    index will be written to words.bin
    creating index... 
    JVM memory usage after creating index: 779616256 bytes
    Index created with 558900 keys in 19 seconds
    Serializing index...
    done, kryo took 10 seconds to serialize
    ./scrabble-indexer words.txt words.bin  57.84s user 0.83s system 191% cpu 30.705 total

➜  du -sh words.bin 
    83M    words.bin



Suggester:

➜ time ./scrabble-suggester words.bin aba 20
    Deserialized index with 558900 keys
    kryo took 10 seconds to deserialize index
    Query completed in 5 ms
    163 total results, top 20 are:
                    (27,zabajones)
                    (26,zabajone)
                    (20,maccabaws)
                    (20,usquabaes)
                    (20,zabaiones)
                    (19,djellabas)
                    (19,maccabaw)
                    (19,usquabae)
                    (19,zabaione)
                    (18,djellaba)
                    (17,abandonments)
                    (17,calabashes)
                    (17,kabakas)
                    (16,abandonment)
                    (16,abaxial)
                    (16,abaxile)
                    (16,contrabands)
                    (16,hullabaloos)
                    (16,kabaka)
                    (16,kabayas)
./scrabble-suggester words.bin aba 20  23.60s user 0.57s system 211% cpu 11.410 total




QPS Bench:

➜  ./scrabble-bench words.bin 10000000 3
    QPS tests
    rehydrating index
    Deserialized index with 558900 keys
    kryo took 10 seconds to deserialize index
    choosing at random from 313 potential queries
    running 10000000 sequential queries... done
    average query size (character length): 4.6520423
    total query time (seconds): 14.899 s
    qps: 671185.98563662
