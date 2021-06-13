package registers

import org.ergoplatform.playground.Coll
import sigmastate.verified.Coll

/**
 * Utility functions for serializing and deserializing information to store in registers.
 * Since registers can only have types like Byte, Coll, and Int, the utility functions here
 * allow using higher level data structures like Maps and serializing/deserializing them to
 * write/read them in registers.
 */
object RegisterUtil {
  def serializeMap(map: Map[AnyRef, AnyRef]): Coll[Array[Byte]] = {
    Coll()
  }

  def deserializeMap(coll: Coll[Array[Byte]]): Map[AnyRef, AnyRef] = {
    Map()
  }
}
