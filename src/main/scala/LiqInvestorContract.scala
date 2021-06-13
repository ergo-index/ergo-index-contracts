import org.ergoplatform.playground.{Box, R5, Transaction}
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

object LiqInvestorContract {
  def run(blockchainSim: BlockchainSimulation): Unit = {
    ///////////////////////////////////////////////////////////////////////////////////
    // Liquidate Investor Transaction //
    ///////////////////////////////////////////////////////////////////////////////////
    // Every created "liqInvestor" UTXO has 1 input box (a random UTXO from the fundFounder) and produces 2 output boxes (one being investor N's fund box & the other the recursed pooled-fund box)

    // liqInvestorScript will prevent the pooled fund box from being spent unless the following are true:
    //     1) Ring signature provided
    //     2) new Nav = old NAV - oldInvestor's balance
    //     3) new investor array length = old investor array length - 1
    val liqInvestorScript =
    s"""
  {
  1
  }
  """.stripMargin

    val liqInvestorContract = ErgoScriptCompiler.compile(Map(), liqInvestorScript)

  }
}