package contracts

import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler
import org.ergoplatform.playground._
import org.ergoplatform.ErgoBox

import scala.language.postfixOps
import org.ergoplatform.playgroundenv.models.BlockchainSimulation

/**
 * Transaction to create a new index or mutual fund. <br /><br />
 * <ul>
 *   <li>
 *     INPUTS(0)-INPUTS(N): Any number of UTXOs guarded by the fund creator's public key
 *   </li>
 *   <li>
 *     OUTPUTS(0): The core state UTXO with portfolio configuration information populated in its registers, as
 *     well as an initial investment for the fund
 *   </li>
 *   <li>
 *     OUTPUTS(1)-OUTPUTS(N): Any change UTXOs for the fund owner
 *   </li>
 * </ul>
 */
object CreateFundContract {

  /**
   * Simulates a fund manager creating an index or mutual fund.
   * @param fundFounder the person who is creating the fund. Only this person will be able to modify its portfolio
   * @param blockchainSim the simulation to run in
   * @return the core state UTXO
   */
  def run(fundFounder: Party, blockchainSim: BlockchainSimulation): ErgoBox = {
    println("==================================================")
    println("=== RUNNING TX: CREATE INDEX ===")
    println("==================================================")

    val fundFounderBal = 200000000000L
    val fundFounderAddress = fundFounder.wallet.getAddress
    fundFounder.generateUnspentBoxes(toSpend = fundFounderBal) // generating unspent UTXO boxes

    // This is a placeholder for the script guarding the core state UTXO.
    // See CoreStateGuard.scala for pseudocode of what it would actually look like
    val fundingScript =
      s"""
          {
            1
          }
          """.stripMargin
    val fundingContract = ErgoScriptCompiler.compile(Map(), fundingScript)
    val fundAssetsBox = Box(value = fundFounderBal - MinTxFee,
      // See Registers.scala for information on what data goes into each register
      //registers: R4 -> nft, R5 -> mapOfInvestorPksToAmtInvested, R6 -> tokensData, R7 -> tokenBoxHashes, R8 -> guardScripts
      script = fundingContract
    )

    val createIndexTx = Transaction(
      inputs = fundFounder.selectUnspentBoxes(toSpend = fundFounderBal - MinTxFee),
      outputs = List(fundAssetsBox),
      fee = MinTxFee,
      sendChangeTo = fundFounderAddress
    )

    val createIndexTxSigned = fundFounder.wallet.sign(createIndexTx)
    blockchainSim.send(createIndexTxSigned)
    fundFounder.printUnspentAssets()

    createIndexTxSigned.outputs(0)
  }
}
