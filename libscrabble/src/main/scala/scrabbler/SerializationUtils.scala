package scrabbler

import java.io.File

object SerializationUtils {

  /**
   * serialize an index to disk using java's built in serializers
   * @param index the index file to serialize
   * @param destination the destination file
   */
  def serialize(index: Core.Index, destination: File): Unit = {
    import java.io.{FileOutputStream,ObjectOutputStream}
    import scala.util.Try

    assert(destination.canWrite)
    val start = System.currentTimeMillis
    for (fos <- Try(new FileOutputStream(destination)); 
           o <- Try(new ObjectOutputStream(fos))){ o.writeObject(index) }

    Console println s"Serialized index using java serializer in ${(System.currentTimeMillis - start) / 1000} seconds"
  }

  /**
   * deserialize an index from disk
   * @param file the file containing the serialized index
   * @return the hydrated Index object
   */
  def deserialize(file: File): Core.Index = {
    import java.io.{ObjectInputStream,FileInputStream}
    val in = new FileInputStream(file)
    val reader = new ObjectInputStream(in)
    reader.readObject().asInstanceOf[Core.Index]
  }


  /**
   * deserialize an index object using the kryo format
   * @param source
   * @return the rehydrated index object
   */
  def kryo_deserialize(source: File): Core.Index ={
    import com.esotericsoftware.kryo.Kryo
    import com.esotericsoftware.kryo.io.Input
    import com.romix.scala.serialization.kryo._
    import org.objenesis.strategy.StdInstantiatorStrategy
    import java.io.{FileInputStream, BufferedInputStream}

    val kryo = new Kryo

    kryo.setRegistrationRequired(false)
    kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    kryo.register(classOf[scala.collection.immutable.$colon$colon[_]],60)
    kryo.register(classOf[scala.collection.immutable.Nil$],61)
    kryo.addDefaultSerializer(classOf[scala.Enumeration#Value], classOf[EnumerationSerializer])

    val start = System.currentTimeMillis
    val ifile = new FileInputStream(source)
    val input = new Input(new BufferedInputStream(ifile))
    val deserialized = kryo.readClassAndObject(input).asInstanceOf[Core.Index]
    input.close() 
    Console println "Deserialized index with " + deserialized.keys.size + " keys"
    Console println s"kryo took ${(System.currentTimeMillis - start) / 1000} seconds to deserialize"
    deserialized
  }

  /**
   * serialize an index object using the kryo format
   * @param index the object to serialize
   * @param destination the destination file
   */
  def kryo_serialize(index: Core.Index, destination: File): Unit = {
    import com.esotericsoftware.kryo.Kryo
    import com.esotericsoftware.kryo.io.Output
    import com.romix.scala.serialization.kryo._
    import org.objenesis.strategy.StdInstantiatorStrategy
    import java.io.{FileOutputStream, BufferedOutputStream}

    val kryo = new Kryo

    kryo.setRegistrationRequired(false)
    kryo.register(classOf[scala.collection.immutable.$colon$colon[_]],60)
    kryo.register(classOf[scala.collection.immutable.Nil$],61)
    kryo.addDefaultSerializer(classOf[scala.Enumeration#Value], classOf[EnumerationSerializer])

    val serStart = System.currentTimeMillis
    val ofile = new FileOutputStream(destination)
    val output2 = new BufferedOutputStream(ofile)
    val output = new Output(output2)
    kryo.writeClassAndObject(output, index)
    output.close()
    Console println s"done, kryo took ${(System.currentTimeMillis - serStart) / 1000} seconds to serialize"
  }
}

