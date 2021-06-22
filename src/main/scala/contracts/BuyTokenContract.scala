/*package contracts

import org.ergoplatform.ErgoBox
import org.ergoplatform.ErgoBox.R3
import org.ergoplatform.playground.{Box, R5, _}
import org.ergoplatform.playgroundenv.models.BlockchainSimulation
import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler
//import scala.collection.mutable.Map
import scala.language.postfixOps

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

    println("-----------")
    //fundFounder.printUnspentAssets()

    val fakeFundAddress = blockchainSim.newParty("fakeFundAddress").wallet.getAddress
    val address1 = blockchainSim.newParty("address1").wallet.getAddress
    val address2 = blockchainSim.newParty("address2").wallet.getAddress

    var immutableMap: Map[Address, Long] = Map(fundPoolBox.get(R3).get -> 100000000L, address1 -> 100000000L, address2 -> 200000000L)

    def getNAV(immutableMap: Map[Address, Long]): Long = {
      var nav: Long = 0L
      //immutableMap.keys.foreach { i ->
      for ((k, v) <- immutableMap)
        nav += v
      nav
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
}
 */