/*import org.ergoplatform.playground.{Box, R5, Transaction}
import org.ergoplatform.{ErgoBox, ErgoBoxCandidate, Pay2SAddress}
import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler
import org.ergoplatform.compiler.ErgoScalaCompiler._
import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler
import org.ergoplatform.playground._
import org.ergoplatform.Pay2SAddress
import org.ergoplatform.playgroundenv.models.BlockchainSimulation
import sigmastate.eval.Extensions._
import scorex.crypto.hash.Blake2b256
//import scala.collection.mutable.Map
import scala.language.postfixOps
import sigmastate.eval.SigmaDsl

object BuyTokenContract {
  def run(blockchainSim: BlockchainSimulation, fundPoolBox: ErgoBox): Unit = {
    ///////////////////////////////////////////////////////////////////////////////////
    // Buy Token Transaction //
    ///////////////////////////////////////////////////////////////////////////////////
    // Every created "buy token" box has 1 input box (the pooled fund box) and produces 2 output boxes
    // (one being being a UTXO holding the newly-purchased token & the other being the recursed pooled-fund box)

    // buyTokenScript will lock the pooled fund box from being spent unless the following are true:
    //     1) Ring signature provided, and many, many more
    val buyTokenScript =
    s"""
  {
  1
  }
  """.stripMargin

    //val buyTokenContract = ErgoScriptCompiler.compile(Map("founderPk" -> fundFounderAddress.pubKey), buyTokenScript)
    val buyTokenContract = ErgoScriptCompiler.compile(Map(), buyTokenScript)


    val tokenPrice: Long = 10000000 / 2


    // This function will loop through pks, find the first investor that has enough funds to purchase the
    /*def  purchaseToken(tokenPrice: Long, pks: Map[org.ergoplatform.playgroundenv.models.Address, Long]) : scala.collection.immutable.Map[org.ergoplatform.playgroundenv.models.Address, Long] = {
    var tokensData: Map[org.ergoplatform.playgroundenv.models.Address, Long] = Map()
    pks.keys.foreach{ i =>
      print("Key = " + i)
      println(" Value = " + pks(i))
      //tokensData += i -> pks(i)
      //tokensData.map(kv => (kv._1,kv._2.toSet)).toMap
    }
    //var immMap: scala.collection.immutable.Map[org.ergoplatform.playgroundenv.models.Address, Long] = Map())
      //printf("key: %s, value: %s\n", k, v)
      //if(v >= tokenPrice)
        var tokensData1: Map[org.ergoplatform.playgroundenv.models.Address, Long] = Map(fundFounderAddress -> fundFounderBal, address1 -> 10000000L, address2 -> 20000000L)
   return tokensData1
}*/

    println("-----------")
    //fundFounder.printUnspentAssets()

    var immutableMap: Map[Address, Long] = Map(fundPoolBox.additionalRegisters.get(4). -> fundFounderBal, address1 -> 100000000L, address2 -> 200000000L)

    def getNAV(immutableMap: Map[Address, Long]): Long = {
      val nav: Long = 0L
      val i = immutableMap.toIterator
      //immutableMap.keys.foreach { i ->
      for ((k, v) <- immutableMap)
        nav += immutableMap.get(k)
      return nav
    }

    val nav = getNAV(immutableMap)

    val buyOutput = Box(
      value = nav - tokenPrice,
      registers = Map(R5 -> immutableMap),
      script = buyTokenContract // originally had as sellTokenContract
    )

    /*val buyTokenTransaction = Transaction(
      inputs = fundFounder.selectUnspentBoxes(toSpend = fundFounderBal),
      outputs = List(buyOutput),
      fee = 1000000L,
      sendChangeTo = fundFounder.wallet.getAddress
    )*/

    //val buyTokenTransactionSigned = fundFounder.wallet.sign(buyTokenTransaction)

    // Submit the tx to the simulated blockchain
    //blockchainSim.send(buyTokenTransactionSigned)
    //fundFounder.printUnspentAssets()
    println("-----------")

  }
}*/