package contracts

import org.ergoplatform.ErgoLikeTransaction
import org.ergoplatform.playground._
import org.ergoplatform.playgroundenv.models.BlockchainSimulation
import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler

import scala.language.postfixOps

object CreateFundContract {

  /**
   * Simulates a fund manager creating an index or mutual fund.
   * @param fundFounder the person who is creating the fund. Only this person will be able to modify its portfolio
   * @param blockchainSim the simulation to run in
   * @return the core state UTXO
   */
  def run(fundFounder: Party, blockchainSim: BlockchainSimulation): ErgoLikeTransaction = { // Unit should = ErgoBox
    println("==================================================")
    println("=== RUNNING TX: CREATE INDEX ===")
    println("==================================================")

    val fundFounderBal = 1000000000L
    val fundFounderAddress = fundFounder.wallet.getAddress
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
  if (OUTPUTS(0).propositionBytes == pk.propBytes) {
    true
  } else { false }
  }
""".stripMargin
    val fundingContract = ErgoScriptCompiler.compile(Map("pk" -> fundFounderAddress.pubKey), fundingScript)
    val fundAssetsBox = Box(
      value = fundFounderBal - MinTxFee,
      registers = Map(R4 -> 1, R5 -> fundFounder.wallet.getAddress.pubKey), // R4 -> nft, R5 -> pks, R6 -> tokensData, R7 -> utxos, R8 -> guardScripts
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
    blockchainSim.send(createIndexTxSigned)
    fundFounder.printUnspentAssets()

    println(createIndexTxSigned.outputs(0))
    println("ENDING CREATE INDEX TX")
    //println(createIndexTxSigned.outputs(0).additionalRegisters)
    createIndexTxSigned
  }
}