import org.ergoplatform.playground.{Box, R5, Transaction}
import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler

import org.ergoplatform.compiler.ErgoScalaCompiler._
import org.ergoplatform.playgroundenv.utils.ErgoScriptCompiler
import org.ergoplatform.playground._
import org.ergoplatform.Pay2SAddress
import sigmastate.eval.Extensions._
import scorex.crypto.hash.{Blake2b256}
//import scala.collection.mutable.Map
import scala.language.postfixOps
import sigmastate.eval.SigmaDsl

object JoinInvestorContract {
  def run(): Unit = {
    ///////////////////////////////////////////////////////////////////////////////////
    // Investor Join Transaction //
    ///////////////////////////////////////////////////////////////////////////////////
    // Every created "investorJoin Tx" box has 1 input box and produces 1 output boxes (both the pooled fund box)
    // Website would provide investor's public key & balance
    // TODO: Before doing investor join we need to create the index and send it to the simulated blockchain

    // joinInvestorScript will lock the pooled fund box from being spent unless the following are true:
    //     1) Ring signature provided *(maybe not needed if other investors' permission not need AND/OR new investor can't provide signature yet)
    //     2) new NAV = old NAV + newInvestor's balance
    //     3) new investor array length = old investor array + 1
    val joinInvestorScript =
    s"""
  {
  1
  }
  """.stripMargin

    val joinInvestorContract = ErgoScriptCompiler.compile(Map(), joinInvestorScript)

    /*val investorJoinsTransaction = Transaction (
          inputs       = fundAssetsBox,
          outputs      = List(fundAssetsBox),
          fee          = MinTxFee,
          sendChangeTo = address1
    )*/

  }
}