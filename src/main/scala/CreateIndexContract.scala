import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler
import org.ergoplatform.playground._
import org.ergoplatform.{ErgoBox, ErgoBoxCandidate, Pay2SAddress}

import scala.language.postfixOps
import org.ergoplatform.playgroundenv.models.BlockchainSimulation
import sigmastate.eval.SigmaDsl

object CreateIndexContract {
  def run(blockchainSim: BlockchainSimulation): ErgoBox = {
    println("STARTING CREATE INDEX TX")

    val fundFounder = blockchainSim.newParty("fundFounder") // fundFounder's wallet would, in theory, be initialized before the fund's creation
    val fundFounderBal = 200000000000L
    val fundFounderAddress = fundFounder.wallet.getAddress
    val rawA = SigmaDsl.GroupElement(fundFounderAddress.proveDlog.value)
    fundFounder.generateUnspentBoxes(toSpend = fundFounderBal) // generating unspent UTXO boxes

    ///////////////////////////////////////////////////////////////////////////////////
    // Create Index Transaction //
    ///////////////////////////////////////////////////////////////////////////////////
    // Every created "create index" UTXO has 1 input box (a random UTXO from the fundFounder) and produces 1 output box (fundAssetsBox)

    // This transaction creates the uniquely-identifiable fund, loads it with the founder's funds and sends it to the blockchain!

    // fundingScript will lock the pooled fund box from being spent unless the following are true:
    //     1) Ring signature provided
    //     2) The guard script held in Output(1) isDefined in R8
    //     3) That the investor's funds have already been subtracted/added in the Output by comparing the investor's balance in both the input & output
    //     4) If the output's guard script == "buy" then Output(1).value == (NAV / R6["Token"].targetNAV) * 100
    //     5) If the output's guard script == "buy" || "sell" then:
    //        a) Check that the target buy/sell price is within 1% of the target buy/sell price
    val x = 4
    val fundingScript =
      s"""
  {
  1
  }
""".stripMargin
    val fundingContract = ErgoScriptCompiler.compile(Map(), fundingScript)
    val fundAssetsBox = Box(value = fundFounderBal - MinTxFee,
      register = R4 -> 1, // R4 -> nft, R5 -> pks, R6 -> tokensData, R7 -> utxos, R8 -> guardScripts
      script = fundingContract
    )

    println(fundAssetsBox)

    val createIndexTx = Transaction(
      inputs = fundFounder.selectUnspentBoxes(toSpend = fundFounderBal - MinTxFee),
      outputs = List(fundAssetsBox),
      fee = MinTxFee,
      sendChangeTo = fundFounderAddress
    )

    val createIndexTxSigned = fundFounder.wallet.sign(createIndexTx)
    //println(createIndexTxSigned.outputs(0))
    blockchainSim.send(createIndexTxSigned)
    fundFounder.printUnspentAssets()
    //println(fundAssetsBox.get(R4))

    println(createIndexTxSigned.outputs(0))
    println("ENDING CREATE INDEX TX")
    println(createIndexTxSigned.outputs(0).additionalRegisters)
    createIndexTxSigned.outputs(0)
  }
}